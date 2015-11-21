package com.app.fxa.joke.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GsonUtil {

    public static Gson gson = new Gson();

    public static <T> T getJsonObject(String json, TypeToken<T> token) {
        try {
            return gson.fromJson(json, token.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static <T> T getJsonObject(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

}
