package com.jadenine.circle.app;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Circle;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.HomeActivity;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import timber.log.Timber;

/**
 * Created by zhangsj on 2015/8/27.
 */
public class CircleUmengNotificationClickHandler extends UmengNotificationClickHandler {
    public static final String CUSTOM_NOTIFICATION_TYPE_TOPIC = "topic";
    public static final String CUSTOM_NOTIFICATION_TYPE_CHAT = "chat";

    private final Gson gson;
    private final Account account;

    CircleUmengNotificationClickHandler(Account account, Gson gson) {
        this.account = account;
        this.gson = gson;
    }

    @Override
    public void dealWithCustomAction(Context context, UMessage uMessage) {
        if(!TextUtils.isEmpty(uMessage.custom)) {
            try {
                CustomNotificationType customNotification = gson.fromJson(uMessage.custom, CustomNotificationType.class);

                if (CUSTOM_NOTIFICATION_TYPE_TOPIC.equalsIgnoreCase(customNotification.type)) {
                    Bomb bomb = gson.fromJson(uMessage.custom, CustomNotificationDataTopic.class).data;
                    Circle circle = account.getCircle(bomb.getCircle());
                    if(null != circle) {
                        context.startActivity(HomeActivity.getOpenCircleIntent(context, circle.getCircleId()));
                    } else {
                        Timber.w("dealWithCustomAction() - No circle found with id %s", bomb.getCircle());
                    }
                } else if(CUSTOM_NOTIFICATION_TYPE_CHAT.equalsIgnoreCase(customNotification.type)) {
                    context.startActivity(HomeActivity.getOpenChatIntent(context));
                }
            } catch (JsonSyntaxException e) {
                Timber.e(e, uMessage.custom);
            } catch (Throwable t) {
                Timber.e(t, "wtf");
            }
        }
    }

    private static class CustomNotificationType {
        public String type;
    }
    private static class CustomNotificationDataTopic {
        public Bomb data;
    }
}
