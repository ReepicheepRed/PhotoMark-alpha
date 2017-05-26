package com.zebra.util;

import com.zebra.R;
import com.zebra.model.LabelModelImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/12/1.
 */

public class DataResUtil {

    public static List<LabelModelImpl> labelList = new ArrayList<>();

    static {
        labelList.add(new LabelModelImpl(R.drawable.slabel,"123456798"));
        labelList.add(new LabelModelImpl(R.drawable.slabel,"asddfad"));
        labelList.add(new LabelModelImpl(R.drawable.slabel,"gsrgsehtr"));
        labelList.add(new LabelModelImpl(R.drawable.slabel,"sgdfgahr"));
        labelList.add(new LabelModelImpl(R.drawable.slabel,"reesgreg"));
        labelList.add(new LabelModelImpl(R.drawable.slabel,"gsergeh"));
    }
}
