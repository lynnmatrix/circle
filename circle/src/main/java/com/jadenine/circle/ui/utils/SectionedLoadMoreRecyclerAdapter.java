package com.jadenine.circle.ui.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.TimelineRange;
import com.jadenine.circle.model.Identifiable;

/**
 * Created by linym onFooterClickListener 7/28/15.
 */
public class SectionedLoadMoreRecyclerAdapter<T extends Identifiable<Long>> extends
        SectionedRecyclerViewAdapter<T> {
    private OnFooterClickListener onFooterClickListener;

    public SectionedLoadMoreRecyclerAdapter(ItemAdapter<T> dataAdapter) {
        super(dataAdapter);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateSectionFooterViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer,
                parent, false);
        return new LoadMoreViewHolder(view);
    }

    @Override
    protected void onBindSectionFooter(RecyclerView.ViewHolder viewHolder, int position, final
                                       Section<T> section) {
        ((LoadMoreViewHolder)viewHolder).bind(section, onFooterClickListener);
    }

    public void setOnFooterClickListener(OnFooterClickListener listener) {
        onFooterClickListener = listener;
    }

    public interface OnFooterClickListener {
        void onFooterClicked(TimelineRange range, LoadMoreViewHolder loadMoreViewHolder);
    }
}
