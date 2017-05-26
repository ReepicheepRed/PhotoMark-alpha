package com.zebra.contract;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.widget.RelativeLayout;

import com.zebra.adapter.MarkPosterEditAdapter;
import com.zebra.bean.PatternsMark;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/12/22.
 */

public class MarkPosterEditContract {
    
public interface View{
    void setCurTextColor(int color);
    void setCurTextFont(Typeface font);
    void setPosterViewSize(int width,int height);
    void setSquarePosterView(boolean flag);
    void showPosterView(com.martin.poster.Model model);
    void addView(android.view.View view, RelativeLayout.LayoutParams layoutParams);
    MarkPosterEditAdapter getAdapter();
    void showPosterPatterns(List<PatternsMark> data_style);
}

public interface Presenter{
    void releasePresenter();
    void generatePoster(Intent intent);
    void savePicture(Bitmap bitmap);
    void onItemClick(int position, android.view.View view);
    void filtrateTemplate(Intent intent);
    void clearPosterData();
    void addOnSoftKeyBoardVisibleListener(Activity activity, final IKeyBoardVisibleListener listener);
}

public interface Model{
}

public interface IKeyBoardVisibleListener{
    void onSoftKeyBoardVisible(boolean visible , int windowBottom);
}


}