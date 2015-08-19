package com.jadenine.circle.ui.topic.detail;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Circle;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.ScreenComponentFactory;
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
    private final Long groupId;
    private final Path parentPath;

    public TopicDetailPath(@NotNull Long groupId, Path parentPath) {
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
                .module(new Module(groupId))
                .build();
    }

    @DaggerScope(TopicDetailPresenter.class)
    @dagger.Component(dependencies = TopicListPath.Component.class, modules = Module.class)
    interface Component extends TopicDetailComponent{
    }

    @dagger.Module
    static class Module{
        private final Long groupId;

        public Module(Long groupId) {
            this.groupId = groupId;
        }

        @Provides
        @DaggerScope(TopicDetailPresenter.class)
        Group<Bomb> provideBombGroup(Circle circle) {
            return circle.getTopic(groupId);
        }

        @DaggerScope(TopicDetailPresenter.class)
        @Provides
        TopicDetailPresenter providePresenter(Account account, Circle circle, Group<Bomb> bombGroup, AvatarBinder
                avatarBinder, ActivityOwner owner) {
            return new TopicDetailPresenter(account, circle, bombGroup, avatarBinder, owner);
        }
    }
}
