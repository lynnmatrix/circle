package com.jadenine.circle.ui.topic;

import android.graphics.drawable.Drawable;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.ScreenComponentFactory;
import com.jadenine.circle.ui.HomeComponent;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.utils.SectionedLoadMoreRecyclerAdapter;
import com.jadenine.common.flow.Layout;

import dagger.Provides;
import flow.path.Path;

/**
 * Created by linym on 7/22/15.
 */
@Layout(R.layout.screen_bomb_list)
public class TopicListPath extends Path implements ScreenComponentFactory {
    private final String ap;

    public TopicListPath(String ap) {
        this.ap = ap;
    }

    public String getAp() {
        return ap;
    }

    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerTopicListPath_Component.builder().homeComponent((HomeComponent)
                dependencies[0])
                .module(new Module())
                .build();
    }

    @DaggerScope(TopicListPath.class)
    @dagger.Component(dependencies = HomeComponent.class, modules = Module.class)
    interface Component{
        void inject(TopicListView view);
    }

    @dagger.Module
    class Module {
        @DaggerScope(TopicListPath.class)
        @Provides
        UserAp provideUserAp(Account account) {
            return account.getUserAp(ap);
        }

        @DaggerScope(TopicListPath.class)
        @Provides
        SectionedLoadMoreRecyclerAdapter<Bomb> provideAdapter(AvatarBinder binder, Drawable
                errorDrawable) {
            return new SectionedLoadMoreRecyclerAdapter(new TopicListAdapter(errorDrawable,
                    binder));
        }
    }
}
