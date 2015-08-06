package com.jadenine.circle.ui.composer;

import android.content.ContentResolver;
import android.text.TextUtils;
import android.widget.Toast;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.UserAp;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.utils.SoftKeyboardToggler;
import com.jadenine.common.mortar.ActivityOwner;

import java.io.FileNotFoundException;
import java.io.InputStream;

import flow.Flow;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by linym on 7/23/15.
 */
class TopicComposerPresenter extends ComposerPresenter {

    private final long CONTENT_MAX_LENGTH = 256;
    public TopicComposerPresenter(Account account, UserAp userAp, ActivityOwner owner) {
        super(account, userAp, owner);
    }

    @Override
    void send(String content) {
        if (TextUtils.isEmpty(content) ) {
            Toast.makeText(getView().getContext(), R.string.message_invalid_empty, Toast
                    .LENGTH_SHORT).show();
            return;
        }

        if (content.length() > CONTENT_MAX_LENGTH) {
            if (TextUtils.isEmpty(content) ) {
                Toast.makeText(getView().getContext(), getView().getContext().getString(R.string
                        .message_invalid_size, CONTENT_MAX_LENGTH), Toast
                        .LENGTH_SHORT).show();
                return;
            }
        }

        if (!sendSubscription.isUnsubscribed()) {
            return;
        }

        //TODO reuse bomb
        final Bomb bomb = new Bomb(userAp.getAP(), userAp.getUser());
        bomb.setContent(content);
        if (null != imageUri) {
            InputStream inputStream = null;
            ContentResolver contentResolver = getView().getContext().getContentResolver();
            try {
                inputStream = contentResolver.openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            sendSubscription = userAp.uploadImage(inputStream, mimeType).subscribe(new Observer<String>() {

                @Override
                public void onCompleted() {
                    publish(bomb);
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
                    bomb.setImages(imageUri);
                }
            });
        } else {
            publish(bomb);
        }
    }

    private void publish(Bomb bomb) {
        account.publish(bomb).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer
                <Bomb>() {

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
            public void onNext(Bomb bomb) {
            }
        });
    }

}
