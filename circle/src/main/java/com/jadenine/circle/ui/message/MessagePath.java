package com.jadenine.circle.ui.message;

import com.jadenine.circle.R;
import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.entity.UserAp;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.ScreenComponentFactory;
import com.jadenine.circle.request.MessageService;
import com.jadenine.common.flow.Layout;

import dagger.Provides;
import flow.path.Path;
import retrofit.RestAdapter;

/**
 * Created by linym on 6/8/15.
 */
@DaggerScope(MessagePresenter.class)
@Layout(R.layout.screen_message_list)
public class MessagePath extends Path implements ScreenComponentFactory{
    private final UserAp userAp;
    public MessagePath(UserAp userAp) {
        this.userAp = userAp;
    }

    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerMessagePath_Component.builder().appComponent((CircleApplication
                .AppComponent) dependencies[0]).module(new Module()).build();
    }

    @DaggerScope(MessagePresenter.class)
    @dagger.Component(dependencies = CircleApplication.AppComponent.class, modules = Module.class)
    interface Component{
        void inject(MessagePathView pathView);
    }

    @dagger.Module()
    class Module{
        @Provides
        @DaggerScope(MessagePresenter.class)
        MessageService provideMessageService(RestAdapter restAdapter) {
            return restAdapter.create(MessageService.class);
        }

        @Provides
        @DaggerScope(MessagePresenter.class)
        MessagePresenter providePresenter(MessageService messageService){
            return new MessagePresenter(messageService, userAp);
        }
    }
}
