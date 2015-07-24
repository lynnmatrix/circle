package com.jadenine.circle.ui.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.jadenine.circle.R;

/**
 * Created by linym on 7/24/15.
 */
public class ContentValidater {
    private static final long CONTENT_MAX_LENGTH = 256;

    public static boolean validate(Context context, String content) {
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(context, R.string.message_invalid_empty, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(context, context.getString(R.string.message_invalid_size,
                    CONTENT_MAX_LENGTH), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}