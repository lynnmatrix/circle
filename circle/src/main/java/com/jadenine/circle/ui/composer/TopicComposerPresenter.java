package com.jadenine.circle.ui.composer;

import android.content.ContentResolver;
import android.text.TextUtils;
import android.widget.Toast;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Topic;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.ui.utils.SoftKeyboardToggler;

import java.io.FileNotFoundException;
import java.io.InputStream;

import flow.Flow;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by linym on 7/23/15.
 */
public class TopicComposerPresenter extends ComposerPresenter {
    public TopicComposerPresenter(UserAp userAp) {
        super(userAp);
    }

    @Override
    void send(String content) {
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(getView().getContext(), R.string.message_invalid_empty, Toast
                    .LENGTH_SHORT).show();
            return;
        }

        if (!sendSubscription.isUnsubscribed()) {
            return;
        }

        final Topic topic = new Topic(userAp, content);

        if (null != imageUri) {
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
                    if (!hasView()) {
                        return;
                    }
                    Toast.makeText(getView().getContext(), R.string.message_send_fail, Toast
                            .LENGTH_SHORT).show();
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

    private void publish(Topic topic) {
        topic.publish(userAp).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Topic>() {

            @Override
            public void onCompleted() {
                if (!hasView()) {
                    return;
                }
                Toast.makeText(getView().getContext(), R.string.message_send_success, Toast
                        .LENGTH_SHORT).show();
                SoftKeyboardToggler.toggleInputMethod(getView().editor, false);
                Flow.get(getView().getContext()).goBack();
                sendSubscription = Subscriptions.empty();
                sendSubscription.unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                if (!hasView()) {
                    return;
                }
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
}
