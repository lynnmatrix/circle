package com.jadenine.circle.ui.chat.detail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.utils.TimeFormatUtils;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linym on 7/27/15.
 */
class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{
    private final static int TYPE_CHAT_NORMAL = 0;
    private final static int TYPE__CHAT_SELF = 1;

    private final AvatarBinder avatarBinder;
    private List<DirectMessageEntity> chatMessages = Collections.emptyList();
    private String myUserId;

    @Inject
    @DaggerScope(ChatPath.class)
    public ChatAdapter(Account account, AvatarBinder avatarBinder) {
        this.myUserId = account.getDeviceId();
        this.avatarBinder = avatarBinder;
    }

    @Override
    public int getItemViewType(int position) {
        DirectMessageEntity messageEntity = chatMessages.get(position);
        if(messageEntity.getFrom().equals(myUserId)) {
            return TYPE__CHAT_SELF;
        } else {
            return TYPE_CHAT_NORMAL;
        }
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int itemLayout;
        if(TYPE_CHAT_NORMAL == viewType) {
            itemLayout = R.layout.item_chat;
        } else {
            itemLayout = R.layout.item_chat_self;
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        holder.bind(chatMessages.get(position), avatarBinder);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void setChatMessages(List<DirectMessageEntity> messages) {
        this.chatMessages = messages;
        notifyDataSetChanged();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.avatar)
        ImageView avatarView;
        @InjectView(R.id.content)
        TextView contentView;
        @InjectView(R.id.date)
        TextView timeView;

        public ChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        public void bind(DirectMessageEntity directMessageEntity, AvatarBinder avatarBinder) {
            avatarView.setImageResource(avatarBinder.getAvatar(directMessageEntity.getFrom(),
                    directMessageEntity.getTopicId()));
            contentView.setText(directMessageEntity.getContent());
            timeView.setText(TimeFormatUtils.getFormattedTime(directMessageEntity.getTimestamp()));
        }
    }
}
