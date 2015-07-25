package com.jadenine.circle.ui.menu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.UserAp;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by linym on 7/22/15.
 */
public class ApMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_HEADER = 0;
    private final int TYPE_AP = 1;
    private List<UserAp> aps = new ArrayList<>();
    private int selectedPosition = -1;

    @Override
    public int getItemViewType(int position) {
        int type;
        if(0 == position) {
            type = TYPE_HEADER;
        } else {
            type = TYPE_AP;
        }
        return type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case TYPE_HEADER:
                View header = LayoutInflater.from(parent.getContext()).inflate(R.layout
                                .drawer_header, parent,
                        false);
                viewHolder = new DrawerHeaderViewHolder(header);
                break;
            case TYPE_AP:
                View apView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .item_ap_menu, parent, false);
                viewHolder = new ApMenuItemViewHolder(apView);
                break;
            default:
                viewHolder = null;
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        boolean selected = selectedPosition == position;
        switch (holder.getItemViewType()) {
            case TYPE_HEADER:
                break;
            case TYPE_AP:
                ((ApMenuItemViewHolder)holder).bind(getAp(position));
                holder.itemView.setSelected(selected);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return aps.size() + 1;
    }

    public UserAp getAp(int position) {
        if(position <= 0) {
            throw new InvalidParameterException("Position must be larger than 0");
        }
        return aps.get(position - 1);
    }

    public void setUserAps(List<UserAp> userAps) {
        aps.clear();
        aps.addAll(userAps);
        notifyDataSetChanged();
    }
    public void setSelected(int position) {
        if(position > 0 && position < getItemCount()) {
            selectedPosition = position;
        }
        notifyDataSetChanged();
    }
}
