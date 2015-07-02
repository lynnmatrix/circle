package com.jadenine.circle.ui.topic;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

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
 * Created by linym on 6/10/15.
 */
@DaggerScope(TopicPresenter.class)
@Layout(R.layout.screen_topic_list)
public class TopicPath extends Path implements ScreenComponentFactory {
    private final String ap;

    public TopicPath(String ap) {
        this.ap = ap;
    }

    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerTopicPath_Component.builder().homeComponent((HomeComponent)
                dependencies[0])
                .module(new Module())
                .build();
    }

    @DaggerScope(TopicPresenter.class)
    @dagger.Component(dependencies = HomeComponent.class, modules = Module.class)
    interface Component{
        void inject(TopicView topicView);
        void inject(TopicItemViewHolder viewHolder);
    }

    @dagger.Module
    class Module{
        @DaggerScope(TopicPresenter.class)
        @Provides
        UserAp provideUserAp(Account account) {
            return account.getUserAp(ap);
        }

        @DaggerScope(TopicPresenter.class)
        @Provides
        TopicPresenter providePresenter(UserAp userAp) {
            return new TopicPresenter(userAp);
        }

        @DaggerScope(TopicPresenter.class)
        @Provides
        Drawable provideErrorDrawable(Activity activity){
            Drawable errorDrawable = activity.getResources().getDrawable(R.drawable
                    .ic_error_outline_black);

            TypedValue typedValue = new TypedValue();
            int[] textSizeAttr = new int[]{R.attr.colorPrimary};
            int indexOfAttrColorPrimary = 0;
            TypedArray a = activity.obtainStyledAttributes(typedValue.data, textSizeAttr);
            int colorPrimary = a.getColor(indexOfAttrColorPrimary, Color.BLACK);
            a.recycle();

            errorDrawable.setColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN);
            return errorDrawable;
        }
    }


}
