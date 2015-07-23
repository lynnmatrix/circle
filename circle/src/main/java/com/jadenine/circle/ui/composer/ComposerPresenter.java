package com.jadenine.circle.ui.composer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.ui.utils.SoftKeyboardToggler;

import mortar.ViewPresenter;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

/**
 * Created by linym on 6/9/15.
 */
public abstract class ComposerPresenter extends ViewPresenter<ComposerView> implements PreferenceManager
        .OnActivityResultListener{
    private static final String BUNDLE_TYPED_CONTENT = "editor_content";
    private static final int PICK_IMAGE = 1;

    protected final UserAp userAp;
    protected Uri imageUri;
    protected String mimeType;
    protected Subscription sendSubscription = Subscriptions.empty();{
        sendSubscription.unsubscribe();
    }

    public ComposerPresenter(UserAp userAp) {
        this.userAp = userAp;
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        if(!hasView()){
            return;
        }

        getView().toolbar.setTitle(R.string.title_activity_topic_composer);

        if(null != savedInstanceState) {
            String content = savedInstanceState.getString(BUNDLE_TYPED_CONTENT, "");
            getView().editor.setText(content);
        }
        SoftKeyboardToggler.toggleInputMethod(getView().editor, true);
    }

    @Override
    protected void onSave(Bundle outState) {
        super.onSave(outState);
        outState.putString(BUNDLE_TYPED_CONTENT, getView().editor.getText().toString());
    }

    void pickImage() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        getView().activity.startActivityForResult(intent, PICK_IMAGE);
    }


    abstract void send(final String content);

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if(PICK_IMAGE == requestCode && resultCode == Activity.RESULT_OK) {
            ContentResolver contentResolver = getView().getContext().getContentResolver();

            imageUri = data.getData();
            mimeType = contentResolver.getType(imageUri);

            getView().imageView.setImageURI(imageUri);
            getView().imageView.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }
}
