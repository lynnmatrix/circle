package com.jadenine.circle.domain;

import android.support.annotation.NonNull;

import com.jadenine.circle.model.Identifiable;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 *
 * Caution: 要求稳定排序
 * Created by linym on 7/7/14.
 */
class SortedCollection<K, T extends Identifiable<K>> {

    private final HashMap<K, T> entityMap = new HashMap<>();
    private final ArrayList<T> entityList = new ArrayList<T>();

    private final Comparator<T> comparator;

    private final HashMap<K, Integer> estimatedIndexMap = new HashMap<>();

    public SortedCollection(@NonNull Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public synchronized T get(K id) {
        T item = entityMap.get(id);

        return item;
    }

    public synchronized T getAt(int index) {
        if(index <0 || index >= size()) {
            throw new ArrayIndexOutOfBoundsException("Index "+index +", count " + size());
        }
        return entityList.get(index);
    }

    public synchronized boolean contains(T entry) {
        K key = entry.getId();
        return entityMap.get(key) != null;
    }

    public synchronized @NonNull List<T> getAll() {
        if(entityList.isEmpty()) {
            return Collections.emptyList();
        } else {
            return new ArrayList<>(entityList);
        }
    }

    public synchronized void put(final T item){
        checkNull(item);

        insertToList(item);
        entityMap.put(item.getId(), item);
    }

    protected static void checkNull(Identifiable item) {
        if (null == item) {
            throw new InvalidParameterException("Null parameter");
        }
    }

    public synchronized void remove(T item){
        checkNull(item);

        if(!contains(item)) {
            return;
        }

        removeFromList(item);

        entityMap.remove(item.getId());

    }

    public synchronized void remove(K id) {
        T item = get(id);
        if(null != item) {
            remove(item);
        }
    }

    private void removeFromList(T item) {
        int index = indexOfItem(item);
        if(index >= 0) {
            entityList.remove(index);
        }
        estimatedIndexMap.remove(item.getId());
    }

    public synchronized int size() {
        return entityMap.size();
    }

    /**
     * @return the position to insert current item. If the same item is already at the right
     * position of the collection , return -1;
     */
    private int positionToInsert(T item) {
        if(contains(item)) {
            return -1;
        }
        if (size() > 0) {
            T firstItem = entityList.get(0);
            if (comparator.compare(item, firstItem) < 0) {
                return 0;
            }
            T lastItem = entityList.get(entityList.size() - 1);
            if (comparator.compare(lastItem, item) < 0) {
                return entityList.size();
            }
        }

        int position = Collections.binarySearch(entityList, item, comparator);
        if(position < 0) {
            position =  -position - 1;
        }
        return position;
    }

    public synchronized void reorder(T item) {
        if (needReorder(item)) {
            remove(item);
            put(item);
        }
    }

    /**
     * @return {@code true } if the current item is at the right position of the list.
     */
    private boolean needReorder(T item) {
        boolean needReorder = false;

        int index = indexOfItem(item);
        if(index >= 0) {
            if(index - 1 >= 0) {
                T itemBefore = entityList.get(index - 1);
                needReorder |= comparator.compare(itemBefore, item) > 0;
            }

            if(!needReorder && index + 1 <= entityList.size() - 1) {
                T itemAfter = entityList.get(index + 1);
                needReorder |= comparator.compare(item, itemAfter) > 0;
            }
        }

        return needReorder;
    }

    /**
     * @return  -1 if not found
     */
    private int indexOfItem(T item) {
        int index = -1;
        Integer estimateLocation = estimatedIndexMap.get(item.getId());
        if (null != estimateLocation && 0 <= estimateLocation && estimateLocation < entityList
                .size() && item == entityList.get(estimateLocation)) {
            index = estimateLocation;
        } else if (contains(item)) {
            index = entityList.indexOf(item);
        }

        if(-1 == index){
            estimatedIndexMap.remove(item.getId());
        } else if(null == estimateLocation || estimateLocation != index) {
            estimatedIndexMap.put(item.getId(), index);
        }

        return index;
    }

    private void insertToList(T item) {
        int positionToInsert = positionToInsert(item);
        if(positionToInsert >=0){
            entityList.add(positionToInsert, item);
            estimatedIndexMap.put(item.getId(), positionToInsert);
        }
    }

    
}