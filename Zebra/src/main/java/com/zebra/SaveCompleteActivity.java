package com.zebra;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.zebra.base.BaseActivity;
import com.zebra.share.ShareUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

/**
 * Created by zhiPeng.S on 2016/12/27.
 */
@ContentView(R.layout.activity_save_complete)
public class SaveCompleteActivity extends BaseActivity{

    @ViewInject(R.id.actionbar_title_sns)
    private TextView title;

    @Override
    protected void initView() {
        title.setText(R.string.save_share);
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        ArrayList<String> picPaths = intent.getStringArrayListExtra("picPaths");
        ShareUtil.setLocalPicPath(picPaths.get(0));
    }

    @Event({
            R.id.actionbar_back_iv_sns,
            R.id.complete_back_btn,
            R.id.complete_share_btn

    })
    private void viewClick(View view){
        switch (view.getId()){
            case R.id.actionbar_back_iv_sns:
                finish();
                break;
            case R.id.complete_back_btn:
                Intent intent = new Intent(this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.complete_share_btn:
                ShareUtil.showShare();
                break;
        }
    }
}
