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
 * Created by linym on 6/9/15.
 */
@DaggerScope(ComposerPresenter.class)
@Layout(R.layout.screen_composer)
public class ComposerPath extends Path implements ScreenComponentFactory {
    private final String ap;

    public ComposerPath(String ap) {
        this.ap = ap;
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
        UserAp provideUserAp(Account account) {
            return account.getUserAp(ap);
        }

        @DaggerScope(ComposerPresenter.class)
        @Provides
        ComposerPresenter providePresenter(UserAp userAp) {
            return new ComposerPresenter(userAp);
        }
    }
}
