package com.app.fxa.joke.util;

import android.app.Activity;
import android.content.Intent;

public class ThemeUtil {

    public static int sTheme;

    public static void setTheme(Activity activity, int theme) {
        sTheme = theme;
        for (Activity act : AppManager.activities) {
            act.finish();
            act.startActivity(new Intent(act, act.getClass()));
        }
    }

    /**
     * Set the theme of the activity, according to the configuration.
     */
    public static void onActivityCreateSetTheme(Activity activity) {
        activity.setTheme(sTheme);
    }
}
