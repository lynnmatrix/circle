package com.jadenine.circle.ui.topic.top;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.ScreenComponentFactory;
import com.jadenine.circle.ui.HomeComponent;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.topic.TopicListAdapter;
import com.jadenine.circle.ui.topic.detail.TopTopicDetailPath;
import com.jadenine.common.flow.Layout;
import com.jadenine.common.mortar.ActivityOwner;

import dagger.Provides;
import flow.Flow;
import flow.path.Path;

/**
 * Created by linym on 8/11/15.
 */
@Layout(R.layout.screen_top)
public class TopPath extends Path implements ScreenComponentFactory{

    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerTopPath_Component.builder().homeComponent((HomeComponent) dependencies[0]).module(new Module()).build();
    }

    @DaggerScope(TopPath.class)
    @dagger.Component(dependencies = HomeComponent.class, modules = Module.class)
    interface Component{
        void inject(TopView topView);
    }

    @dagger.Module
    static class Module{
        @DaggerScope(TopPath.class)
        @Provides
        TopPresenter providePresenter(Account account, ActivityOwner activityOwner) {
            return new TopPresenter(account, activityOwner);
        }

        @DaggerScope(TopPath.class)
        @Provides
        TopicListAdapter provideAdapter(Drawable errorDrawable, AvatarBinder avatarBinder) {
            return new TopicListAdapter(errorDrawable, avatarBinder, new TopicListAdapter.OnTopicClickListener() {
                @Override
                public void onTopicClicked(Context context, Group<Bomb> topic) {
                    Bomb rootBomb = topic.getRoot();
                    Flow.get(context).set(new TopTopicDetailPath(rootBomb.getGroupId()));
                }
            });
        }
    }
}
