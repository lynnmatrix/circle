package com.jadenine.circle.ui.scanner;

import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jadenine.circle.R;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linym on 6/13/15.
 */
public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {

    private List<Pair<String, String>> apList = Collections.emptyList();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifi,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(apList.get(position));
    }

    @Override
    public int getItemCount() {
        return apList.size();
    }

    public void setApList(List<Pair<String, String>> aps) {
        apList = aps;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        @InjectView(R.id.ap_ssid)
        TextView ssidView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        public void bind(Pair<String, String> ap) {
            ssidView.setText(ap.first );
        }
    }
}
