package com.jadenine.circle.ui.image;

import android.os.Bundle;

import com.squareup.picasso.Picasso;

import mortar.ViewPresenter;

/**
 * Created by linym on 8/27/15.
 */
public class ImageFullPresenter extends ViewPresenter<ImageFullView> {
    private final String imageUri;
    public ImageFullPresenter(String imageUrl) {
        this.imageUri = imageUrl;
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        if (!hasView()) return;
        Picasso.with(getView().getContext()).load(imageUri).into(getView().imageView);
    }
}
