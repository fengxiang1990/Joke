package com.app.fxa.joke.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.fxa.joke.BaseActivity;
import com.app.fxa.joke.R;
import com.app.fxa.joke.util.AppConfig;
import com.app.fxa.joke.util.GsonUtil;
import com.app.fxa.joke.util.ThreadPool;
import com.baidu.appx.BDBannerAd;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by admin on 2015/11/20.
 */
public class GifsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    String path = Environment.getExternalStorageDirectory().getPath() + "/qutu";

    Toolbar toolbar;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    List<ImageJoke> data = new ArrayList<ImageJoke>();
    private boolean isRefresh = false;// 是否刷新中
    private static BDBannerAd bannerAdView;
    RelativeLayout adContainer;
    int loadType = 1;//0 load More,1 refresh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gifs_layout);
        initView();
    }

    void initView() {
        bannerAdView = new BDBannerAd(this, "awfNCwb6Ydq5o8jd1aGdWXbW", "5EVLvzaHidDRjYvzoem5PLG5");
        // 设置横幅广告展示尺寸，如不设置，默认为SIZE_FLEXIBLE;
        bannerAdView.setAdSize(BDBannerAd.SIZE_FLEXIBLE);
        adContainer = (RelativeLayout) findViewById(R.id.adContainer);
        // 显示广告视图
        adContainer.addView(bannerAdView);
        toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle("搞笑趣图");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GifsActivity.this.finish();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdaprter(data);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        // 加载颜色是循环播放的，只要没有完成刷新就会一直循环,
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_purple,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        registerForContextMenu(recyclerView);
        initData();
    }

    public void initData() {
        loadGifs();
    }

    @Override
    public void onRefresh() {
        if (!isRefresh) {
            loadType = 1;
            loadGifs();
        }
    }


    //上下文菜单，本例会通过长按条目激活上下文菜单
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("选择操作");
        //添加菜单项
        menu.add(0, 2, 0, "分享图片");
        menu.add(0, 1, 0, "保存图片");
    }

    //菜单单击响应
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                if (selectJoke != null) {
                    saveImg(selectJoke);
                }
                break;
            case 2:
                if (selectJoke != null) {
                    share(selectJoke);
                }
                break;
        }
        return true;
    }


    public void saveImg(final ImageJoke sellectedJoke) {
        ThreadPool.getCacheService().execute(new Runnable() {
            @Override
            public void run() {
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdir();
                }
                try {
                    URL url = new URL(sellectedJoke.getImgUrl());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    InputStream ins = conn.getInputStream();
                    File imgFile = null;
                    if (sellectedJoke.getImgUrl().contains(".gif") || sellectedJoke.getImgUrl().contains(".GIF")) {
                        imgFile = new File(file.getPath() + "/" + System.currentTimeMillis() + ".gif");
                    }
                    if (sellectedJoke.getImgUrl().contains(".jpg") || sellectedJoke.getImgUrl().contains(".JPG")) {
                        imgFile = new File(file.getPath() + "/" + System.currentTimeMillis() + ".jpg");
                    }
                    inputstreamtofile(ins, imgFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void inputstreamtofile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = ins.read(buffer, 0, 1024)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(GifsActivity.this, "已保存到;" + path, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ImageJoke selectJoke = null;

    class MyAdaprter extends RecyclerView.Adapter<MyAdaprter.ViewHolder> {

        List<ImageJoke> data;

        public MyAdaprter(List<ImageJoke> data) {
            this.data = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_gif_view, parent, false);
            return new ViewHolder(v);
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Log.i("recycler_view", "position:" + String.valueOf(position));
            final ImageJoke joke = data.get(position);
            holder.loadGif(joke.getImgUrl());
            holder.setTitle(joke.getTitle());
            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    selectJoke = joke;
                    return false;
                }
            });
            if (position == data.size() - 2) {
                loadType = 0;
                loadGifs();
            }
        }


        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView titleView;
            SimpleDraweeView draweeView;
            public CardView cardView;

            public ViewHolder(View itemView) {
                super(itemView);
                draweeView = (SimpleDraweeView) itemView.findViewById(R.id.img);
                titleView = (TextView) itemView.findViewById(R.id.title);
                cardView = (CardView) itemView.findViewById(R.id.card);
            }

            public void setTitle(String title) {
                titleView.setText(title);
            }

            public void loadGif(String url) {
                Uri uri = Uri.parse(url);
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                        .build();

                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setAutoPlayAnimations(true)
                        .build();
                draweeView.setController(controller);
            }
        }
    }

    class ImageJoke {
        private String title;
        private String imgUrl;

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isRefresh = false;
            swipeRefreshLayout.setRefreshing(false);
            Bundle bundle = msg.getData();
            String result = bundle.getString("RESULT");
            List<ImageJoke> list = GsonUtil.getJsonObject(result, new TypeToken<List<ImageJoke>>() {
            });
            if (loadType == 1) {
                data.clear();
            }
            data.addAll(list);
            adapter.notifyDataSetChanged();
        }
    };


    public int lastPageNumber = 0;

    public void loadGifs() {
        isRefresh = true;
        swipeRefreshLayout.setRefreshing(true);
        ThreadPool.getCacheService().execute(new Runnable() {
            @Override
            public void run() {
                List<ImageJoke> list = new ArrayList<ImageJoke>();
                int qutuPageNumber = new Random().nextInt(AppConfig.QUTU_PAGE_COUNT);
                if (qutuPageNumber != lastPageNumber) {
                    String url = "http://xiaohua.zol.com.cn/qutu/" + qutuPageNumber + ".html";
                    Log.i("gif_url", url);
                    try {
                        URL localURL = new URL(url);
                        Element localElement1 = Jsoup.parse(localURL, 5000).body();
                        Elements rootElements = localElement1.getElementsByClass("article-summary");
                        for (Element element : rootElements) {
                            Elements titles = element.getElementsByClass("article-title");
                            Elements summarys = element.getElementsByClass("summary-text");
                            Element summaryElement = summarys.get(0);
                            String img_url = null;
                            if (summaryElement.toString().contains("loadsrc")) {
                                img_url = summaryElement.getElementsByTag("img").get(0).attr("loadsrc");
                            } else {
                                img_url = summarys.get(0).getElementsByTag("img").get(0).attr("src");
                            }
                            String title = titles.get(0).text();
                            if (!TextUtils.isEmpty(img_url) && !TextUtils.isEmpty(title)) {
                                ImageJoke joke = new ImageJoke();
                                joke.setTitle(title);
                                joke.setImgUrl(img_url);
                                list.add(joke);
                            }
                        }
                        if (list.size() > 0) {
                            String result = GsonUtil.gson.toJson(list);
                            Bundle bd = new Bundle();
                            bd.putString("RESULT", result);
                            Message message = handler.obtainMessage();
                            message.setData(bd);
                            handler.sendMessage(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    loadGifs();
                }
                lastPageNumber = qutuPageNumber;
            }
        });
    }


    private void share(ImageJoke joke) {
        ShareSDK.initSDK(GifsActivity.this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.app_name));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(AppConfig.APP_SHARE_URL);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(joke.getTitle());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        oks.setImageUrl(joke.getImgUrl());
        // url仅在微信（包括好友和朋友圈）中使用
        //  oks.setUrl("http://shouji.baidu.com/software/item?docid=7475147&from=as");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(AppConfig.APP_SHARE_URL);
// 启动分享GUI
        oks.show(GifsActivity.this);
    }

}
