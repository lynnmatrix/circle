package com.jadenine.circle.app;

import android.app.Notification;
import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Circle;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

import timber.log.Timber;

/**
 * Created by linym on 8/25/15.
 */
class CircleUmengMessageHandler extends UmengMessageHandler {
    public static final String CUSTOM_NOTIFICATION_TYPE_TOPIC = "topic";
    public static final String CUSTOM_NOTIFICATION_TYPE_CHAT = "chat";

    private final Gson gson;
    private final Account account;

    public CircleUmengMessageHandler(Account account, Gson gson) {
        this.account = account;
        this.gson = gson;
    }

    @Override
    public Notification getNotification(Context context, UMessage uMessage) {
        uMessage.title = context.getResources().getString(R.string.notification_title_topic);
        if(!TextUtils.isEmpty(uMessage.custom)) {
            try {
                CustomNotificationType customNotification = gson.fromJson(uMessage.custom, CustomNotificationType.class);

                if (CUSTOM_NOTIFICATION_TYPE_TOPIC.equalsIgnoreCase(customNotification.type)) {
                    Bomb bomb = gson.fromJson(uMessage.custom, CustomNotificationDataTopic.class).data;
                    try {
                        updateCircleUnRead(bomb);
                    } catch (IllegalStateException ignore) {
                    }
                    Circle circle = account.getCircle(bomb.getCircle());
                    if(null != circle) {
                        uMessage.title = circle.getName();
                    } else {
                        Timber.w("No circle found with id %s", bomb.getCircle());
                    }
                } else if(CUSTOM_NOTIFICATION_TYPE_CHAT.equalsIgnoreCase(customNotification.type)) {
                    uMessage.title = context.getResources().getString(R.string.notification_title_message);
                    DirectMessageEntity chatMessage = gson.fromJson(uMessage.custom, CustomNotificationDataChat.class).data;
                    updateChatUnread(chatMessage);
                }
            } catch (JsonSyntaxException e) {
                Timber.e(e, uMessage.custom);
            } catch (Throwable t) {
                Timber.e(t, "wtf");
            }
        }

        return super.getNotification(context, uMessage);
    }

    @Override
    public void dealWithCustomMessage(final Context context, final UMessage msg) {
        Timber.d("dealWithCustomMessage " + msg.custom);
    }

    private void updateCircleUnRead(Bomb bomb) {
        Circle circle = account.getCircle(bomb.getCircle());
        if(null == circle){
            Timber.w("Cant find circle %s in NotificationService", bomb.getCircle());
            return;
        }
        Group<Bomb> topic = circle.getTopic(bomb.getGroupId());
        Long lastRead = null;
        if(null != topic) {
            Bomb latestBomb = topic.getLatest();
            lastRead = latestBomb.getId();
        }
        if(null != circle && (null == lastRead || bomb.getId() <
                lastRead)) {
            circle.setHasUnread(true);
        }
    }

    private void updateChatUnread(DirectMessageEntity chatMessage) {
        Group<DirectMessageEntity> chat = account.getChat(chatMessage.getCircle(), Long
                        .valueOf(chatMessage.getTopicId()), chatMessage.getRootUser(),
                chatMessage.getGroupId());

        if(null == chat) {
            Timber.w("Cant find chat %s in NotificationService", chatMessage.getCircle());
            return;
        }
        Long lastRead = null;
        if(null != chat) {
            DirectMessageEntity latestMessage = chat.getLatest();
            lastRead = latestMessage.getId();
        }

        if(null == lastRead || chatMessage.getId() < lastRead) {
            account.setHasUnreadChat(true);
        }
    }

    private static class CustomNotificationType {
        public String type;
    }
    private static class CustomNotificationDataTopic {
        public Bomb data;
    }
    private static class CustomNotificationDataChat {
        public DirectMessageEntity data;
    }

}
