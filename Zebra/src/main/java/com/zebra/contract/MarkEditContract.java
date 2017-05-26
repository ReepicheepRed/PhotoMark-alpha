package com.zebra.contract;

import android.graphics.Bitmap;
import android.widget.TextView;

import com.zebra.bean.Template2;

/**
 * Created by zhiPeng.S on 2016/11/28.
 */

public class MarkEditContract {
    
public interface View{
    void setMaterialPicture(Bitmap bitmap);
    void setMaterialText(CharSequence charSequence);
    void clearInputField();
}

public interface Presenter{
    void obtainMaterial(Template2 template);
    void generateWaterMark(TextView textView,String content);
    void savePicture();
    void savePicture(TextView view);
}

public interface Model{
}


}