package com.zebra.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zebra.R;
import com.zebra.SnsActivity;
import com.zebra.base.BaseActivity;
import com.zebra.contract.LoginContract;
//import com.zebra.databinding.ActivityLoginBinding;
import com.zebra.presenter.LoginPresenterImpl;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhiPeng.S on 2016/11/24.
 */
@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity implements LoginContract.View{
    private LoginContract.Presenter presenter;

    @ViewInject(R.id.actionbar_back_iv_sns)
    private ImageView back;
    @ViewInject(R.id.actionbar_ll_sns)
    private LinearLayout actionbar;

    @ViewInject(R.id.login_verify_code_et)
    private EditText phone;

    @ViewInject(R.id.login_verify_code_et)
    private EditText verifyCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setLoginBinding();
    }


    @Event({
            R.id.actionbar_back_iv_sns,
            R.id.login_obtain_verify_code_tv,
            R.id.login_btn
    })
    private void viewClick(View view) {
        Intent intent = new Intent();
        switch(view.getId()){
            case R.id.actionbar_back_iv_sns:
                finish();
                break;
            case R.id.login_obtain_verify_code_tv:
                break;
            case R.id.login_btn:
                intent.setClass(this, SnsActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void initView() {
        presenter = new LoginPresenterImpl();
        back.setImageResource(R.drawable.nav_icon_fanhui);
        actionbar.setBackgroundColor(getResources().getColor(R.color.transparent));
    }

//dataBinding
//    private ActivityLoginBinding loginBinding;
//    private void setLoginBinding(){
//        loginBinding = DataBindingUtil.setContentView(this,R.layout.activity_login);
//        loginBinding.setLoginActivity(this);
//    }

    public void bindClick(View view){
        viewClick(view);
    }
}
