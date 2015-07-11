package com.jadenine.circle.domain;

import com.jadenine.circle.model.entity.Savable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by linym on 6/26/15.
 */
class RestResultClassifier<E extends Savable, D extends Updatable<E>> {
    private final List<E> entities;
    private final Binder<E, D> mapperDelegate;

    private List<E> receivedTopicEntities;
    private List<D> newDomainObject;

    public RestResultClassifier(List<E> entities, Binder<E, D> mapperDelegate) {
        this.entities = entities;
        this.mapperDelegate = mapperDelegate;
    }

    public List<E> getReceivedTopicEntities() {
        return receivedTopicEntities;
    }

    public List<D> getNewDomainObject() {
        return newDomainObject;
    }

    public RestResultClassifier invoke() {
        receivedTopicEntities = new ArrayList<>(entities.size());
        newDomainObject = new LinkedList<>();

        for (E entity : entities) {
            D domainObject = mapperDelegate.find(entity);

            if (null != domainObject) {
                domainObject.merge(entity);
            } else {
                domainObject = mapperDelegate.build(entity);
                newDomainObject.add(domainObject);
            }

            receivedTopicEntities.add(domainObject.getEntity());
        }
        return this;
    }
}
