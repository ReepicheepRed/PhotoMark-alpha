package com.zebra;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zebra.base.BaseActivity;
import com.zebra.contract.SnsImproveChildContract;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhiPeng.S on 2016/12/14.
 */
@ContentView(R.layout.activity_improvement_child)
public class SnsImproveChildActivity extends BaseActivity implements SnsImproveChildContract.View,TextWatcher{
    private SnsImproveChildContract.Presenter snsImproveChildPresenter;

    @ViewInject(R.id.actionbar_title_sns)
    private TextView title;
    @ViewInject(R.id.actionbar_right_sns)
    private TextView complete;

    @ViewInject(R.id.improve_child_et)
    private EditText content;

    @Event({
            R.id.actionbar_back_iv_sns,
            R.id.actionbar_right_sns
    })
    private void viewClick(View view){
        switch (view.getId()){
            case R.id.actionbar_back_iv_sns:
                finish();
                break;
            case R.id.actionbar_right_sns:
                break;
        }
    }

    @Override
    protected void initView() {
        String infoType = getIntent().getStringExtra("infoType");
        String[] titleType = getResources().getStringArray(R.array.sns_info);
        String[] tip = getResources().getStringArray(R.array.improve_tip);
        title.setText(infoType);
        content.setHint(String.format(tip[1],infoType));
        if(infoType.equals(titleType[0]))   content.setHint(tip[0]);
        complete.setText(R.string.complete);
        complete.setTextColor(getResources().getColor(R.color.color_bf));

        content.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.length() > 0){
            complete.setTextColor(getResources().getColor(R.color.bar_red_c));
            complete.setClickable(true);
            return;
        }

        complete.setTextColor(getResources().getColor(R.color.color_bf));
        complete.setClickable(false);
    }
}
