package com.jadenine.circle.ui.composer;

import android.content.ContentResolver;
import android.widget.Toast;

import com.jadenine.circle.R;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Circle;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.ui.utils.ContentValidator;
import com.jadenine.circle.ui.utils.SoftKeyboardToggler;
import com.jadenine.common.mortar.ActivityOwner;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import flow.Flow;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

/**
 * Created by linym on 7/23/15.
 */
class TopicComposerPresenter extends ComposerPresenter {

    private final AtomicBoolean sending = new AtomicBoolean(false);

    public TopicComposerPresenter(Account account, Circle circle, ActivityOwner owner) {
        super(account, circle, owner);
    }

    @Override
    void send(String content) {
        if (!ContentValidator.validate(getView().getContext(), content)) {
            return;
        }

        if (sending.get() || !sendSubscription.isUnsubscribed()) {
            return;
        }
        sending.set(true);

        //TODO reuse bomb
        final Bomb bomb = new Bomb(circle.getCircleId(), account.getDeviceId());
        bomb.setContent(content);
        if (null != imageUri) {
            InputStream inputStream = null;
            ContentResolver contentResolver = getView().getContext().getContentResolver();
            try {
                inputStream = contentResolver.openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            sendSubscription = circle.uploadImage(inputStream, mimeType).subscribe(new Observer<String>() {

                @Override
                public void onCompleted() {
                    publish(bomb);
                }

                @Override
                public void onError(Throwable e) {
                    Timber.e(e, "Fail to upload image.");
                    sendSubscription = Subscriptions.empty();
                    sendSubscription.unsubscribe();
                    sending.set(false);
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
                sendSubscription = Subscriptions.empty();
                sendSubscription.unsubscribe();
                sending.set(false);
                if (!hasView()) {
                    return;
                }
                Toast.makeText(getView().getContext(), R.string.message_send_success, Toast
                        .LENGTH_SHORT).show();
                SoftKeyboardToggler.toggleInputMethod(getView().editor, false);
                Flow.get(getView().getContext()).goBack();
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "fail to publish new message.");
                sendSubscription = Subscriptions.empty();
                sendSubscription.unsubscribe();
                sending.set(false);
                if (!hasView()) {
                    return;
                }
                Toast.makeText(getView().getContext(), R.string.message_send_fail, Toast
                        .LENGTH_LONG).show();
            }

            @Override
            public void onNext(Bomb bomb) {
            }
        });
    }

}
