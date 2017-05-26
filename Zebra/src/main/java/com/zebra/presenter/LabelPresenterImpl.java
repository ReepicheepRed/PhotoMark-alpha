package com.zebra.presenter;
import android.app.Activity;

import com.zebra.contract.LabelContract;

/**
* Created by zhiPeng.S on 2016/12/01
*/

public class LabelPresenterImpl implements LabelContract.Presenter{
    private Activity activity;
    private LabelContract.View labelView;

    public LabelPresenterImpl(Activity activity, LabelContract.View labelView) {
        this.activity = activity;
        this.labelView = labelView;
    }
}