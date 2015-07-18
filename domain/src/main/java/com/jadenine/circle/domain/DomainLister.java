package com.jadenine.circle.domain;

import java.util.List;

import rx.Observable;
import rx.android.internal.Preconditions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by linym on 6/18/15.
 */
class DomainLister<D> {

    public interface Delegate<D> {
        boolean isDBLoaded();

        void onDBLoaded();

        Observable<List<D>> createDBObservable();

        Observable<List<D>> createRefreshRestObservable();

        Observable<List<D>> createLoadMoreRestObservable();

        List<D> getRestStartSource();
    }

    private final Delegate<D> delegate;

    public DomainLister(Delegate delegate) {
        Preconditions.checkNotNull(delegate, "Null Delegate");
        this.delegate = delegate;
    }

    public Observable<List<D>> list() {

        Observable<List<D>> observable;
        if (!delegate.isDBLoaded()) {
            Observable<List<D>> dbObservable = delegate.createDBObservable();
            observable = dbObservable.flatMap(new Func1<List<D>, Observable<List<D>>>() {
                @Override
                public Observable<List<D>> call(List<D> ds) {
                    delegate.onDBLoaded();
                    return Observable.mergeDelayError(delegate.createRefreshRestObservable(),
                            Observable.just(ds));
                }
            }).subscribeOn(Schedulers.io());
        } else {
            List[] lists = {delegate.getRestStartSource()};
            Observable restObservable = delegate.createRefreshRestObservable();
            observable = Observable.mergeDelayError(Observable.from(lists), restObservable).subscribeOn(Schedulers.io());
        }
        return observable;
    }

    public Observable<List<D>> loadMore() {
        List[] lists = {delegate.getRestStartSource()};
        Observable restObservable = delegate.createLoadMoreRestObservable();
        return restObservable.startWith(Observable.from(lists)).subscribeOn(Schedulers
                .io());
    }
}
