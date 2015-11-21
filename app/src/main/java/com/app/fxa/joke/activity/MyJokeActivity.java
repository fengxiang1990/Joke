package com.app.fxa.joke.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.app.fxa.joke.BaseActivity;
import com.app.fxa.joke.R;
import com.app.fxa.joke.model.Joke;
import com.app.fxa.joke.model.JokeAdapter;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by chengxu1 on 2015/9/6.
 */
public class MyJokeActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private boolean isRefresh = false;// 是否刷新中
    ListView listView;
    List<Joke> jokes = new ArrayList<Joke>();
    JokeAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView warnText;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.acivity_myjoke_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("我的笑话");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyJokeActivity.this.finish();
            }
        });
        listView = (ListView) this.findViewById(R.id.listView);
        adapter = new JokeAdapter(this, jokes);
        listView.setAdapter(adapter);
        warnText = (TextView) this.findViewById(R.id.warnText);
        warnText.setVisibility(View.GONE);
        swipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        // 加载颜色是循环播放的，只要没有完成刷新就会一直循环,
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_purple,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        new Thread() {
            public void run() {
                List<Joke> jokesn = SugarRecord.find(Joke.class, null, null, null, "date desc", null);
                if (!jokes.isEmpty()) {
                    jokes.clear();
                }
                for (Joke joke : jokesn) {
                    jokes.add(joke);
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.notifyDataSetChanged();
            isRefresh = false;
        }
    };


    private void showShare(String content) {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //   oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }


    public void shareTo(Joke joke) {
        showShare(joke.getTitle() + " \n" + joke.getContent());
    }

    @Override
    public void onRefresh() {

        if (!isRefresh) {
            isRefresh = true;
            handler.post(new Runnable() {
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                    new Thread() {
                        public void run() {
                            List<Joke> jokesn = SugarRecord.find(Joke.class, null, null, null, "date desc", null);
                            if (!jokes.isEmpty()) {
                                jokes.clear();
                            }
                            for (Joke joke : jokesn) {
                                jokes.add(joke);
                            }
                            handler.sendEmptyMessage(0);
                        }
                    }.start();

                }
            });
        }

    }


}
