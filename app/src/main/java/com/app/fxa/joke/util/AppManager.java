package com.app.fxa.joke.util;

import android.content.SharedPreferences;

import com.android.volley.toolbox.ImageLoader;
import com.app.fxa.joke.BaseActivity;
import com.app.fxa.joke.BaseFragment;

import java.util.ArrayList;
import java.util.List;


public class AppManager {

    public static List<BaseActivity> activities = new ArrayList<BaseActivity>();
    public static List<BaseFragment> fragments = new ArrayList<BaseFragment>();


    private static ImageLoader imageLoader;

    private static SharedPreferences preferences;

    public static SharedPreferences getPreferences() {
        return preferences;
    }

    public static void setPreferences(SharedPreferences preferences) {
        AppManager.preferences = preferences;
    }

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }

    public static void setImageLoader(ImageLoader imageLoader) {
        AppManager.imageLoader = imageLoader;
    }


}
