package com.zebra.contract;

import android.content.Intent;

import com.zebra.bean.PatternsMark;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/30.
 */

public class MarkResContract {
    
public interface View{
    void showMarkResource(List<PatternsMark> list);
    void showTitle(CharSequence charSequence);
}

public interface Presenter{
    void obtainMaterialResource(Intent intent);
    void onItemClick(int position);
    void onActivityResult(int requestCode, int resultCode, Intent data);
}

public interface Model{
}


}