package com.jadenine.circle.ui.detail;

import android.graphics.drawable.Drawable;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.ScreenComponentFactory;
import com.jadenine.circle.ui.HomeComponent;
import com.jadenine.common.flow.Layout;
import com.raizlabs.android.dbflow.annotation.NotNull;

import dagger.Provides;
import flow.path.Path;

/**
 * Created by linym on 7/24/15.
 */
@Layout(R.layout.screen_bomb_group)
public class BombGroupPath extends Path implements ScreenComponentFactory{
    private final String ap;
    private final Long groupId;

    public BombGroupPath(@NotNull String ap, @NotNull Long groupId) {
        this.ap = ap;
        this.groupId = groupId;
    }

    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerBombGroupPath_Component.builder().homeComponent((HomeComponent)
                dependencies[0])
                .module(new Module())
                .build();
    }

    @DaggerScope(BombGroupPresenter.class)
    @dagger.Component(dependencies = HomeComponent.class, modules = Module.class)
    interface Component {
        void inject(BombGroupDetailView detailView);
    }

    @dagger.Module
    class Module{

        @Provides
        @DaggerScope(BombGroupPresenter.class)
        UserAp provideUserAp(Account account){
            return account.getUserAp(ap);
        }

        @Provides
        @DaggerScope(BombGroupPresenter.class)
        Group<Bomb> provideBombGroup(UserAp userAp) {
            return userAp.getBombGroup(groupId);
        }

        @DaggerScope(BombGroupPresenter.class)
        @Provides
        BombGroupPresenter providePresenter(UserAp userAp, Group<Bomb> bombGroup, Drawable
                errorDrawable) {
            return new BombGroupPresenter(userAp, bombGroup, errorDrawable);
        }
    }
}
