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
