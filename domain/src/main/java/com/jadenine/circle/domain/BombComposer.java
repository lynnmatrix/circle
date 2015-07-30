package com.jadenine.circle.domain;

import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.model.entity.Image;
import com.jadenine.circle.model.rest.AzureBlobUploader;
import com.jadenine.circle.model.rest.BombService;
import com.jadenine.circle.model.rest.ImageService;

import java.io.InputStream;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by linym on 7/23/15.
 */
class BombComposer {
    private final ImageService imageService;
    private final AzureBlobUploader blobUploader;
    private final BombService bombService;

    @Inject
    public BombComposer(ImageService imageService, AzureBlobUploader uploader, BombService bombService) {
        this.imageService = imageService;
        this.blobUploader = uploader;
        this.bombService = bombService;
    }

    public Observable<String> uploadImage(final InputStream imageInputStream, final String
            mimeType) {
        return imageService.getWritableSas().flatMap(new Func1<Image, Observable<String>>() {
            @Override
            public Observable<String> call(final Image image) {
                return Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        boolean success = blobUploader.upload(image.getWritableSas(),
                                imageInputStream, mimeType);
                        if (!subscriber.isUnsubscribed()) {
                            if (success) {
                                subscriber.onNext(image.getReadableSas());
                                subscriber.onCompleted();
                            } else {
                                subscriber.onError(new Exception("Fail to upload image"));
                            }
                        }
                    }
                }).subscribeOn(Schedulers.io());
            }
        });
    }

    public Observable<Bomb> send(Bomb bomb) {
        return bombService.add(bomb);
    }
}
