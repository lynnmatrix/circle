package com.jadenine.circle.ui.composer;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.ScreenComponentFactory;
import com.jadenine.circle.ui.HomeComponent;
import com.jadenine.common.flow.Layout;

import dagger.Provides;
import flow.path.Path;

/**
 * Created by linym on 7/23/15.
 */
@Layout(R.layout.screen_composer)
public class BombComposerPath extends Path implements ScreenComponentFactory {

        private final String ap;

        public BombComposerPath(String ap) {
            this.ap = ap;
        }

        @Override
        public Object createComponent(Object... dependencies) {
            return DaggerBombComposerPath_Component.builder().homeComponent((HomeComponent)
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
            UserAp provideUserAp(Account account) {
                return account.getUserAp(ap);
            }

            @DaggerScope(ComposerPresenter.class)
            @Provides
            ComposerPresenter providePresenter(UserAp userAp) {
                return new BombComposerPresenter(userAp);
            }
        }
}
