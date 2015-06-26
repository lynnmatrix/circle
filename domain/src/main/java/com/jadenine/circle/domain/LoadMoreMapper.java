package com.jadenine.circle.domain;

import com.jadenine.circle.model.entity.Savable;
import com.jadenine.circle.model.rest.JSONListWrapper;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.runtime.transaction.process.SaveModelTransaction;

import java.util.List;

import rx.functions.Func1;

/**
 * Created by linym on 6/26/15.
 */
class LoadMoreMapper<E extends Savable, D extends Updatable<E>> implements Func1<JSONListWrapper<E>, List<D>> {
    private final MapperDelegate mapperDelegate;

    public LoadMoreMapper(MapperDelegate mapperDelegate) {
        this.mapperDelegate = mapperDelegate;
    }

    @Override
    public List<D> call(JSONListWrapper<E> entities) {

        RestResultClassifier restResultClassifier = new RestResultClassifier(entities, mapperDelegate).invoke();

        TransactionManager.getInstance().addTransaction(new SaveModelTransaction(ProcessModelInfo.withModels
                (restResultClassifier.getReceivedTopicEntities())));
        List<D> source = mapperDelegate.getOriginSource();

        source.addAll(restResultClassifier.getNewDomainObject());
        mapperDelegate.setHasMore(entities.hasMore());

        return source;
    }
}
