package com.jadenine.circle.ui.menu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Circle;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.ui.HomeActivity;
import com.jadenine.circle.ui.chat.MyChatPath;
import com.jadenine.circle.ui.topic.top.TopPath;
import com.jadenine.circle.ui.topic.user.MyTopicPath;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by linym on 7/22/15.
 */
class ApMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_HEADER = 0;
    private final int TYPE_AP = 1;
    private final int TYPE_MY_CHAT = 2;
    private final int TYPE_MY_TOPICS = 3;
    private final int TYPE_TOP_TOPICS = 4;

    public static final int NON_AP_ITEM_COUNT = 4;

    private List<Circle> circles = new ArrayList<>();
    private int selectedPosition = -1;

    private final Account account;

    @Inject @DaggerScope(HomeActivity.class)
    public ApMenuAdapter(Account account) {
        this.account = account;
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        if(0 == position) {
            type = TYPE_HEADER;
        } else if(1 == position) {
            type = TYPE_TOP_TOPICS;
        } else if(2 == position) {
            type = TYPE_MY_TOPICS;
        } else if(3 == position) {
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
            case TYPE_TOP_TOPICS:
            case TYPE_MY_TOPICS:
            case TYPE_MY_CHAT:
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .item_navigation_menu, parent, false);

                viewHolder = new ItemViewHolder(itemView);
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
            case TYPE_TOP_TOPICS:
                ((ItemViewHolder)holder).bind(R.drawable.ic_fire, R.string
                        .title_top_topics,false, new TopPath());
                break;
            case TYPE_MY_TOPICS:
                ((ItemViewHolder)holder).bind(R.drawable.ic_person_outline, R.string
                        .title_my_topic, account.hasUnreadChat(), new MyTopicPath());
                break;
            case TYPE_MY_CHAT:
                ((ItemViewHolder)holder).bind(R.drawable.checkbox_private, R.string
                        .title_private_chat, account.hasUnreadChat(), new MyChatPath());
                break;
            case TYPE_AP:
                ((ApMenuItemViewHolder)holder).bind(getCircle(position));
                holder.itemView.setSelected(selected);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return circles.size() + NON_AP_ITEM_COUNT;
    }

    public Circle getCircle(int position) {
        if(position < NON_AP_ITEM_COUNT ) {
            throw new InvalidParameterException("Position must be larger than " + NON_AP_ITEM_COUNT);
        }

        return circles.get(position - NON_AP_ITEM_COUNT);
    }

    public void setCircles(List<Circle> circles) {
        this.circles.clear();
        this.circles.addAll(circles);
        notifyDataSetChanged();
    }

    public void setSelected(int position) {
        if(position > 0 && position < getItemCount()) {
            selectedPosition = position;
        }
        notifyDataSetChanged();
    }

}
