package com.jadenine.circle.ui.chat;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.ScreenComponentFactory;
import com.jadenine.circle.ui.HomeComponent;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.ui.utils.SectionedLoadMoreRecyclerAdapter;
import com.jadenine.common.flow.Layout;
import com.jadenine.common.mortar.ActivityOwner;

import dagger.Provides;
import flow.path.Path;

/**
 * Created by linym on 7/27/15.
 */
@Layout(R.layout.screen_my_chats )
public class MyChatPath extends Path implements ScreenComponentFactory {
    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerMyChatPath_Component.builder().homeComponent((HomeComponent)
                dependencies[0]).module(new Module()).build();
    }

    @DaggerScope(MyChatPath.class)
    @dagger.Component(dependencies = HomeComponent.class, modules = Module.class)
    interface Component{
        void inject(MyChatsView chatsView);
    }

    @dagger.Module
    static class Module {
        @DaggerScope(MyChatPath.class)
        @Provides
        SectionedLoadMoreRecyclerAdapter<DirectMessageEntity> provideMyChatsAdapter(Account account, AvatarBinder binder) {
            return new SectionedLoadMoreRecyclerAdapter(new MyChatsAdapter(account, binder));
        }

        @DaggerScope(MyChatPath.class)
        @Provides
        MyChatsPresenter providePresenter(Account account, ActivityOwner owner) {
            return new MyChatsPresenter(account, owner);
        }
    }
}
