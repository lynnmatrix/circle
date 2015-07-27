package com.jadenine.circle.ui.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.ui.avatar.AvatarBinder;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linym on 7/27/15.
 */
public class MyChatsAdapter extends RecyclerView.Adapter<MyChatsAdapter.ItemMyChatViewHolder> {

    private List<Group<DirectMessageEntity>> chatGroups = Collections.emptyList();
    private final AvatarBinder avatarBinder;
    private final Account account;

    @Inject @DaggerScope(MyChatPath.class)
    public MyChatsAdapter(Account account, AvatarBinder avatarBinder){
        this.account = account;
        this.avatarBinder = avatarBinder;
    }

    @Override
    public ItemMyChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_group,
                parent, false);
        return new ItemMyChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemMyChatViewHolder holder, int position) {
        holder.bind(chatGroups.get(position), account, avatarBinder);
    }

    @Override
    public int getItemCount() {
        return chatGroups.size();
    }

    public void setChatGroups(List<Group<DirectMessageEntity>> chatGroups) {
        this.chatGroups = chatGroups;
        notifyDataSetChanged();
    }

    public Group<DirectMessageEntity> getChat(int position) {
        return chatGroups.get(position);
    }

    static class ItemMyChatViewHolder extends RecyclerView.ViewHolder{

        @InjectView(R.id.avatar)
        ImageView avatarView;

        @InjectView(R.id.ap)
        TextView apView;

        @InjectView(R.id.content)
        TextView contentView;

        public ItemMyChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        public void bind(Group<DirectMessageEntity> chat, Account account, AvatarBinder
                avatarBinder) {
            DirectMessageEntity lastMessage = chat.getLatest();
            if(null != lastMessage) {
                avatarView.setImageResource(avatarBinder.getAvatar(lastMessage.getFrom(),
                        lastMessage.getTopicId()));
                UserAp userAp = account.getUserAp(lastMessage.getAp());
                String apDes;
                if(null != userAp) {
                    apDes = userAp.getSSID();
                } else {
                    apDes = lastMessage.getAp();
                }
                apView.setText(apDes);
                contentView.setText(lastMessage.getContent());
            }
        }
    }
}
