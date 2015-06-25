package com.jadenine.circle.domain;

import com.jadenine.circle.model.entity.Savable;

import java.util.List;

import rx.functions.Func1;

/**
 * Created by linym on 6/18/15.
 */
class DBMapper<E extends Savable, D extends Updatable> implements Func1<List<E>,
        List<D>> {

    private final MapperDelegate<E, D> mapperDelegate;
    private final List<D> origin;

    public DBMapper(MapperDelegate<E, D> mapperDelegate, List<D> origin){
        this.mapperDelegate = mapperDelegate;
        this.origin = origin;
    }

    @Override
    public List<D> call(List<E> userAps) {

        for (E entity : userAps) {
            D domainModel = mapperDelegate.find(entity);
            if (null != domainModel) {
                domainModel.merge(entity);
            } else {
                domainModel = mapperDelegate.build(entity);
                origin.add(domainModel);
                entity.save();
            }
        }
        return origin;
    }
}
