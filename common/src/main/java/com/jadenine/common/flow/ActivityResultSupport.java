package com.jadenine.common.flow;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.View;

/**
 * Created by linym on 7/1/15.
 */
public class ActivityResultSupport {

    public static boolean onActivityResult(View childView, int requestCode, int resultCode,
                                           Intent data) {
        if (childView instanceof PreferenceManager.OnActivityResultListener) {
            if (((PreferenceManager.OnActivityResultListener) childView).onActivityResult
                    (requestCode, resultCode, data)) {
                return true;
            }
        }
        return false;
    }

    private ActivityResultSupport() {
    }
}
