package com.jadenine.circle.ui.chat;

import com.jadenine.circle.R;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.ScreenComponentFactory;
import com.jadenine.circle.ui.HomeComponent;
import com.jadenine.common.flow.Layout;

import flow.path.Path;

/**
 * Created by linym on 7/27/15.
 */
@Layout(R.layout.screen_my_chats )
public class MyChatPath extends Path implements ScreenComponentFactory {
    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerMyChatPath_Component.builder().homeComponent((HomeComponent)
                dependencies[0]).build();
    }

    @DaggerScope(MyChatPath.class)
    @dagger.Component(dependencies = HomeComponent.class)
    interface Component{
        void inject(MyChatsView chatsView);
    }
}
