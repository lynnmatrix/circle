package com.jadenine.circle.domain;

import com.jadenine.circle.model.entity.Savable;

import rx.functions.Func1;

/**
 * Created by linym on 6/18/15.
 */
class RestMapper<E extends Savable, D extends Updatable> implements Func1<E, D> {
    final MapperDelegate<E, D> mapperDelegate;

    public RestMapper(MapperDelegate<E, D> mapperDelegate) {
        this.mapperDelegate = mapperDelegate;
    }

    @Override
    public D call(E e) {
        D domainModel = mapperDelegate.find(e);
        if (null != domainModel) {
            domainModel.merge(e);
        } else {
            domainModel = mapperDelegate.build(e);
            mapperDelegate.getOriginSource().add(0, domainModel);
            e.save();
        }
        return domainModel;
    }
}