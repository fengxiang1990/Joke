package com.app.fxa.joke.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

    private ThreadPool() {
    }

    private static ExecutorService executorService = Executors
            .newSingleThreadExecutor();

    private static ExecutorService cacheService = Executors
            .newCachedThreadPool();

    public static ExecutorService getSingleThread() {

        return executorService;

    }

    public static ExecutorService getCacheService() {

        return cacheService;

    }
}
