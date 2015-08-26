package com.jadenine.circle.ui.chat.detail;

import android.support.annotation.Nullable;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.ScreenComponentFactory;
import com.jadenine.circle.ui.HomeComponent;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.common.flow.Layout;
import com.jadenine.common.mortar.ActivityOwner;

import dagger.Provides;
import flow.path.Path;

/**
 * Created by linym on 7/25/15.
 */
@Layout(R.layout.screen_chat)
public class ChatPath extends Path implements ScreenComponentFactory {
    private final String circle;
    private final Long topicId;
    private final String rootUser;
    private final String rootTo;
    private final Long rootMessageId;

    public ChatPath(String circle, Long topicId, String rootUser, String rootTo) {
        this(circle, topicId, rootUser, rootTo, null);
    }

    public ChatPath(String circle, Long topicId, String rootUser, String rootTo, Long
            rootMessageId) {
        this.circle = circle;
        this.topicId = topicId;
        this.rootUser = rootUser;
        this.rootTo = rootTo;
        this.rootMessageId = rootMessageId;
    }

    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerChatPath_Component.builder().homeComponent((HomeComponent) dependencies[0])
                .module(new Module()).build();
    }

    @DaggerScope(ChatPath.class)
    @dagger.Component(dependencies = HomeComponent.class, modules = Module.class)
    interface Component{
        void inject(ChatView chatView);
    }

    @dagger.Module
    class Module{

        @DaggerScope(ChatPath.class)
        @Provides
        @Nullable
        Group<DirectMessageEntity> provideChatGroup(Account account) {
            return account.getChat(circle, topicId, rootUser, rootMessageId);
        }

        @DaggerScope(ChatPath.class)
        @Provides
        ChatPresenter providePresenter(Account account, @Nullable Group<DirectMessageEntity>
                chatGroup,  AvatarBinder avatarBinder, ActivityOwner owner) {
            String from = rootUser;
            String to = rootTo;
            if(!account.getDeviceId().equals(rootUser)) {
                from = rootTo;
                to = rootUser;
            }
            return new ChatPresenter(account, circle, String.valueOf(topicId), from, to,
                    chatGroup, avatarBinder, owner);
        }
    }
}
