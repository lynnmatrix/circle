package com.jadenine.circle.ui.message.composer;

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
 * Created by linym on 6/9/15.
 */
@DaggerScope(MessageComposerPresenter.class)
@Layout(R.layout.screen_message_composer)
public class MessageAddPath extends Path implements ScreenComponentFactory {
    private final UserAp userAp;
    public MessageAddPath(UserAp userAp) {
        this.userAp = userAp;
    }

    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerMessageAddPath_Component.builder().appComponent((CircleApplication.AppComponent)
                dependencies[0]).module(new Module()).build();
    }

    @DaggerScope(MessageComposerPresenter.class)
    @dagger.Component(dependencies = CircleApplication.AppComponent.class, modules = Module.class)
    interface Component{
        void inject(ComposerView composer);
    }

    @dagger.Module
    class Module{

        @Provides
        @DaggerScope(MessageComposerPresenter.class)
        MessageService provideMessageService(RestAdapter restAdapter) {
            return restAdapter.create(MessageService.class);
        }

        @DaggerScope(MessageComposerPresenter.class)
        @Provides
        MessageComposerPresenter providePresenter(MessageService messageService) {
            return new MessageComposerPresenter(messageService, userAp);
        }
    }
}
