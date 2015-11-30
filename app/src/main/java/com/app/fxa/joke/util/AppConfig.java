package com.app.fxa.joke.util;

import android.content.SharedPreferences;
import android.net.Uri;

import com.app.fxa.joke.model.JokeType;

public class AppConfig {

    public static final String APP_ICON_SHARE_URL = "http://bcs.91.com/pcsuite-dev/img/0/512_512/fd31238407bb3fa4f963d22f18ed6152.png";
    public static final String APP_SHARE_URL = "http://shouji.baidu.com/software/item?docid=8175466";
    public static final int QUTU_PAGE_COUNT = 733; //现在有733页
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;

    public static final int MODE_RANDOM = 1;

    public static final int MODE_NOT_RANDOM = 2;
    public static Uri imageUri; // 图片路径
    public static String filename; // 图片名称

    public static final String PREFERENCE_NAME = "APP";
    public static String USER_NAME = "username";
    public static String PASS_WORD = "password";
    public static String MAIL = "mail";
    public static String TEL_PHONE = "tel_phone";
    public static String IS_LOGIN = "isLogin";
    public static String IS_REMEMBER_PASSWORD = "is_remember_password";

    public static String JSESSIONID = null;

    public static String DATE = "date";

    public static int getJokeType() {
        return jokeType;
    }

    public static void setJokeType(int jokeType) {
        AppConfig.jokeType = jokeType;
    }

    private static int jokeType = JokeType.NEW.getType();

    //检查是否登陆
    public static boolean isLogin(SharedPreferences preference) {
        //return preference.getBoolean(AppConfig.IS_LOGIN, false);
        return true;
    }

}
