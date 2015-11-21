package com.app.fxa.joke;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.app.fxa.joke.util.AppManager;


public abstract class BaseActivity extends BaseAppCompatActivity {

    protected SharedPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppManager.activities.add(this);
        super.onCreate(savedInstanceState);
        AppManager.setPreferences(preference);
    }


}
