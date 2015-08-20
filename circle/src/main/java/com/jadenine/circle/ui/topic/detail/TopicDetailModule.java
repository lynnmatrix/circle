package com.jadenine.circle.ui.topic.detail;

import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Circle;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.common.mortar.ActivityOwner;

import dagger.Provides;

/**
 * Created by linym on 8/12/15.
 */
@dagger.Module
class TopicDetailModule {

    public TopicDetailModule() {
    }

    @Provides
    @DaggerScope(TopicDetailPresenter.class)
    Circle provideCircle(Account account, Group<Bomb> topic) {
        return account.getCircle(topic.getRoot().getCircle());
    }

    @DaggerScope(TopicDetailPresenter.class)
    @Provides
    TopicDetailPresenter providePresenter(Account account, Circle circle, Group<Bomb> bombGroup, AvatarBinder
            avatarBinder, ActivityOwner owner) {
        return new TopicDetailPresenter(account, circle, bombGroup, avatarBinder, owner);
    }
}
