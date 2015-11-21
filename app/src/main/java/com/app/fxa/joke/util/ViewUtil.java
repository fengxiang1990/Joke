package com.app.fxa.joke.util;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;

public class ViewUtil {

    public static void hide(View view) {
        view.setVisibility(View.GONE);
    }


    public static void show(View view) {
        view.setVisibility(View.VISIBLE);
    }


    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal, Resources resources) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, resources.getDisplayMetrics());
    }

    /**
     * sp 2 px
     *
     * @param spVal
     * @return
     */
    protected int sp2px(int spVal, Resources resources) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, resources.getDisplayMetrics());

    }

}
