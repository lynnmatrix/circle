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
import com.jadenine.circle.ui.topic.TopicListPath;
import com.jadenine.common.flow.Layout;
import com.jadenine.common.mortar.ActivityOwner;
import com.raizlabs.android.dbflow.annotation.NotNull;

import dagger.Provides;
import flow.path.Path;

/**
 * Created by linym on 7/24/15.
 */
@Layout(R.layout.screen_topic_detail)
public class TopicDetailPath extends Path implements ScreenComponentFactory{
    private final String ap;
    private final Long groupId;
    private final Path parentPath;

    public TopicDetailPath(@NotNull String ap, @NotNull Long groupId, Path parentPath) {
        this.ap = ap;
        this.groupId = groupId;
        this.parentPath = parentPath;
    }

    @Override
    protected void build(Builder builder) {
        super.build(builder);
        builder.append(parentPath);
    }

    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerTopicDetailPath_Component.builder()
                .component((TopicListPath.Component)dependencies[0])
                .module(new Module(ap, groupId))
                .build();
    }

    @DaggerScope(TopicDetailPresenter.class)
    @dagger.Component(dependencies = TopicListPath.Component.class, modules = Module.class)
    interface Component extends TopicDetailComponent{
    }

    @dagger.Module
    static class Module{
        private final String ap;
        private final Long groupId;

        public Module(String ap, Long groupId) {
            this.ap = ap;
            this.groupId = groupId;
        }

        @Provides
        @DaggerScope(TopicDetailPresenter.class)
        Group<Bomb> provideBombGroup(UserAp userAp) {
            return userAp.getTopic(groupId);
        }

        @DaggerScope(TopicDetailPresenter.class)
        @Provides
        TopicDetailPresenter providePresenter(Account account, UserAp userAp, Group<Bomb> bombGroup, AvatarBinder
                avatarBinder, ActivityOwner owner) {
            return new TopicDetailPresenter(account, userAp, bombGroup, avatarBinder, owner);
        }
    }
}
