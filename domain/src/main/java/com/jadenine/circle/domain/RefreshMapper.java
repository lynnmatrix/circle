package com.jadenine.circle.domain;

import com.jadenine.circle.model.entity.Savable;
import com.jadenine.circle.model.rest.JSONListWrapper;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.runtime.transaction.process.SaveModelTransaction;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

/**
 * Created by linym on 6/26/15.
 */
class RefreshMapper<E extends Savable, D extends Updatable<E>> implements
        Func1<JSONListWrapper<E>, List<D>> {
    private final MapperDelegate mapperDelegate;

    public RefreshMapper(MapperDelegate mapperDelegate) {
        this.mapperDelegate = mapperDelegate;
    }

    @Override
    public List<D> call(JSONListWrapper<E> entities) {
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
