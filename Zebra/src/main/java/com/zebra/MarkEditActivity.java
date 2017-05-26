package com.zebra;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zebra.base.BaseActivity;
import com.zebra.bean.Template2;
import com.zebra.contract.MarkEditContract;
import com.zebra.presenter.MarkEditPresenterImpl;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhiPeng.S on 2016/11/28.
 */
@ContentView(R.layout.activity_mark_edit)
public class MarkEditActivity extends BaseActivity implements MarkEditContract.View,TextWatcher{
    private MarkEditContract.Presenter markEditPresenter;

    @ViewInject(R.id.actionbar_title)
    private TextView title;
    @ViewInject(R.id.actionbar_right)
    private TextView generate;

    @ViewInject(R.id.mEdit_iv)
    private TextView materialPicture;
    @ViewInject(R.id.mEdit_content)
    private EditText content;

    @Event({
            R.id.actionbar_back,
            R.id.actionbar_right,
            R.id.mEdit_title
    })
    private void viewClick(View view){
        switch (view.getId()){
            case R.id.actionbar_back:
                finish();
                break;
            case R.id.actionbar_right:
                markEditPresenter.savePicture(materialPicture);
                break;
            case R.id.mEdit_title:
                break;
        }
    }

    @Override
    protected void initView() {
        markEditPresenter = new MarkEditPresenterImpl(this,this);

        title.setText(R.string.preview);
        generate.setText(R.string.generate);
        content.addTextChangedListener(this);
        content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Template2 template = (Template2) bundle.getSerializable("MarkRes");
        markEditPresenter.obtainMaterial(template);
    }

    @Override
    public void setMaterialPicture(Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(getResources(),bitmap);
        materialPicture.setBackgroundDrawable(drawable);
    }

    @Override
    public void setMaterialText(CharSequence charSequence) {
        content.setText(charSequence);
    }

    @Override
    public void clearInputField() {
        content.setText("");
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        markEditPresenter.generateWaterMark(materialPicture,s.toString());
        content.setSelection(content.length());
    }
}
