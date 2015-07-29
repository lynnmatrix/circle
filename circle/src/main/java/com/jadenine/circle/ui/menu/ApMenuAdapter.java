package com.jadenine.circle.ui.menu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.ui.chat.MyChatPath;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import flow.Flow;
import flow.History;

/**
 * Created by linym on 7/22/15.
 */
class ApMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_HEADER = 0;
    private final int TYPE_AP = 1;
    private final int TYPE_MY_CHAT = 2;
    public static final int NON_AP_ITEM_COUNT = 2;

    private List<UserAp> aps = new ArrayList<>();
    private int selectedPosition = -1;

    @Override
    public int getItemViewType(int position) {
        int type;
        if(0 == position) {
            type = TYPE_HEADER;
        } else if(1 == position) {
            type = TYPE_MY_CHAT;
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
            case TYPE_MY_CHAT:
                View myChatMenuItem = LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .item_navigation_menu, parent, false);

                viewHolder = new ItemViewHolder(myChatMenuItem);
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
            case TYPE_MY_CHAT:
                ((ItemViewHolder)holder).bind(R.drawable.checkbox_private, R.string.title_private_chat);
                break;
            case TYPE_AP:
                ((ApMenuItemViewHolder)holder).bind(getAp(position));
                holder.itemView.setSelected(selected);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return aps.size() + NON_AP_ITEM_COUNT;
    }

    public UserAp getAp(int position) {
        if(position < NON_AP_ITEM_COUNT ) {
            throw new InvalidParameterException("Position must be larger than " + NON_AP_ITEM_COUNT);
        }

        return aps.get(position - NON_AP_ITEM_COUNT);
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

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.icon)
        ImageView iconView;
        @InjectView(R.id.title)
        TextView titleView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        public void bind(int iconResId, int titleResId) {
            iconView.setImageResource(iconResId);
            titleView.setText(titleResId);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setSelected(true);
                    Context context = itemView.getContext();
                    History.Builder historyBuilder = Flow.get(context).getHistory().buildUpon();
                    historyBuilder.pop();
                    historyBuilder.push(new MyChatPath());

                    Flow.get(context).setHistory(historyBuilder.build(), Flow.Direction.REPLACE);
                }
            });
        }
    }
}
