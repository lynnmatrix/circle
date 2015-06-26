package com.jadenine.circle.domain;

import com.jadenine.circle.model.entity.Savable;

import java.util.List;

import rx.functions.Func1;

/**
 * Created by linym on 6/18/15.
 */
class DBMapper<E extends Savable, D extends Updatable<E>> implements Func1<List<E>,
        List<D>> {

    private final MapperDelegate<E, D> mapperDelegate;

    public DBMapper(MapperDelegate<E, D> mapperDelegate){
        this.mapperDelegate = mapperDelegate;
    }

    @Override
    public List<D> call(List<E> userAps) {

        for (E entity : userAps) {
            D domainModel = mapperDelegate.find(entity);
            if (null == domainModel) {
                domainModel = mapperDelegate.build(entity);
                mapperDelegate.getOriginSource().add(domainModel);
            }
        }
        return mapperDelegate.getOriginSource();
    }
}
