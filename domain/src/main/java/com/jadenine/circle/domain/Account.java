package com.jadenine.circle.domain;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.db.ApDBService;
import com.jadenine.circle.model.entity.UserApEntity;
import com.jadenine.circle.model.rest.ApService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;

/**
 * Created by linym on 6/17/15.
 */
public class Account {
    private final String deviceId;
    private final ArrayList<UserAp> aps = new ArrayList<>();

    @Inject
    ApService apService;
    @Inject
    ApDBService apDBService;

    private boolean loaded = false;

    private  final UserApFinder finder = new UserApFinder();
    private final DomainLister<UserAp> userApLister = new DomainLister<>(new UserApListerDelegate());

    public Account(String deviceId) {
        this.deviceId = deviceId;
        DaggerService.getDomainComponent().inject(this);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Observable<List<UserAp>> listAPs() {
        return userApLister.list();
    }

    Observable<List<UserAp>> addUserAp(final UserAp userAp) {
        if(null == finder.find(userAp.getEntity())) {
            aps.add(userAp);
        }
        Observable<List<UserAp>> observable = apService.addAP(userAp.getEntity()).map(new RestListMapper<>
                (finder, aps));
        observable.subscribe(new Observer<List<UserAp>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                aps.remove(userAp);
            }

            @Override
            public void onNext(List<UserAp> userAps) {
            }
        });
        return observable;
    }

    private class UserApFinder implements Finder<UserApEntity, UserAp>{
        @Override
        public UserAp find(UserApEntity userApEntity) {
            for(UserAp userAp : aps) {
                if (userAp.getAP().equals(userApEntity.getAP())) {
                    return userAp;
                }
            }
            return null;
        }

        @Override
        public UserAp build(UserApEntity userApEntity) {
            return UserAp.build(userApEntity);
        }
    }

    private class UserApListerDelegate implements DomainLister.Delegate<UserAp> {
        @Override
        public boolean isDBLoaded() {
            return loaded;
        }

        @Override
        public void onDBLoaded() {
            loaded = true;
        }

        @Override
        public Observable<List<UserAp>> createDBObservable() {
            return apDBService.listAps().map(new DBMapper<>(finder, aps));
        }

        @Override
        public Observable<List<UserAp>> createRestObservable() {
            return apService.listAPs(getDeviceId()).map(new RestListMapper<>(finder, aps));
        }

        @Override
        public List<UserAp> getRestStartSource() {
            return aps;
        }
    }
}
