package com.jadenine.circle.ui.composer;

import com.jadenine.circle.R;
import com.jadenine.circle.app.CircleApplication;
import com.jadenine.circle.entity.Topic;
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
@DaggerScope(ComposerPresenter.class)
@Layout(R.layout.screen_composer)
public class ComposerPath extends Path implements ScreenComponentFactory {
    private final Topic topic;
    private final String ap;

    public ComposerPath(UserAp userAp) {
        this.ap = userAp.getAP();
        this.topic = null;
    }

    public ComposerPath(Topic topic) {
        this.topic = topic;
        this.ap = topic.getAp();
    }

    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerComposerPath_Component.builder().appComponent((CircleApplication.AppComponent)
                dependencies[0]).module(new Module()).build();
    }

    @DaggerScope(ComposerPresenter.class)
    @dagger.Component(dependencies = CircleApplication.AppComponent.class, modules = Module.class)
    interface Component{
        void inject(ComposerView composer);
    }

    @dagger.Module
    class Module{

        @Provides
        @DaggerScope(ComposerPresenter.class)
        MessageService provideMessageService(RestAdapter restAdapter) {
            return restAdapter.create(MessageService.class);
        }

        @DaggerScope(ComposerPresenter.class)
        @Provides
        ComposerPresenter providePresenter(MessageService messageService) {
            if(null == topic) {
                return new ComposerPresenter(messageService, ap);
            }else {
                return new ComposerPresenter(messageService, topic);
            }
        }
    }
}
