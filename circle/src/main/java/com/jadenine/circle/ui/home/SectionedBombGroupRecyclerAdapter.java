package com.jadenine.circle.ui.home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.domain.TimelineRange;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.utils.SectionedRecyclerViewAdapter;

/**
 * Created by linym on 7/28/15.
 */
class SectionedBombGroupRecyclerAdapter extends
        SectionedRecyclerViewAdapter<TimelineRange<Bomb>, Group<Bomb>> {
    private OnFooterClickListener loadMoreListener;

    public SectionedBombGroupRecyclerAdapter(BombRecyclerAdapter dataAdapter) {
        super(dataAdapter);
        setHasStableIds(true);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateSectionFooterViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer,
                parent, false);
        return new LoadMoreViewHolder(view);
    }

    @Override
    protected void onBindSectionFooter(RecyclerView.ViewHolder viewHolder, int position, final
                                       Section<TimelineRange<Bomb>> section) {
        ((LoadMoreViewHolder)viewHolder).bind(section, loadMoreListener);
    }

    public void setLoadMoreListener(OnFooterClickListener listener) {
        loadMoreListener = listener;
    }

    public interface OnFooterClickListener {
        void onFooterClicked(TimelineRange range, LoadMoreViewHolder loadMoreViewHolder);
    }
}
