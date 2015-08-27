package com.jadenine.circle.ui.image;

import com.jadenine.circle.R;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.ScreenComponentFactory;
import com.jadenine.circle.ui.HomeComponent;
import com.jadenine.common.flow.Layout;

import dagger.Provides;
import flow.path.Path;

/**
 * Created by linym on 8/27/15.
 */
@Layout(R.layout.screen_image)
public class ImagePath extends Path implements ScreenComponentFactory {
    private String imageUri;

    public ImagePath(String imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public Object createComponent(Object... dependencies) {
        return DaggerImagePath_Component.builder().homeComponent((HomeComponent)dependencies[0])
                .module(new Module()).build();
    }

    @DaggerScope(ImagePath.class)
    @dagger.Component(dependencies = HomeComponent.class, modules = Module.class)
    interface Component{
        void inject(ImageFullView imageFullView);
    }

    @dagger.Module
    class Module{
        @DaggerScope(ImagePath.class)
        @Provides
        public ImageFullPresenter presenter() {
            return new ImageFullPresenter(imageUri);
        }
    }

}
