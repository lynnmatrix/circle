package com.jadenine.circle.ui;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by linym on 7/28/15.
 */
public abstract class SectionedRecyclerViewAdapter extends RecyclerView.Adapter {
    private static final int TYPE_SECTION_HEADER = 0;
    private static final int TYPE_SECTION_FOOTER = 1;
    private static final int SECTION_TYPE_COUNT = 2;

    private final RecyclerView.Adapter dataAdapter;
    private SparseArray<Section> sections = new SparseArray<>();

    public SectionedRecyclerViewAdapter(RecyclerView.Adapter dataAdapter) {
        this.dataAdapter = dataAdapter;

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
            onBindSectionFooter(position, sections.get(position));
        } else {
            dataAdapter.onBindViewHolder(holder, sectionedPositionToListPosition(position));
        }
    }

    protected abstract void onBindSectionFooter(int position, Section section);

    @Override
    public int getItemCount() {
        return dataAdapter.getItemCount() + sections.size();
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


    public void setSections(Section[] sections) {
        Arrays.sort(sections, new Comparator<Section>() {
            @Override
            public int compare(Section lhs, Section rhs) {
                return lhs.firstPosition - rhs.firstPosition;
            }
        });

        int offset = 0;
        SparseArray<Section> tmpSections = new SparseArray<>(sections.length);
        for(Section section : sections) {
            section.sectionedPosition = section.firstPosition + offset;
            tmpSections.append(section.sectionedPosition, section);
        }

        this.sections = tmpSections;

        notifyDataSetChanged();
    }

    private int sectionedPositionToListPosition(int sectionedPosition) {
        int position = RecyclerView.NO_POSITION;
        if(isSectionFooter(sectionedPosition)) {
           return position;
        }

        int offset = 0;
        for (int i = 0;i< sections.size(); i++) {
            if(sections.valueAt(i).sectionedPosition > sectionedPosition){
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

    public static class Section<T> {
        int firstPosition;
        int sectionedPosition;
        T data;

        public Section(int firstPosition, T data) {
            this.firstPosition = firstPosition;
            this.data = data;
        }
    }
}
