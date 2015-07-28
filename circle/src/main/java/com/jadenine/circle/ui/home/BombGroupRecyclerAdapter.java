package com.jadenine.circle.ui.home;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.jadenine.circle.ui.SectionedRecyclerViewAdapter;

/**
 * Created by linym on 7/28/15.
 */
public class BombGroupRecyclerAdapter extends SectionedRecyclerViewAdapter {
    public BombGroupRecyclerAdapter(RecyclerView.Adapter dataAdapter) {
        super(dataAdapter);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateSectionFooterViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    protected void onBindSectionFooter(int position, Section section) {

    }
}
