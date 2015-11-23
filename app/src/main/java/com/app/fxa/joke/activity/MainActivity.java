package com.app.fxa.joke.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.app.fxa.joke.BaseActivity;
import com.app.fxa.joke.R;
import com.app.fxa.joke.fragment.MainFragment;
import com.app.fxa.joke.model.JokeType;
import com.app.fxa.joke.service.DataService;
import com.app.fxa.joke.util.AppConfig;
import com.app.fxa.joke.view.DrawerArrowDrawable;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout mainLeft;
    DrawerLayout drawerLayout;

    public DataService dataService;
    boolean isBind = false;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    MainFragment mainFragment;

    CardView btn_new;
    CardView btn_all;
    CardView btn_cold;
    CardView btn_humor;
    CardView btn_love;
    CardView btn_high;
    CardView btn_shcool;
    CardView btn_audit;
    CardView btn_child;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    void initView() {
        Resources resources = getResources();
        DrawerArrowDrawable drawerArrowDrawable = new DrawerArrowDrawable(resources);
        drawerArrowDrawable.setStrokeColor(resources
                .getColor(R.color.lightgray));
        toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle("今日推荐");
        toolbar.setSubtitle("下拉可以换一批笑话哟~");
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.lightgray));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(drawerArrowDrawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleLeftLayout();
            }
        });
        btn_new = (CardView) this.findViewById(R.id.btn_new);
        btn_cold = (CardView) this.findViewById(R.id.btn_cold);
        btn_humor = (CardView) this.findViewById(R.id.btn_humor);
        btn_all = (CardView) this.findViewById(R.id.btn_all);
        btn_love = (CardView) this.findViewById(R.id.btn_love);
        btn_high = (CardView) this.findViewById(R.id.btn_high);
        btn_shcool = (CardView) this.findViewById(R.id.btn_school);
        btn_audit = (CardView) this.findViewById(R.id.btn_audit);
        btn_child = (CardView) this.findViewById(R.id.btn_child);
        btn_new.setOnClickListener(this);
        btn_cold.setOnClickListener(this);
        btn_humor.setOnClickListener(this);
        btn_all.setOnClickListener(this);
        btn_love.setOnClickListener(this);
        btn_high.setOnClickListener(this);
        btn_shcool.setOnClickListener(this);
        btn_audit.setOnClickListener(this);
        btn_child.setOnClickListener(this);

        mainLeft = (LinearLayout) this.findViewById(R.id.main_left);
        drawerLayout = (DrawerLayout) this.findViewById(R.id.main_drawer_layout);
        mainFragment = new MainFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, mainFragment);
        fragmentTransaction.commit();

    }


    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, DataService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Bind to LocalService
        Intent intent = new Intent(this, DataService.class);
        unbindService(mConnection);
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            DataService.JokeDataBinder binder = (DataService.JokeDataBinder) service;
            dataService = binder.getService();
            mainFragment.setDataService(dataService);
            mainFragment.loadData(mainFragment.pageNumber, JokeType.NEW.getType(), mainFragment.handler, AppConfig.MODE_NOT_RANDOM);
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBind = false;
        }
    };

    @Override
    public void onClick(View v) {
        mainFragment.pageNumber = 1;
        switch (v.getId()) {
            case R.id.btn_new:
                toggleLeftLayout();
                toolbar.setTitle("最新笑话");
                mainFragment.loadData(mainFragment.pageNumber, JokeType.NEW.getType(), mainFragment.handler, AppConfig.MODE_NOT_RANDOM);
                AppConfig.setJokeType(JokeType.NEW.getType());
                break;
            case R.id.btn_all:
                toggleLeftLayout();
                toolbar.setTitle("全部笑话");
                mainFragment.loadData(mainFragment.pageNumber, JokeType.ALL.getType(), mainFragment.handler, AppConfig.MODE_NOT_RANDOM);
                AppConfig.setJokeType(JokeType.ALL.getType());
                break;
            case R.id.btn_cold:
                toggleLeftLayout();
                toolbar.setTitle("冷笑话");
                mainFragment.loadData(mainFragment.pageNumber, JokeType.COLD.getType(), mainFragment.handler, AppConfig.MODE_NOT_RANDOM);
                AppConfig.setJokeType(JokeType.COLD.getType());
                break;
            case R.id.btn_humor:
                toggleLeftLayout();
                toolbar.setTitle("幽默笑话");
                mainFragment.loadData(mainFragment.pageNumber, JokeType.HUMOR.getType(), mainFragment.handler, AppConfig.MODE_NOT_RANDOM);
                AppConfig.setJokeType(JokeType.HUMOR.getType());
                break;
            case R.id.btn_love:
                toggleLeftLayout();
                toolbar.setTitle("爱情笑话");
                mainFragment.loadData(mainFragment.pageNumber, JokeType.LOVE.getType(), mainFragment.handler, AppConfig.MODE_NOT_RANDOM);
                AppConfig.setJokeType(JokeType.LOVE.getType());
                break;
            case R.id.btn_high:
                toggleLeftLayout();
                toolbar.setTitle("爆笑");
                mainFragment.loadData(mainFragment.pageNumber, JokeType.HIGH.getType(), mainFragment.handler, AppConfig.MODE_NOT_RANDOM);
                AppConfig.setJokeType(JokeType.HIGH.getType());
                break;
            case R.id.btn_school:
                toggleLeftLayout();
                toolbar.setTitle("校园笑话");
                mainFragment.loadData(mainFragment.pageNumber, JokeType.SCHOOL.getType(), mainFragment.handler, AppConfig.MODE_NOT_RANDOM);
                AppConfig.setJokeType(JokeType.SCHOOL.getType());
                break;
            case R.id.btn_audit:
                toggleLeftLayout();
                toolbar.setTitle("成人笑话");
                mainFragment.loadData(mainFragment.pageNumber, JokeType.AUDIT.getType(), mainFragment.handler, AppConfig.MODE_NOT_RANDOM);
                AppConfig.setJokeType(JokeType.AUDIT.getType());
                break;
            case R.id.btn_child:
                toggleLeftLayout();
                toolbar.setTitle("儿童笑话");
                mainFragment.loadData(mainFragment.pageNumber, JokeType.CHILDREN.getType(), mainFragment.handler, AppConfig.MODE_NOT_RANDOM);
                AppConfig.setJokeType(JokeType.CHILDREN.getType());
                break;
        }
    }

    public void toggleLeftLayout() {
        if (drawerLayout.isDrawerOpen(mainLeft)) {
            drawerLayout.closeDrawer(mainLeft);
        } else {
            drawerLayout.openDrawer(mainLeft);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_menu_mine:
                Intent intent = new Intent(MainActivity.this, MyJokeActivity.class);
                startActivity(intent);
                return true;
            case R.id.item_menu_gif:
                intent = new Intent(MainActivity.this, GifsActivity.class);
                startActivity(intent);
                return true;
            case R.id.item_menu_about:
                intent = new Intent(MainActivity.this, AboutWebActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this).setTitle("确认退出吗？")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 点击“确认”后的操作
                            MainActivity.this.finish();

                        }
                    })
                    .setNegativeButton("返回", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 点击“返回”后的操作,这里不设置没有任何操作
                        }
                    }).show();
        }
        return super.onKeyDown(keyCode, event);
    }

}