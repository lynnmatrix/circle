package com.jadenine.circle.ui.topic.detail;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.ScreenComponentFactory;
import com.jadenine.circle.ui.HomeComponent;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.common.flow.Layout;
import com.jadenine.common.mortar.ActivityOwner;
import com.raizlabs.android.dbflow.annotation.NotNull;

import dagger.Provides;
import flow.path.Path;

/**
 * Created by linym on 8/6/15.
 */
@Layout(R.layout.screen_bomb_group)
public class MyTopicDetailPath extends Path implements ScreenComponentFactory {
    private final Long groupId;

    public MyTopicDetailPath(@NotNull Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerMyTopicDetailPath_Component.builder().homeComponent((HomeComponent)
                dependencies[0])
                .module(new Module(groupId))
                .build();
    }

    @DaggerScope(MyTopicDetailPath.class)
    @dagger.Component(dependencies = HomeComponent.class, modules = Module.class)
    interface Component extends TopicDetailComponent{
    }

    @dagger.Module
    static class Module{
        private final Long groupId;

        public Module(Long groupId) {
            this.groupId = groupId;
        }

        @Provides
        @DaggerScope(MyTopicDetailPath.class)
        UserAp provideUserAp(Account account, Group<Bomb> topic){
            return account.getUserAp(topic.getRoot().getAp());
        }

        @Provides
        @DaggerScope(MyTopicDetailPath.class)
        Group<Bomb> provideBombGroup(Account account) {
            return account.getMyTopic(groupId);
        }

        @DaggerScope(MyTopicDetailPath.class)
        @Provides
        TopicDetailPresenter providePresenter(Account account, UserAp userAp, Group<Bomb> bombGroup, AvatarBinder
                avatarBinder, ActivityOwner owner) {
            return new TopicDetailPresenter(account, userAp, bombGroup, avatarBinder, owner);
        }
    }
}
