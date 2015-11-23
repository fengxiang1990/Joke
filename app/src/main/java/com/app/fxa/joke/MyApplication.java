package com.app.fxa.joke;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.app.fxa.joke.util.AppManager;
import com.app.fxa.joke.util.BitmapLruCache;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.orm.SugarApp;

public class MyApplication extends SugarApp {


    //volley image
    ImageLoader imageLoader;
    RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("applicayion", "onCreate");
        requestQueue = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(requestQueue, new BitmapLruCache());
        AppManager.setImageLoader(imageLoader);
        Fresco.initialize(this);//111111
    }

}
