package com.jadenine.circle.ui.composer;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Circle;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.ScreenComponentFactory;
import com.jadenine.circle.ui.HomeComponent;
import com.jadenine.common.flow.Layout;
import com.jadenine.common.mortar.ActivityOwner;

import dagger.Provides;
import flow.path.Path;

/**
 * Created by linym on 7/23/15.
 */
@Layout(R.layout.screen_composer)
public class ComposerPath extends Path implements ScreenComponentFactory {

        private final String circle;

        public ComposerPath(String circle) {
            this.circle = circle;
        }

        @Override
        public Object createComponent(Object... dependencies) {
            return DaggerComposerPath_Component.builder().homeComponent((HomeComponent)
                    dependencies[0]).module(new Module()).build();
        }

        @DaggerScope(ComposerPresenter.class)
        @dagger.Component(dependencies = HomeComponent.class, modules = Module.class)
        interface Component{
            void inject(ComposerView composer);
        }

        @dagger.Module
        class Module{
            @DaggerScope(ComposerPresenter.class)
            @Provides
            Circle provideCircle(Account account) {
                return account.getCircle(circle);
            }

            @DaggerScope(ComposerPresenter.class)
            @Provides
            ComposerPresenter providePresenter(Account account, Circle circle, ActivityOwner owner) {
                return new TopicComposerPresenter(account, circle, owner);
            }
        }
}
