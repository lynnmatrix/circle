package com.jadenine.circle.ui.detail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jadenine.circle.R;
import com.jadenine.circle.model.entity.Bomb;

import java.util.Collections;
import java.util.List;

/**
 * Created by linym on 7/24/15.
 */
public class BombRecyclerAdapter extends RecyclerView.Adapter<BombItemViewHolder> {
    private List<Bomb> bombs = Collections.emptyList();
    @Override
    public BombItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,
                parent, false);
        BombItemViewHolder viewHolder = new BombItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BombItemViewHolder holder, int position) {
        holder.bind(bombs.get(position), position);
    }

    @Override
    public int getItemCount() {
        return bombs.size();
    }

    public void setBombs(List<Bomb> entities) {
        bombs = entities;
        notifyDataSetChanged();
    }
}
