package com.jadenine.circle.domain;

import com.jadenine.circle.domain.dagger.DaggerService;
import com.jadenine.circle.model.db.ApDBService;
import com.jadenine.circle.model.db.CircleDBService;
import com.jadenine.circle.model.entity.ApEntity;
import com.jadenine.circle.model.entity.CircleEntity;
import com.jadenine.circle.model.rest.CircleResult;
import com.jadenine.circle.model.rest.CircleService;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.runtime.transaction.process.SaveModelTransaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by linym on 8/19/15.
 */
public class CircleSource {

    @Inject
    ApDBService apDBService;

    @Inject
    CircleService circleService;

    @Inject
    CircleDBService circleDBService;

    private final String account;
    private final ArrayList<Circle> circles = new ArrayList<>();
    private final ArrayList<ApEntity> aps = new ArrayList<>();
    private AtomicBoolean loaded = new AtomicBoolean(false);
    private final RefreshMapper refreshMapper = new RefreshMapper();

    public CircleSource(String deviceId) {
        this.account = deviceId;
        DaggerService.getDomainComponent().inject(this);
    }

    public List<Circle> getCircles() {
        return new ArrayList<>(circles);
    }

    public Circle getCircle(String circleId) {
        Circle result = null;
        for(Circle circle : getCircles()) {
            if(circle.getCircleId().equals(circleId)) {
                result = circle;
                break;
            }
        }
        return result;
    }

    private ApEntity getAp(String mac) {
        ApEntity result = null;
        ArrayList<ApEntity> localAps = new ArrayList<>(aps);
        for(ApEntity ap : localAps) {
            if(ap.getMac().equals(mac)) {
                result = ap;
                break;
            }
        }

        return result;
    }

    public Observable<List<Circle>> listCircles() {
        Observable<List<Circle>> result ;
        if(!loaded.get()) {
            result = circleDBService.listCircles().zipWith(apDBService.listAps(), new Func2<List<CircleEntity>, List<ApEntity>, Void>() {
                @Override
                public Void call(List<CircleEntity> circleEntities, List<ApEntity> apEntities) {
                    updateCircles(circleEntities, false);
                    updateAps(apEntities, false);
                    loaded.set(true);
                    return null;
                }
            }).flatMap(new Func1<Void, Observable<List<Circle>>>() {
                @Override
                public Observable<List<Circle>> call(Void v) {
                    return circleService.listCircle(account).flatMap(refreshMapper);
                }
            }).subscribeOn(Schedulers.io());
        } else {
            result = circleService.listCircle(account).flatMap(refreshMapper);
        }
        return result;
    }

    public Observable<List<Circle>> addAp(ApEntity ap) {
        return circleService.addAP(account, ap).flatMap(refreshMapper);
    }

    class RefreshMapper implements Func1<CircleResult, Observable<List<Circle>>>{
        @Override
        public Observable<List<Circle>> call(CircleResult result) {
            updateCircles(result.getCircles(), true);
            updateAps(result.getAps(), true);
            return Observable.just(getCircles()).subscribeOn(Schedulers.io());
        }
    }

    private void updateCircles(List<CircleEntity> circleEntities, boolean fromServer) {
        List<CircleEntity> circleNeedSave = new LinkedList<>();
        for (CircleEntity circleEntity : circleEntities) {

            Circle circle = getCircle(circleEntity.getCircleId());
            if (null == circle) {
                circle = new Circle(circleEntity);
                circles.add(circle);
                circleNeedSave.add(circleEntity);
            } else if (circle.getName().equals(circleEntity.getName())) {
                circle.merge(circleEntity);
                circleNeedSave.add(circle.getEntity());
            }
        }

        if(fromServer && !circleNeedSave.isEmpty()) {
            TransactionManager.getInstance().addTransaction(new SaveModelTransaction(ProcessModelInfo.withModels(circleNeedSave)));
        }
    }

    private void updateAps(List<ApEntity> apEntities, boolean fromServer) {
        List<ApEntity> entitiesNeedSave = fromServer? new LinkedList<ApEntity>():Collections.<ApEntity>emptyList();

        for(ApEntity entity : apEntities) {
            ApEntity localAp = getAp(entity.getMac());
            if(null == localAp) {
                aps.add(entity);
                if(fromServer) {
                    entitiesNeedSave.add(entity);
                }
            }
        }

        if(fromServer && !entitiesNeedSave.isEmpty()) {
            TransactionManager.getInstance().addTransaction(new SaveModelTransaction(ProcessModelInfo.withModels(entitiesNeedSave)));
        }
    }
}
