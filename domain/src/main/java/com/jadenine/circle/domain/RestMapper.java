package com.jadenine.circle.domain;

import com.jadenine.circle.model.entity.Savable;

import java.util.List;

import rx.functions.Func1;

/**
 * Created by linym on 6/18/15.
 */
class RestMapper<E extends Savable, D extends Updatable>  implements Func1<E, D> {
    private final Finder<E, D> finder;
    private final List<D> origin;

    public RestMapper(Finder<E, D> finder, List<D> origin){
        this.finder = finder;
        this.origin = origin;
    }

    @Override
    public D call(E e) {
        D domainModel = finder.find(e);
        if (null != domainModel) {
            domainModel.merge(e);
        } else {
            domainModel = finder.bind(e);
            origin.add(domainModel);
            e.save();
        }
        return domainModel;
    }

    List<D> getOrigin() {
        return origin;
    }
}
