package com.jadenine.circle.domain;

import com.jadenine.circle.model.entity.Savable;

import java.util.List;

import rx.functions.Func1;

/**
 * Created by linym on 6/18/15.
 */
class RestMapper<E extends Savable, D extends Updatable>  implements Func1<E, D> {
    final MapperDelegate<E, D> mapperDelegate;
    private final List<D> origin;

    public RestMapper(MapperDelegate<E, D> mapperDelegate, List<D> origin){
        this.mapperDelegate = mapperDelegate;
        this.origin = origin;
    }

    @Override
    public D call(E e) {
        D domainModel = mapperDelegate.find(e);
        if (null != domainModel) {
            domainModel.merge(e);
        } else {
            domainModel = mapperDelegate.build(e);
            origin.add(domainModel);
            e.save();
        }
        return domainModel;
    }

    List<D> getOrigin() {
        return origin;
    }

    MapperDelegate<E, D> getMapperDelegate() {
        return mapperDelegate;
    }
}
