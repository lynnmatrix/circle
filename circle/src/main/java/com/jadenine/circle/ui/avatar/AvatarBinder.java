package com.jadenine.circle.ui.avatar;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by linym on 7/25/15.
 */

public class AvatarBinder {
    private final static int AVATAR_COUNT = 109;
    private int[] avatars = new int[AVATAR_COUNT];

    public AvatarBinder(Context context) {
        prepare(context);
    }

    public int getAvatar(String user, String scope) {
        int index = (user + scope).hashCode()%AVATAR_COUNT;
        return avatars[index];
    }

    private void prepare(Context context) {
        String prefix = "ic_avatar_";
        String defType = "drawable";
        String defPackage = "com.jadenine.circle";

        Resources resources = context.getResources();
        for (int i = 1; i<= AVATAR_COUNT;i++) {
            String name = prefix + i;
            int identifier = resources.getIdentifier(name, defType, defPackage);
            avatars[i-1] =  identifier;
        }
    }
}
