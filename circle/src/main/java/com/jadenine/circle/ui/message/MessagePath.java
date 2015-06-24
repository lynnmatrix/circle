package com.jadenine.circle.ui.message;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Topic;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.ScreenComponentFactory;
import com.jadenine.circle.ui.HomeComponent;
import com.jadenine.common.flow.Layout;

import dagger.Provides;
import flow.path.Path;

/**
 * Created by linym on 6/8/15.
 */
@DaggerScope(MessagePresenter.class)
@Layout(R.layout.screen_message_list)
public class MessagePath extends Path implements ScreenComponentFactory{
    private final String ap;
    private final String topicId;

    public MessagePath(String ap, String topicId) {
        this.ap = ap;
        this.topicId = topicId;
    }

    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerMessagePath_Component.builder().homeComponent((HomeComponent)
                dependencies[0]).module(new Module()).build();
    }

    @DaggerScope(MessagePresenter.class)
    @dagger.Component(dependencies = HomeComponent.class, modules = Module.class)
    public interface Component{
        void inject(MessageListView pathView);
    }

    @dagger.Module()
    class Module{
        @Provides
        @DaggerScope(MessagePresenter.class)
        Topic provieTopic(Account account){
            Topic topic = null;
            UserAp userAp = account.getUserAp(ap);
            if(null != userAp) {
                topic = userAp.getTopic(topicId);
            }
            return topic;
        }
        @Provides
        @DaggerScope(MessagePresenter.class)
        MessagePresenter providePresenter(Topic topic){
            return new MessagePresenter(topic);
        }
    }
}
