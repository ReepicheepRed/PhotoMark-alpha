package com.zebra;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zebra.adapter.SnsAdapter;
import com.zebra.base.BaseActivity;
import com.zebra.bean.Personal;
import com.zebra.contract.SnsContract;
import com.zebra.presenter.SnsPresenterImpl;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/12/12.
 */
@ContentView(R.layout.activity_sns)
public class SnsActivity extends BaseActivity implements SnsContract.View,SnsAdapter.OnItemClickListener {
    private SnsContract.Presenter snsPresenter;

    @ViewInject(R.id.actionbar_back_iv_sns)
    private ImageView back;
    @ViewInject(R.id.actionbar_title_sns)
    private TextView title;
    @ViewInject(R.id.actionbar_ll_sns)
    private LinearLayout actionbar;

    @ViewInject(R.id.sns_avatar)
    private ImageView avatar;

    @ViewInject(R.id.sns_rv)
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Personal> data;


    @Event({
            R.id.actionbar_back_iv_sns,
            R.id.sns_exit_btn,
            R.id.sns_avatar
    })
    private void viewClick(View view){
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.sns_exit_btn:
            case R.id.actionbar_back_iv_sns:
                finish();
                break;
            case R.id.sns_avatar:
                intent.setClass(this,SnsImproveActivity.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    protected void initView() {
        snsPresenter = new SnsPresenterImpl(this,this);
        title.setText(R.string.sns);
        title.setTextColor(Color.WHITE);
        back.setImageResource(R.drawable.nav_icon_fanhui);
        actionbar.setBackgroundColor(getResources().getColor(R.color.color_black_percent_8));

        data = new ArrayList<>();
        adapter = new SnsAdapter(this,data);
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        ((SnsAdapter)adapter).setOnItemClickListener(this);
        snsPresenter.obtainSnsInfo();
    }

    @Override
    public void onItemClick(int position) {
        String[] titleType = getResources().getStringArray(R.array.sns_info);
        Intent intent = new Intent();
        if (position == 4)
            intent.setClass(this,SnsCategoryActivity.class);
        else{
            intent.setClass(this,SnsImproveChildActivity.class);
            intent.putExtra("infoType", titleType[position]);
        }
        startActivity(intent);
    }

    @Override
    public void showPersonInfo(Personal personal) {

    }
}
