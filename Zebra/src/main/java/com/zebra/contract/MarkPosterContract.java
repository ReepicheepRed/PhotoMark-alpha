package com.zebra.contract;

import com.zebra.bean.PatternsMark;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/12/15.
 */

public class MarkPosterContract {
    
public interface View{
    void showPoster(List<PatternsMark> data);

}

public interface Presenter{
    void obtainPosterResource();
    void onItemClick(int position, android.view.View view);
}

public interface Model{
}


}