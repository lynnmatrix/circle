package com.jadenine.circle.ui;

import android.graphics.drawable.Drawable;

import com.jadenine.circle.domain.Account;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.common.mortar.ActivityOwner;

/**
 * Created by linym on 8/14/15.
 */
public interface UiComponent {
    Drawable getErrorDrawable();
    AvatarBinder avatarBinder();

    ActivityOwner activityOwner();

    Account account();
}
