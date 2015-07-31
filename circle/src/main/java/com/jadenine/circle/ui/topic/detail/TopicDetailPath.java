package com.jadenine.circle.ui.topic.detail;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.ScreenComponentFactory;
import com.jadenine.circle.ui.HomeComponent;
import com.jadenine.common.flow.Layout;
import com.raizlabs.android.dbflow.annotation.NotNull;

import dagger.Provides;
import flow.path.Path;

/**
 * Created by linym on 7/24/15.
 */
@Layout(R.layout.screen_bomb_group)
public class TopicDetailPath extends Path implements ScreenComponentFactory{
    private final String ap;
    private final Long groupId;

    public TopicDetailPath(@NotNull String ap, @NotNull Long groupId) {
        this.ap = ap;
        this.groupId = groupId;
    }

    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerTopicDetailPath_Component.builder().homeComponent((HomeComponent)
                dependencies[0])
                .module(new Module())
                .build();
    }

    @DaggerScope(TopicDetailPresenter.class)
    @dagger.Component(dependencies = HomeComponent.class, modules = Module.class)
    interface Component {
        void inject(TopicDetailView detailView);
    }

    @dagger.Module
    class Module{

        @Provides
        @DaggerScope(TopicDetailPresenter.class)
        UserAp provideUserAp(Account account){
            return account.getUserAp(ap);
        }

        @Provides
        @DaggerScope(TopicDetailPresenter.class)
        Group<Bomb> provideBombGroup(UserAp userAp) {
            return userAp.getBombGroup(groupId);
        }
    }
}