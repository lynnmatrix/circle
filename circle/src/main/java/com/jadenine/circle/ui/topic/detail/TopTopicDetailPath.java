package com.jadenine.circle.ui.topic.detail;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.ScreenComponentFactory;
import com.jadenine.circle.ui.HomeComponent;
import com.jadenine.common.flow.Layout;
import com.raizlabs.android.dbflow.annotation.NotNull;

import dagger.Provides;
import flow.path.Path;

/**
 * Created by linym on 8/12/15.
 */
@Layout(R.layout.screen_topic_detail)
public class TopTopicDetailPath extends Path implements ScreenComponentFactory {
    private final Long groupId;

    public TopTopicDetailPath(@NotNull Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerTopTopicDetailPath_Component.builder().homeComponent((HomeComponent)
                dependencies[0])
                .topicDetailModule(new TopicDetailModule())
                .module(new Module(groupId))
                .build();
    }

    @DaggerScope(TopicDetailPresenter.class)
    @dagger.Component(dependencies = HomeComponent.class, modules = Module.class)
    interface Component extends TopicDetailComponent{
    }

    @dagger.Module(includes = TopicDetailModule.class)
    static class Module{
        private final Long groupId;

        public Module(Long groupId) {
            this.groupId = groupId;
        }

        @Provides
        @DaggerScope(TopicDetailPresenter.class)
        Group<Bomb> provideBombGroup(Account account) {
            return account.getTopTopic(groupId);
        }
    }
}
