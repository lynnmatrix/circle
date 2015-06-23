package com.jadenine.circle.domain;

import com.jadenine.circle.model.entity.Savable;

import java.util.List;

import rx.functions.Func1;

/**
 * Created by linym on 6/18/15.
 */
class DBMapper<E extends Savable, D extends Updatable> implements Func1<List<E>,
        List<D>> {

    private final Finder<E, D> finder;
    private final List<D> origin;

    public DBMapper(Finder<E, D> finder, List<D> origin){
        this.finder = finder;
        this.origin = origin;
    }

    @Override
    public List<D> call(List<E> userAps) {

        for (E entity : userAps) {
            D domainModel = finder.find(entity);
            if (null != domainModel) {
                domainModel.merge(entity);
            } else {
                domainModel = finder.build(entity);
                origin.add(domainModel);
                entity.save();
            }
        }
        return origin;
    }
}
