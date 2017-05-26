package com.xiaopo.flying.photolayout.contract;

import com.xiaopo.flying.photolayout.bean.PuzzleStyle;

import java.util.List;

/**
 * Created by zhiPeng.S on 2017/2/28.
 */

public class ProcessContract {
    
public interface View{
    void showStyle(List<PuzzleStyle> data);
}

public interface Presenter{
    void obtainStyleInfo(int num, String baseUrl);
}

public interface Model{
}


}