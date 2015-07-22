package com.jadenine.circle.ui.menu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.UserAp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linym on 7/22/15.
 */
public class ApMenuAdapter extends RecyclerView.Adapter<ApMenuItemViewHolder> {
    private List<UserAp> aps = new ArrayList<>();

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public ApMenuItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ap_menu, parent, false);
        return new ApMenuItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ApMenuItemViewHolder holder, int position) {
        holder.bind(aps.get(position));
    }

    @Override
    public int getItemCount() {
        return aps.size();
    }

    public UserAp getAp(int position) {
        return aps.get(position);
    }

    public void setUserAps(List<UserAp> userAps) {
        aps.clear();;
        aps.addAll(userAps);
    }
}
