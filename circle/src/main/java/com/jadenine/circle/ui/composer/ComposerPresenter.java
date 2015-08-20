package com.jadenine.circle.ui.composer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Circle;
import com.jadenine.circle.domain.Constants;
import com.jadenine.circle.ui.utils.SoftKeyboardToggler;
import com.jadenine.circle.utils.ImageCompressor;
import com.jadenine.circle.utils.ToolbarColorizer;
import com.jadenine.common.mortar.ActivityOwner;

import java.io.FileNotFoundException;
import java.io.IOException;

import mortar.ViewPresenter;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

/**
 * Created by linym on 6/9/15.
 */
abstract class ComposerPresenter extends ViewPresenter<ComposerView> implements PreferenceManager
        .OnActivityResultListener{
    private static final String BUNDLE_TYPED_CONTENT = "editor_content";
    private static final int PICK_IMAGE = 1;

    protected final Account account;
    protected final Circle circle;
    protected Uri imageUri;
    protected String mimeType;

    private final ActivityOwner activityOwner;

    protected Subscription sendSubscription = Subscriptions.empty();{
        sendSubscription.unsubscribe();
    }
    public ComposerPresenter(Account account, Circle circle, ActivityOwner owner) {
        this.account = account;
        this.circle = circle;
        this.activityOwner = owner;
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        if(!hasView()){
            return;
        }

        if(null != savedInstanceState) {
            String content = savedInstanceState.getString(BUNDLE_TYPED_CONTENT, "");
            getView().editor.setText(content);
        }
        SoftKeyboardToggler.toggleInputMethod(getView().editor, true);

        ToolbarColorizer.colorizeToolbar(getView().toolbar, Color.WHITE, activityOwner.getActivity());
        getView().toolbar.setTitle(R.string.title_activity_topic_composer);
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
        Activity activity = activityOwner.getActivity();
        activity.startActivityForResult(intent, PICK_IMAGE);
    }

    abstract void send(final String content);

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if(PICK_IMAGE == requestCode && resultCode == Activity.RESULT_OK) {
            ContentResolver contentResolver = getView().getContext().getContentResolver();

            imageUri = data.getData();
            mimeType = contentResolver.getType(imageUri);
            try {
                imageUri = ImageCompressor.compress(getView().getContext(), imageUri, Constants
                        .COMPRESS_DST_WIDTH, Constants.COMPRESS_DST_HEIGHT);
            } catch (FileNotFoundException e) {
                Timber.e(e, "Fail to compress image.");
            } catch (IOException e) {
                Timber.e(e, "Fail to compress image.");
            }

            getView().imageView.setImageURI(imageUri);
            getView().imageView.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }
}
