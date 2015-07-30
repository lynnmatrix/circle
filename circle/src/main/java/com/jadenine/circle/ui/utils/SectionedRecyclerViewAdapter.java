package com.jadenine.circle.ui.utils;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.jadenine.circle.domain.Group;
import com.jadenine.circle.domain.TimelineRange;
import com.jadenine.circle.model.Identifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by linym on 7/28/15.
 */
public abstract class SectionedRecyclerViewAdapter<T extends Identifiable<Long>> extends
        RecyclerView.Adapter {

    private static final int TYPE_SECTION_HEADER = 0;
    private static final int TYPE_SECTION_FOOTER = 1;
    private static final int SECTION_TYPE_COUNT = 2;

    private final ItemAdapter<T> dataAdapter;
    private SparseArray<Section<T>> sections = new SparseArray<>();

    public SectionedRecyclerViewAdapter(ItemAdapter<T> dataAdapter) {
        this.dataAdapter = dataAdapter;

        setHasStableIds(true);

        dataAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                notifyItemRangeChanged(positionToSectionedPosition(positionStart), itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                notifyItemRangeInserted(positionToSectionedPosition(positionStart), itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                notifyItemRangeRemoved(positionToSectionedPosition(positionStart), itemCount);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if(isSectionFooter(position)) {
            return TYPE_SECTION_FOOTER;
        } else {
            return dataAdapter.getItemViewType(sectionedPositionToListPosition(position)) +
                    SECTION_TYPE_COUNT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        switch (viewType){
            case TYPE_SECTION_FOOTER:
                viewHolder = onCreateSectionFooterViewHolder(parent);
                break;
            default:
                viewHolder = dataAdapter.onCreateViewHolder(parent, viewType);
                break;
        }
        return viewHolder;
    }

    protected abstract RecyclerView.ViewHolder onCreateSectionFooterViewHolder(ViewGroup parent);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(isSectionFooter(position)) {
            onBindSectionFooter(holder, position, sections.get(position));
        } else {
            dataAdapter.onBindViewHolder(holder, sectionedPositionToListPosition(position));
        }
    }

    protected abstract void onBindSectionFooter(RecyclerView.ViewHolder viewHolder, int position,
                                                Section<T> section);

    @Override
    public int getItemCount() {
        int itemCount = dataAdapter.getItemCount() + sections.size();
        if(sections.size() > 0) {
            itemCount--;
        }
        return itemCount;
    }

    @Override
    public long getItemId(int position) {
        long itemId;
        if(isSectionFooter(position)) {
            itemId = Integer.MAX_VALUE - sections.indexOfKey(position);
        } else {
            itemId = dataAdapter.getItemId(sectionedPositionToListPosition(position));
        }
        return itemId;
    }

    public void setSections(List<TimelineRange<T>> ranges) {
        List<Group<T>> groups = new LinkedList<>();
        List<SectionedRecyclerViewAdapter.Section<T>> sections = new ArrayList<>(ranges.size());
        int offset = 0;
        int sectionOffset = 0;
        for(TimelineRange<T> range: ranges) {
            SectionedRecyclerViewAdapter.Section<T> section = new
                    SectionedRecyclerViewAdapter.Section<>(offset, range.getGroupCount(), range
                    .hasMore(), range);

            sections.add(sectionOffset++, section);

            offset += range.getGroupCount();

            groups.addAll(range.getAllGroups());
        }
        setSections(sections, groups);
    }

    public void setSections(List<Section<T>> sections, List<Group<T>> items) {
        Collections.sort(sections, new Comparator<Section>() {
            @Override
            public int compare(Section lhs, Section rhs) {
                return lhs.firstPosition - rhs.firstPosition;
            }
        });

        int offset = 0;
        SparseArray<Section<T>> tmpSections = new SparseArray<>(sections.size());
        for(Section section : sections) {
            section.sectionedPosition = section.firstPosition + offset;
            tmpSections.append(section.sectionedPosition + section.count, section);
        }

        this.sections = tmpSections;

        dataAdapter.setItems(items);

        notifyDataSetChanged();
    }

    private int sectionedPositionToListPosition(int sectionedPosition) {
        int position = RecyclerView.NO_POSITION;
        if(isSectionFooter(sectionedPosition)) {
           return position;
        }

        int offset = 0;
        for (int i = 0;i< sections.size(); i++) {
            Section section = sections.valueAt(i);
            if (section.sectionedPosition + section.count >= sectionedPosition) {
                break;
            }
            offset++;
        }

        return sectionedPosition - offset;
    }

    private int positionToSectionedPosition(int position) {
        int offset = 0;
        for(int i = 0; i< sections.size(); i++) {
            if(sections.valueAt(i).firstPosition > position) {
                break;
            }
            offset++;
        }
        return position + offset;
    }

    private boolean isSectionFooter(int position) {
        return null != sections.get(position);
    }

    public static class Section<S extends Identifiable<Long>> {
        int firstPosition;
        int count;
        int sectionedPosition;
        boolean hasMore;
        TimelineRange<S> data;

        public Section(int firstPosition, int count, boolean hasMore, TimelineRange<S> data) {
            this.firstPosition = firstPosition;
            this.count = count;
            this.hasMore = hasMore;
            this.data = data;
        }

        public int getFirstPosition(){
            return firstPosition;
        }

        public TimelineRange<S> getData(){
            return data;
        }

        public boolean hasMore() {
            return hasMore;
        }
    }

    public static abstract class ItemAdapter<E extends Identifiable<Long>> extends
            RecyclerView.Adapter{
        public abstract void setItems(List<Group<E>> items);
        public abstract Group<E> getItem(int position);
    }

}
