package com.jadenine.circle.domain;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.db.ApDBService;
import com.jadenine.circle.model.entity.UserApEntity;
import com.jadenine.circle.model.rest.ApService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

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
    private boolean hasMore = true;

    private  final UserApMapperDelegate finder = new UserApMapperDelegate();
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
        Observable<List<UserAp>> observable = apService.addAP(userAp.getEntity()).map(new RestListMapper<>
                (finder, aps));
        return observable;
    }

    public UserAp getUserAp(String ap) {
        for(UserAp userAp : aps) {
            if (userAp.getAP().equals(ap)) {
                return userAp;
            }
        }
        return null;
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
            Account.this.hasMore = hasMore;
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
        public Observable<List<UserAp>> createRefreshRestObservable() {
            return apService.listAPs(getDeviceId()).map(new RestListMapper<>(finder, aps));
        }

        @Override
        public Observable<List<UserAp>> createLoadMoreRestObservable() {
            return null;
        }

        @Override
        public List<UserAp> getRestStartSource() {
            return aps;
        }
    }
}
