package com.zebra;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.zebra.adapter.PosterPagerAdapter;
import com.zebra.base.BaseActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhiPeng.S on 2016/12/15.
 */
@ContentView(R.layout.activity_mark_poster)
public class MarkPosterActivity extends BaseActivity  {

    @ViewInject(R.id.actionbar_title)
    private TextView title;

    @ViewInject(R.id.mPoster_tabs)
    private TabLayout tabLayout;
    @ViewInject(R.id.mPoster_container)
    private ViewPager viewPager;
    private PosterPagerAdapter pagerAdapter;
    @Override
    protected void initView() {
        title.setText(R.string.select_poster);
        pagerAdapter = new PosterPagerAdapter(this,getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Event({
            R.id.actionbar_back
    })
    private void viewClick(View view){
        switch (view.getId()){
            case R.id.actionbar_back:
                finish();
                break;
        }
    }
}
