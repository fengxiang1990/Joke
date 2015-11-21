package com.app.fxa.joke.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.app.fxa.joke.model.Joke;
import com.app.fxa.joke.model.JokeType;
import com.app.fxa.joke.util.AppConfig;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class DataService extends Service {
    private static final int TIME_OUT = 50000;
    private final IBinder binder;
    private Handler handler;
    private Set<String> pageNumbers;
    private SharedPreferences preferences;
    private Random random;

    public DataService() {
        JokeDataBinder localJokeDataBinder = new JokeDataBinder();
        this.binder = localJokeDataBinder;
        Random localRandom = new Random();
        this.random = localRandom;
        HashSet localHashSet = new HashSet();
        this.pageNumbers = localHashSet;
    }

    public List<Joke> get(int paramInt1, int paramInt2, Handler paramHandler, int refreshType)
            throws ExecutionException, InterruptedException {
        this.handler = paramHandler;
        DataTask localDataTask = new DataTask();
        Integer[] arrayOfInteger = new Integer[3];
        arrayOfInteger[0] = Integer.valueOf(paramInt1);
        arrayOfInteger[1] = Integer.valueOf(paramInt2);
        arrayOfInteger[2] = Integer.valueOf(refreshType);
        return (List) localDataTask.execute(arrayOfInteger).get();
    }

    public int getPageNumberInTuiJianMode() {
        return this.random.nextInt(1400);
    }

    public String getTimeKey() {
        Date localDate = new Date();
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return localSimpleDateFormat.format(localDate);
    }

    public IBinder onBind(Intent paramIntent) {
        return this.binder;
    }

    public void onCreate() {
        super.onCreate();
        this.preferences = getSharedPreferences("service_preference", 0);
        while (this.pageNumbers.size() < 10) {
            int j = this.random.nextInt(1400);
            this.pageNumbers.add(String.valueOf(j));
        }
        Editor localEditor = this.preferences.edit();
        if (!this.preferences.contains(getTimeKey()))
            localEditor.putStringSet(getTimeKey(), this.pageNumbers);
        Date localDate = new Date();
        int i = localDate.getDay();
        Log.i("day:", String.valueOf(i));
        if (i % 3 == 0)
            localEditor.clear();
    }

    class DataTask extends AsyncTask<Integer, Integer, List<Joke>> {
        DataTask() {
        }

        protected List<Joke> doInBackground(Integer[] paramArrayOfInteger) {
            return get(paramArrayOfInteger[0].intValue(), paramArrayOfInteger[1].intValue(), paramArrayOfInteger[2].intValue());
        }

        public List<Joke> get(int paramInt1, int paramInt2, int refreshType) {
            Log.i("joke service", String.valueOf(paramInt2));
            List<Joke> jokes = new ArrayList<Joke>();
            int i = 1;
            StringBuilder localStringBuilder = new StringBuilder();
            if (refreshType == AppConfig.MODE_RANDOM) {
                if (paramInt2 != JokeType.ALL.getType()) {
                    i = random.nextInt(50);
                }
            } else if (refreshType == AppConfig.MODE_NOT_RANDOM) {
                i = paramInt1;
            }
            if (paramInt2 == JokeType.ALL.getType()) {
                i = DataService.this.getPageNumberInTuiJianMode();
                localStringBuilder.append("http://xiaohua.zol.com.cn/new/");
            }
            if (paramInt2 == JokeType.COLD.getType()) {
                localStringBuilder.append("http://xiaohua.zol.com.cn/lengxiaohua/");
            }
            if (paramInt2 == JokeType.HUMOR.getType()) {
                localStringBuilder.append("http://xiaohua.zol.com.cn/youmo/");
            }
            if (paramInt2 == JokeType.LOVE.getType()) {
                localStringBuilder.append("http://xiaohua.zol.com.cn/aiqing/");
            }
            if (paramInt2 == JokeType.HIGH.getType()) {
                localStringBuilder.append("http://xiaohua.zol.com.cn/baoxiao/");
            }
            if (paramInt2 == JokeType.SCHOOL.getType()) {
                localStringBuilder.append("http://xiaohua.zol.com.cn/xiaoyuan/");
            }
            if (paramInt2 == JokeType.CHILDREN.getType()) {
                localStringBuilder.append("http://xiaohua.zol.com.cn/ertong/");
            }
            if (paramInt2 == JokeType.AUDIT.getType()) {
                i = random.nextInt(16);
                localStringBuilder.append("http://xiaohua.zol.com.cn/chengren/");
            }
            if (paramInt2 == JokeType.NEW.getType()) {
                localStringBuilder.append("http://xiaohua.zol.com.cn/new/");
            }
            if (i > 0) {
                localStringBuilder.append(i).append(".html");
            }
            Log.i("url:", localStringBuilder.toString());
            try {
                URL localURL = new URL(localStringBuilder.toString());
                Element localElement1 = Jsoup.parse(localURL, TIME_OUT).body();
                Elements localElements1 = localElement1.getElementsByClass("article-title");
                Elements localElements2 = localElement1.getElementsByClass("summary-text");
                int j = 0;
                while (j < localElements1.size()) {
                    Element localElement2 = localElements1.get(j);
                    Element localElement3 = localElements2.get(j);
                    Joke localJoke = new Joke();
                    localJoke.setTitle(localElement2.text());
                    localJoke.setContent(localElement3.text());
                    jokes.add(localJoke);
                    j++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jokes;
        }


        protected void onPostExecute(List<Joke> paramList) {
            super.onPostExecute(paramList);
            DataService.this.handler.sendEmptyMessage(0);
        }
    }

    public class JokeDataBinder extends Binder {
        public JokeDataBinder() {
        }

        public DataService getService() {
            return DataService.this;
        }
    }
}
