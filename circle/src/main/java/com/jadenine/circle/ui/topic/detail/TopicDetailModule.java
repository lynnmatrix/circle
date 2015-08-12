package com.jadenine.circle.ui.topic.detail;

import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.domain.UserAp;
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
    private final Long groupId;

    public TopicDetailModule(Long groupId) {
        this.groupId = groupId;
    }

    @Provides
    @DaggerScope(TopicDetailPresenter.class)
    UserAp provideUserAp(Account account, Group<Bomb> topic) {
        return account.getUserAp(topic.getRoot().getAp());
    }

    @DaggerScope(TopicDetailPresenter.class)
    @Provides
    TopicDetailPresenter providePresenter(Account account, UserAp userAp, Group<Bomb> bombGroup, AvatarBinder
            avatarBinder, ActivityOwner owner) {
        return new TopicDetailPresenter(account, userAp, bombGroup, avatarBinder, owner);
    }
}
