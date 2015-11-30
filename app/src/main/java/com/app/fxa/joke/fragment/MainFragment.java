package com.app.fxa.joke.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.fxa.joke.BaseFragment;
import com.app.fxa.joke.R;
import com.app.fxa.joke.activity.GifsActivity;
import com.app.fxa.joke.model.Joke;
import com.app.fxa.joke.model.JokeAdapter;
import com.app.fxa.joke.model.JokeType;
import com.app.fxa.joke.service.DataService;
import com.app.fxa.joke.util.AppConfig;
import com.app.fxa.joke.util.ThreadPool;
import com.baidu.appx.BDBannerAd;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.orm.SugarRecord;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by chengxu1 on 2015/8/20.
 */
public class MainFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {


    String TAG = MainFragment.class.getName();

    public static final int LOAD_JOKES_SUCCESS = 0;
    public static final int LOAD_IMG_SUCCESS = 10;

    View view = null;

    ListView listView;
    SimpleDraweeView draweeView;
    SimpleDraweeView draweeView2;
    SimpleDraweeView draweeView3;
    CardView cardView;
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout adContainer;

    private static BDBannerAd bannerAdView;
    private JokeAdapter adapter;

    public DataService getDataService() {
        return dataService;
    }

    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    private DataService dataService;
    private List<Joke> jokes;
    public int pageNumber = 1;
    private int type = JokeType.ALL.getType();//默认为全部
    private int selectIndex = -1;


    private boolean isRefresh = false;// 是否刷新中
    View topView;
    View topView2;
    TextView btn1;
    TextView btn2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_layout, null);
        initView(view);
        return view;
    }


    public void loadHeaderGif() {
        ThreadPool.getCacheService().execute(new Runnable() {
            @Override
            public void run() {
                int qutuPageNumber = new Random().nextInt(AppConfig.QUTU_PAGE_COUNT);
                String url = "http://xiaohua.zol.com.cn/qutu/" + qutuPageNumber + ".html";
                Log.i("gif_url", url);
                try {
                    URL localURL = new URL(url);
                    Element localElement1 = Jsoup.parse(localURL, 5000).body();
                    Elements rootElements = localElement1.getElementsByClass("article-summary");
                    Bundle bd = new Bundle();
                    int count = 0;
                    for (int i = 0; i < rootElements.size(); i++) {
                        String img_url;
                        Element element = rootElements.get(i);
                        if (element.getElementsByTag("img").toString().contains("loadsrc")) {
                            img_url = element.getElementsByTag("img").get(0).attr("loadsrc");
                        } else {
                            img_url = element.getElementsByTag("img").get(0).attr("src");
                        }
                        if (!TextUtils.isEmpty(img_url)) {
                            bd.putString("IMG_URL" + (i + 1), img_url);
                            count++;
                            if (count == 3) {
                                break;
                            }
                        }
                    }
                    Message msg = handler.obtainMessage();
                    msg.what = LOAD_IMG_SUCCESS;
                    msg.setData(bd);
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void initView(View rootView) {
        bannerAdView = new BDBannerAd(getActivity(), "awfNCwb6Ydq5o8jd1aGdWXbW", "MdsZPWxwsIGoyUDGeeaBjynD");
        // 设置横幅广告展示尺寸，如不设置，默认为SIZE_FLEXIBLE;
        bannerAdView.setAdSize(BDBannerAd.SIZE_FLEXIBLE);

        listView = (ListView) rootView.findViewById(R.id.listView);
        topView = LayoutInflater.from(getActivity()).inflate(R.layout.joke_list_top, null);
        listView.addHeaderView(topView);
        topView2 = LayoutInflater.from(getActivity()).inflate(R.layout.item_header_menu, null);
        listView.addHeaderView(topView2);
        adContainer = (RelativeLayout) topView.findViewById(R.id.adContainer);
        // 显示广告视图
        adContainer.addView(bannerAdView);
        draweeView = (SimpleDraweeView) topView.findViewById(R.id.img);
        draweeView2 = (SimpleDraweeView) topView.findViewById(R.id.img2);
        draweeView3 = (SimpleDraweeView) topView.findViewById(R.id.img3);
        btn1 = (TextView) topView2.findViewById(R.id.btn_1);
        btn2 = (TextView) topView2.findViewById(R.id.btn_2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GifsActivity.class);
                startActivity(intent);
            }
        });
        cardView = (CardView) topView.findViewById(R.id.card);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GifsActivity.class);
                startActivity(intent);
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        // 加载颜色是循环播放的，只要没有完成刷新就会一直循环,
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_purple,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        jokes = new ArrayList<Joke>();
        adapter = new JokeAdapter(MainFragment.this, jokes);
        listView.setAdapter(adapter);
        loadData(pageNumber, JokeType.NEW.getType(), handler, AppConfig.MODE_NOT_RANDOM);
    }

    public void loadData(int pageNumber, int jokeType, Handler handler, int refreshType) {
        loadHeaderGif();
        isRefresh = true;
        swipeRefreshLayout.setRefreshing(true);
        try {
            if (dataService != null) {
                List<Joke> datas = dataService.get(pageNumber, jokeType, handler, refreshType);
                Log.i(TAG, String.valueOf(jokes.size()));
                if (!jokes.isEmpty()) {
                    jokes.clear();
                }
                for (Joke joke : datas) {
                    jokes.add(joke);
                }
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOAD_JOKES_SUCCESS:
                    isRefresh = false;
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case LOAD_IMG_SUCCESS:
                    Bundle bd = msg.getData();
                    String img_url1 = bd.getString("IMG_URL1");
                    String img_url2 = bd.getString("IMG_URL2");
                    String img_url3 = bd.getString("IMG_URL3");
                    //首页默认GIF
                    Uri uri = Uri.parse(img_url1);
                    ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                            .build();

                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setAutoPlayAnimations(true)
                            .build();
                    draweeView.setController(controller);

                    uri = Uri.parse(img_url2);
                    request = ImageRequestBuilder.newBuilderWithSource(uri)
                            .build();
                    controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setAutoPlayAnimations(true)
                            .build();
                    draweeView2.setController(controller);

                    uri = Uri.parse(img_url3);
                    request = ImageRequestBuilder.newBuilderWithSource(uri)
                            .build();
                    controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setAutoPlayAnimations(true)
                            .build();
                    draweeView3.setController(controller);
                    break;
            }

        }
    };

    @Override
    public void onRefresh() {
        if (!isRefresh) {
            loadData(pageNumber, AppConfig.getJokeType(), handler, AppConfig.MODE_RANDOM);
        }
    }


    private void showShare(Joke joke) {
        ShareSDK.initSDK(getActivity());
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.app_name));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(AppConfig.APP_SHARE_URL);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(joke.getContent());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //  oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        oks.setImageUrl(AppConfig.APP_ICON_SHARE_URL);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(AppConfig.APP_SHARE_URL);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(AppConfig.APP_SHARE_URL);

// 启动分享GUI
        oks.show(getActivity());
    }
    public void shareTo(Joke joke) {
        showShare(joke);
    }

    public boolean checkIsSave(Joke joke) {
        String sql = "select * from joke where title=? and content=?";
        List<Joke> jokes = SugarRecord.findWithQuery(Joke.class, sql, joke.getTitle(), joke.getContent());
        return jokes != null && jokes.size() > 0;
    }

    public void save(Joke joke) {
        joke.setDate(new Date());
        if (checkIsSave(joke)) {
            Toast.makeText(getActivity(), "笑话已经收藏过了", Toast.LENGTH_SHORT).show();
        } else {
            joke.save();
            Toast.makeText(getActivity(), "收藏成功", Toast.LENGTH_SHORT).show();
        }
    }
}
