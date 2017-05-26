package com.zebra;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zebra.adapter.BannerAdapter;
import com.zebra.adapter.EntryAdapter;
import com.zebra.base.BaseActivity;
import com.zebra.bean.Banner;
import com.zebra.contract.MainContract;
import com.zebra.login.LoginActivity;
import com.zebra.login.LoginUserUtils;
import com.zebra.presenter.MainPresenterImpl;
import com.zebra.util.Constant;
import com.zebra.view.CircleFlowIndicator;
import com.zebra.view.ViewFlow;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import static com.zebra.adapter.EntryAdapter.*;

/**
 * Created by zhiPeng.S on 2016/11/18.
 */
@ContentView(R.layout.activity_main_zebra)
public class MainActivity extends BaseActivity implements MainContract.View,OnItemClickListener{
    private MainContract.Presenter mainPresenter;

    @ViewInject(R.id.bannerVf)
    private ViewFlow viewFlow;
    @ViewInject(R.id.bannerFi)
    private CircleFlowIndicator indic;
    private List<Banner> data;
    private BannerAdapter bannerAdapter;

    @ViewInject(R.id.entre_rv)
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Event({ R.id.main_sns_iv })
    private void viewClick(View view){
        switch (view.getId()){
            case R.id.main_sns_iv:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    protected void initView(){
        mainPresenter = new MainPresenterImpl(this,this);

        data = new ArrayList<>();
        bannerAdapter = new BannerAdapter(this, data);
        viewFlow.setFlowIndicator(indic);
        viewFlow.setAdapter(bannerAdapter);
        viewFlow.setTimeSpan(4500);
        viewFlow.setSelection(3 * 1000); // 设置初始位置
        viewFlow.startAutoFlowTimer(); // 启动自动播放

        layoutManager = new GridLayoutManager(this,2);
        adapter = new EntryAdapter(this);
        ((EntryAdapter)adapter).setOnItemClickListener(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new EntryAdapter.SpacesItemDecoration(42));
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void initData() {
        super.initData();
        mainPresenter.obtainBannerInfo();
        SharedPreferences loginFirstPreferences = LoginUserUtils.getAppSharedPreferences(
                MainActivity.this, Constant.PREFERENCES_LOGIN_FIRST);
        SharedPreferences.Editor editor = loginFirstPreferences.edit();
        editor.putBoolean("isFirst", false);
        editor.commit();
    }

    public void showBanner(List<Banner> urls){
        if (urls != null) {
            viewFlow.setmSideBuffer(urls.size());
            bannerAdapter.setData(urls);
            viewFlow.setAdapter(bannerAdapter);
            viewFlow.startAutoFlowTimer();
        }
    }


    @Override
    public void onItemClick(int position) {
        mainPresenter.onItemClick(position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mainPresenter.onActivityResult(requestCode, resultCode, data);
    }
}
