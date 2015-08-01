package com.jadenine.circle.domain;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by linym on 7/11/15.
 */
public class DomainUtils {

    @NonNull
    public static <E> List<E> getEntities(List<? extends ApSource.Updatable<E>> domainModels) {
        List<E> entities = new ArrayList<>(domainModels.size());
        for(ApSource.Updatable<E> topic : domainModels) {
            entities.add(topic.getEntity());
        }
        return entities;
    }

    public static boolean checkEmpty(Collection collection) {
        return null == collection || collection.isEmpty();
    }

    public static int getSize(Collection collection) {
        return checkEmpty(collection) ? 0 : collection.size();
    }

    public static boolean checkEmpty(CharSequence str) {
        return TextUtils.isEmpty(str);
    }
}
