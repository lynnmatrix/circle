package com.jadenine.circle.ui.home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.model.entity.Bomb;

import java.util.Collections;
import java.util.List;

/**
 * Created by linym on 7/22/15.
 */
public class BombRecyclerAdapter extends RecyclerView.Adapter<BombGroupItemViewHolder>{
    private List<Group<Bomb>> bombGroups = Collections.emptyList();

    @Override
    public BombGroupItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bomb_group,
                parent, false);
        BombGroupItemViewHolder viewHolder = new BombGroupItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BombGroupItemViewHolder holder, int position) {
        holder.bind(bombGroups.get(position));
    }

    @Override
    public int getItemCount() {
        return bombGroups.size();
    }

    public void setBombGroups(List<Group<Bomb>> bombGroups) {
        this.bombGroups = bombGroups;
    }

    public Group<Bomb> getBombGroup(int position) {
        return bombGroups.get(position);
    }
}
