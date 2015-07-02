package com.jadenine.circle.ui.composer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Topic;
import com.jadenine.circle.domain.UserAp;

import java.io.FileNotFoundException;
import java.io.InputStream;

import flow.Flow;
import mortar.ViewPresenter;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by linym on 6/9/15.
 */
public class ComposerPresenter extends ViewPresenter<ComposerView> implements PreferenceManager
        .OnActivityResultListener{
    private static final String BUNDLE_TYPED_CONTENT = "editor_content";
    private static final int PICK_IMAGE = 1;

    private final UserAp userAp;
    private Uri imageUri;
    private String mimeType;
    private Subscription sendSubscription = Subscriptions.empty();{
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

    void send(final String content) {
        if(TextUtils.isEmpty(content)){
            Toast.makeText(getView().getContext(), R.string.message_invalid_empty, Toast
                    .LENGTH_SHORT).show();
            return;
        }

        if (!sendSubscription.isUnsubscribed()) {
            return;
        }

        final Topic topic = new Topic(userAp, content);

        if(null != imageUri) {
            InputStream inputStream = null;
            ContentResolver contentResolver = getView().getContext().getContentResolver();
            try {
                inputStream = contentResolver.openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            sendSubscription = topic.uploadImage(inputStream, mimeType).subscribe(new Observer<String>() {

                @Override
                public void onCompleted() {
                    publish(topic);
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    Toast.makeText(getView().getContext(), R.string.message_send_fail, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(String imageUri) {
                    topic.addImage(imageUri);
                }
            });
        } else {
            publish(topic);
        }
    }

    private void publish(Topic topic){
        topic.publish(userAp).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Topic>() {

            @Override
            public void onCompleted() {
                Toast.makeText(getView().getContext(), R.string.message_send_success,
                        Toast.LENGTH_SHORT).show();
                Flow.get(getView().getContext()).goBack();
                sendSubscription = Subscriptions.empty();
                sendSubscription.unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Toast.makeText(getView().getContext(), R.string.message_send_fail, Toast
                        .LENGTH_LONG).show();
                sendSubscription = Subscriptions.empty();
                sendSubscription.unsubscribe();
            }

            @Override
            public void onNext(Topic topic1) {
            }
        });
    }

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
