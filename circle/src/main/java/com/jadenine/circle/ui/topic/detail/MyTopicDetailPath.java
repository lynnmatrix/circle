package com.jadenine.circle.ui.topic.detail;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Circle;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.ScreenComponentFactory;
import com.jadenine.circle.ui.HomeComponent;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.chat.detail.ChatPath;
import com.jadenine.circle.ui.widgets.TopicHeader;
import com.jadenine.common.flow.Layout;
import com.raizlabs.android.dbflow.annotation.NotNull;

import dagger.Provides;
import flow.Flow;
import flow.path.Path;

/**
 * Created by linym on 8/6/15.
 */
@Layout(R.layout.screen_topic_detail)
public class MyTopicDetailPath extends Path implements ScreenComponentFactory {
    private final Long groupId;

    public MyTopicDetailPath(@NotNull Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerMyTopicDetailPath_Component.builder().homeComponent((HomeComponent)
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
            return account.getMyTopic(groupId);
        }

        @Provides
        @DaggerScope(TopicDetailPresenter.class)
        BombListAdapter provideAdapter(final Account account, final Circle circle, final Group<Bomb> bombGroup, Drawable errorDrawable, AvatarBinder binder) {
            BombListAdapter adapter = new BombListAdapter(errorDrawable, binder);
            adapter.setOnAvatarClickListener(new TopicHeader.OnAvatarClickListener
                    () {
                @Override
                public void onClick(Context context) {
                    ChatPath chatPath = new ChatPath(circle.getCircleId(), bombGroup.getGroupId(), account.getDeviceId(), bombGroup.getRoot().getFrom());
                    Flow.get(context).set(chatPath);
                }
            });
            return adapter;
        }

    }
}
