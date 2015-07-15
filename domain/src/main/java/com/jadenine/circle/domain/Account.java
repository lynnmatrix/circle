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
    private static final int USER_AP_CAPABILITY = Integer.MAX_VALUE;
    private final String deviceId;
    private final ArrayList<UserAp> aps = new ArrayList<>();

    @Inject
    ApService apService;
    @Inject
    ApDBService apDBService;

    private boolean loaded = false;
    private boolean hasMore = true;

    private final UserApMapperDelegate finder = new UserApMapperDelegate();
    private final DomainLister<UserAp> userApLister = new DomainLister<>(new UserApListerDelegate());

    public Account(String deviceId) {
        this.deviceId = deviceId;
        DaggerService.getDomainComponent().inject(this);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public List<UserAp> getUserAps(){
        return aps;
    }

    public Observable<List<UserAp>> listAPs() {
        return userApLister.list();
    }

    Observable<List<UserAp>> addUserAp(final UserAp userAp) {
        Observable<List<UserAp>> observable = apService.addAP(userAp.getEntity()).map(new
                RefreshMapper<UserApEntity, UserAp>(finder));
        return observable;
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

        @Override
        public List<UserAp> getOriginSource() {
            return aps;
        }

        @Override
        public int getCapability() {
            return USER_AP_CAPABILITY;
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
            return apDBService.listAps().map(new DBMapper<>(finder));
        }

        @Override
        public Observable<List<UserAp>> createRefreshRestObservable() {
            return apService.listAPs(getDeviceId()).map(new RefreshMapper<UserApEntity, UserAp>(finder));
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
