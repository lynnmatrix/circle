package com.jadenine.circle.ui.avatar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

import com.jadenine.circle.R;

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
        if(index < 0) {
            index += AVATAR_COUNT;
        }
        return avatars[index];
    }

    public SpannableStringBuilder getAtAvatarSpan(Context context, int avatarResId, float textSize){
        String replyType = context.getString(R.string.reply_type_public);

        Drawable avatarDrawable = context.getResources().getDrawable(avatarResId);
        avatarDrawable.setBounds(0, 0, (int)textSize, (int)textSize);

        ImageSpan toAvatarSpan = new ImageSpan(avatarDrawable);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(replyType + "  ");
        builder.setSpan(toAvatarSpan, 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return builder;
    }

    public SpannableStringBuilder getAvatarSpan(Context context, int avatarResId, float textSize) {
        Drawable avatarDrawable = context.getResources().getDrawable(avatarResId);
        avatarDrawable.setBounds(0, 0, (int) textSize, (int) textSize);

        ImageSpan toAvatarSpan = new ImageSpan(avatarDrawable);
        SpannableStringBuilder builder = new SpannableStringBuilder(" ");
        builder.setSpan(toAvatarSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return builder;
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
