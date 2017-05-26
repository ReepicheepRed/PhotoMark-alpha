package com.zebra.presenter;
import android.app.Activity;

import com.zebra.contract.SnsImproveContract;

/**
* Created by zhiPeng.S on 2016/12/13
*/

public class SnsImprovePresenterImpl implements SnsImproveContract.Presenter{
    private Activity activity;
    private SnsImproveContract.View snsImproveView;

    public SnsImprovePresenterImpl(Activity activity, SnsImproveContract.View snsImproveView) {
        this.activity = activity;
        this.snsImproveView = snsImproveView;
    }

}