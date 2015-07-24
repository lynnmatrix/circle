package com.jadenine.circle.domain;

import android.support.annotation.NonNull;

import com.jadenine.circle.model.Identifiable;

import java.util.Comparator;
import java.util.List;

/**
 * Created by linym on 7/17/15.
 */
public class Group<T extends Identifiable<Long>> implements Identifiable<Long>{
    private final Long groupId;
    private final SortedCollection<Long, T> entities;

    public Group(Long groupId){
        this.groupId = groupId;
        entities = new SortedCollection<>(new Comparator<T>() {
            @Override
            public int compare(T lhs, T rhs) {
                return (int)(lhs.getId() - rhs.getId());
            }
        });
    }

    @NonNull
    @Override
    public Long getId() {
        return groupId;
    }

    @NonNull
    @Override
    public Long getGroupId() {
        return groupId;
    }

    public void addEntity(T entity) {
        entities.put(entity);
    }

    public List<T> getEntities() {
        return entities.getAll();
    }

    public boolean hasRootEntity(){
        return null != entities.get(groupId);
    }

    public T getRoot() {
        return entities.get(groupId);
    }

    public T get(Long entityId) {
        return entities.get(entityId);
    }

    public int getCount() {
        return entities.size();
    }
}
