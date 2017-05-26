package com.zebra.presenter;
import android.app.Activity;

import com.zebra.contract.MarkContract;

/**
* Created by zhiPeng.S on 2016/11/28
*/

public class MarkPresenterImpl implements MarkContract.Presenter{
    private Activity activity;
    private MarkContract.View markView;

    public MarkPresenterImpl(Activity activity, MarkContract.View markView) {
        this.activity = activity;
        this.markView = markView;
    }
}