package com.jadenine.circle.ui.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by linym on 7/2/15.
 */
public class SoftKeyboardToggler {
    /**
     * 隐藏键盘和去掉焦点
     */
    public static void toggleInputMethod(View focusView, boolean show) {
        if(null == focusView) {
            return;
        }
        InputMethodManager inputManager = (InputMethodManager) focusView.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager.isActive() && focusView != null) {
            if (show) {
                if (focusView.requestFocus()) {
                    inputManager.showSoftInput(focusView, InputMethodManager.SHOW_IMPLICIT);
                }
            } else {
                inputManager.hideSoftInputFromWindow(focusView.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                focusView.clearFocus();
            }
        }
    }

}
