package com.jadenine.circle.ui.topic.user;

import android.graphics.drawable.Drawable;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.ScreenComponentFactory;
import com.jadenine.circle.ui.HomeComponent;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.topic.TopicListAdapter;
import com.jadenine.circle.ui.utils.SectionedLoadMoreRecyclerAdapter;
import com.jadenine.common.flow.Layout;
import com.jadenine.common.mortar.ActivityOwner;

import dagger.Provides;
import flow.path.Path;

/**
 * Created by linym on 8/6/15.
 */
@Layout(R.layout.screen_my_topics)
public class MyTopicPath extends Path implements ScreenComponentFactory {

    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerMyTopicPath_Component.builder().homeComponent((HomeComponent)
                dependencies[0])
                .module(new Module())
                .build();
    }


    @DaggerScope(MyTopicPath.class)
    @dagger.Component(dependencies = HomeComponent.class, modules = Module.class)
    interface Component{
        void inject(MyTopicView view);
    }

    @dagger.Module
    static class Module {
        @DaggerScope(MyTopicPath.class)
        @Provides
        SectionedLoadMoreRecyclerAdapter<Bomb> provideAdapter(AvatarBinder binder, Drawable
                errorDrawable) {
            return new SectionedLoadMoreRecyclerAdapter(new TopicListAdapter(errorDrawable,
                    binder));
        }

        @DaggerScope(MyTopicPath.class)
        @Provides
        MyTopicsPresenter providePresenter(Account account, ActivityOwner owner){
            return new MyTopicsPresenter(account, owner);
        }

    }
    
}
