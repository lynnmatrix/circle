package com.jadenine.circle.domain;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.db.ApDBService;
import com.jadenine.circle.model.entity.UserApEntity;
import com.jadenine.circle.model.rest.ApService;
import com.jadenine.circle.model.rest.TimelineRangeResult;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.runtime.transaction.process.SaveModelTransaction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by linym on 6/18/15.
 */
public class ApSource {

    private static final int USER_AP_CAPABILITY = Integer.MAX_VALUE;

    @Inject
    ApService apService;

    @Inject
    ApDBService apDBService;

    private final String account;
    private final ArrayList<UserAp> aps = new ArrayList<>();
    private boolean loaded = false;

    private UserApMapperDelegate finder = new UserApMapperDelegate();

    public ApSource(String account) {
        this.account = account;
        DaggerService.getDomainComponent().inject(this);
    }

    public List<UserAp> getAps() {
        return new ArrayList<>(aps);
    }

    public UserAp getUserAp(String ap) {
        for(UserAp userAp : aps) {
            boolean apMatch = userAp.getAP().equals(ap);
            if (apMatch) {
                return userAp;
            }
        }
        return null;
    }

    public Observable<List<UserAp>> list() {

        Observable<List<UserAp>> observable;
        if (!loaded) {
            Observable<List<UserAp>> dbObservable = createDBObservable();
            observable = dbObservable.flatMap(new Func1<List<UserAp>, Observable<List<UserAp>>>() {
                @Override
                public Observable<List<UserAp>> call(List<UserAp> ds) {
                    onDBLoaded();
                    return Observable.mergeDelayError(createRefreshRestObservable(),
                            Observable.just(ds));
                }
            }).subscribeOn(Schedulers.io());
        } else {
            List[] lists = {getRestStartSource()};
            Observable restObservable = createRefreshRestObservable();
            observable = Observable.mergeDelayError(Observable.from(lists), restObservable).subscribeOn(Schedulers.io());
        }
        return observable;
    }

    public Observable<List<UserAp>> loadMore() {
        List[] lists = {getRestStartSource()};
        Observable restObservable = createLoadMoreRestObservable();
        return restObservable.startWith(Observable.from(lists)).subscribeOn(Schedulers
                .io());
    }

    Observable<List<UserAp>> addUserAp(final UserAp userAp) {
        Observable<List<UserAp>> observable = apService.addAP(userAp.getEntity()).map(new
                RefreshMapper<UserApEntity, UserAp>(finder));
        return observable;
    }

    private boolean isDBLoaded() {
        return loaded;
    }

    private void onDBLoaded() {
        loaded = true;
    }

    private Observable<List<UserAp>> createDBObservable() {
        return apDBService.listAps().map(new DBMapper<>(finder));
    }

    private Observable<List<UserAp>> createRefreshRestObservable() {
        return apService.listAPs(account).map(new RefreshMapper<UserApEntity, UserAp>(finder));
    }

    private Observable<List<UserAp>> createLoadMoreRestObservable() {
        return null;
    }

    private List<UserAp> getRestStartSource() {
        return aps;
    }

    private class UserApMapperDelegate implements MapperDelegate<UserApEntity, UserAp> {
        @Override
        public UserAp find(UserApEntity userApEntity) {
            return getUserAp(userApEntity.getAP());
        }

        @Override
        public UserAp build(UserApEntity userApEntity) {
            return UserAp.build(userApEntity);
        }

        @Override
        public void setHasMore(boolean hasMore) {
        }

        @Override
        public List<UserAp> getOriginSource() {
            return aps;
        }

        @Override
        public int getCapability() {
            return USER_AP_CAPABILITY;
        }
    }

    /**
     * Created by linym on 6/26/15.
     */
    static class RefreshMapper<E, D extends Updatable<E>> implements
            Func1<TimelineRangeResult<E>, List<D>> {
        private final MapperDelegate mapperDelegate;

        public RefreshMapper(MapperDelegate mapperDelegate) {
            this.mapperDelegate = mapperDelegate;
        }

        @Override
        public List<D> call(TimelineRangeResult<E> entities) {
            RestResultClassifier restResultClassifier = new RestResultClassifier(entities.getAll(),
                    mapperDelegate).invoke();

            TransactionManager.getInstance().addTransaction(new SaveModelTransaction(ProcessModelInfo
                    .withModels(restResultClassifier.getReceivedTopicEntities())));
            List<D> source = mapperDelegate.getOriginSource();
            if (restResultClassifier.getNewDomainObject().size() > 0) {
                source.addAll(0, restResultClassifier.getNewDomainObject());
            }
            boolean needClear = entities.hasMore() || source.size() > mapperDelegate.getCapability();

            if (needClear) {
                List<D> keptTopics = new ArrayList<>(source.subList(0, Math.min(source.size(),
                        mapperDelegate.getCapability())));
                source.clear();
                source.addAll(keptTopics);
                mapperDelegate.setHasMore(true);
            }

            return source;
        }
    }

    /**
     * Created by linym on 6/18/15.
     */
    static class DBMapper<E, D extends Updatable<E>> implements Func1<List<E>,
            List<D>> {

        private final MapperDelegate<E, D> mapperDelegate;

        public DBMapper(MapperDelegate<E, D> mapperDelegate){
            this.mapperDelegate = mapperDelegate;
        }

        @Override
        public List<D> call(List<E> entities) {

            for (E entity : entities) {
                D domainModel = mapperDelegate.find(entity);
                if (null == domainModel) {
                    domainModel = mapperDelegate.build(entity);
                    mapperDelegate.getOriginSource().add(domainModel);
                }
            }
            return mapperDelegate.getOriginSource();
        }
    }

    /**
     * Created by linym on 6/26/15.
     */
    static class LoadMoreMapper<E, D extends Updatable<E>> implements Func1<TimelineRangeResult<E>, List<D>> {
        private final MapperDelegate mapperDelegate;

        public LoadMoreMapper(MapperDelegate mapperDelegate) {
            this.mapperDelegate = mapperDelegate;
        }

        @Override
        public List<D> call(TimelineRangeResult<E> entities) {

            RestResultClassifier restResultClassifier = new RestResultClassifier(entities.getAll(),
                    mapperDelegate).invoke();

            TransactionManager.getInstance().addTransaction(new SaveModelTransaction(ProcessModelInfo.withModels
                    (restResultClassifier.getReceivedTopicEntities())));
            List<D> source = mapperDelegate.getOriginSource();

            source.addAll(restResultClassifier.getNewDomainObject());
            mapperDelegate.setHasMore(entities.hasMore());

            return source;
        }
    }

    /**
     * Created by linym on 6/18/15.
     */
    static interface MapperDelegate<E, D> extends Binder<E, D> {

        void setHasMore(boolean hasMore);

        List<D> getOriginSource();

        int getCapability();
    }

    /**
     * Created by linym on 6/26/15.
     */
    static class RestResultClassifier<E, D extends Updatable<E>> {
        private final List<E> entities;
        private final Binder<E, D> mapperDelegate;

        private List<E> receivedTopicEntities;
        private List<D> newDomainObject;

        public RestResultClassifier(List<E> entities, Binder<E, D> mapperDelegate) {
            this.entities = entities;
            this.mapperDelegate = mapperDelegate;
        }

        public List<E> getReceivedTopicEntities() {
            return receivedTopicEntities;
        }

        public List<D> getNewDomainObject() {
            return newDomainObject;
        }

        public RestResultClassifier invoke() {
            receivedTopicEntities = new ArrayList<>(entities.size());
            newDomainObject = new LinkedList<>();

            for (E entity : entities) {
                D domainObject = mapperDelegate.find(entity);

                if (null != domainObject) {
                    domainObject.merge(entity);
                } else {
                    domainObject = mapperDelegate.build(entity);
                    newDomainObject.add(domainObject);
                }

                receivedTopicEntities.add(domainObject.getEntity());
            }
            return this;
        }
    }

    /**
     * Created by linym on 7/10/15.
     */
    public interface Binder<E, D>  {
        D find(E e);

        D build(E e);
    }

    /**
     * Created by linym on 6/18/15.
     */
    interface Updatable<E> {
        void merge(E e);

        E getEntity();
    }
}
