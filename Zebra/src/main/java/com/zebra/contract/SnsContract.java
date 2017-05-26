package com.zebra.contract;

import com.zebra.bean.Personal;

/**
 * Created by zhiPeng.S on 2016/12/13.
 */

public class SnsContract {

public interface View{
    void showPersonInfo(Personal personal);
}

public interface Presenter{
    void obtainSnsInfo();
}

public interface Model{
}


}