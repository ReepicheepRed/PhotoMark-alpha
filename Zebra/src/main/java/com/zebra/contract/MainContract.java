package com.zebra.contract;

import android.content.Intent;

import com.zebra.bean.Banner;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/18.
 */

public class MainContract {
    
public interface View{
    void showBanner(List<Banner> urls);
}

public interface Presenter{
    void obtainBannerInfo();
    void onItemClick(int position);
    void onActivityResult(int requestCode, int resultCode, Intent data);
}

public interface Model{
}


}