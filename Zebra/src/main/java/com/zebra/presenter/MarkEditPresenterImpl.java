package com.zebra.presenter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.common.util.FileUtils;
import com.common.util.ImageUtils;
import com.common.util.StringUtils;
import com.common.util.TimeUtils;
import com.sticker.base.ActivityHelper;
import com.zebra.R;
import com.zebra.bean.PatternsLocal;
import com.zebra.bean.PatternsLocal.ItemsBean.EditTextBean;
import com.zebra.bean.Template2;
import com.zebra.contract.MarkEditContract;
import com.zebra.db.DB_Config;
import com.zebra.util.BitmapUtil;
import com.zebra.util.FileUtil;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
* Created by zhiPeng.S on 2016/11/28
*/

public class MarkEditPresenterImpl implements MarkEditContract.Presenter{
    private Activity activity;
    private MarkEditContract.View markEditView;
    private Bitmap materialBitmap;
    private Bitmap materialBitmap_Self;
    private ActivityHelper mActivityHelper;
    private Template2 template;
    private DbManager db;
    public MarkEditPresenterImpl(Activity activity, MarkEditContract.View markEditView) {
        this.activity = activity;
        this.markEditView = markEditView;
        mActivityHelper = new ActivityHelper(activity);
        db = x.getDb(DB_Config.getDaoConfig());
    }

    @Override
    public void obtainMaterial(Template2 template) {
        this.template = template;
        Uri uri = FileUtil.getImageContentUri(activity,new File(template.getOriPath()));
        ImageUtils.asyncLoadImage(activity, uri,
                result -> {
                    materialBitmap = result;
                    markEditView.setMaterialPicture(result);
                });

        obtainPattern(template);
    }
    private List<EditTextBean> editBeans = null;
    int fdi,num;
    private void obtainPattern(Template2 template){
        List<List<String>> layout = null;

        fdi = template.getFdi();
        num = template.getNum();
        String patternsStr = FileUtil.getFileContent(activity, "patterns_" + fdi + ".json");
        List<PatternsLocal> plList = JSON.parseArray(patternsStr, PatternsLocal.class);
        List<PatternsLocal.ItemsBean> itemBeans = plList.get(0).getItems();
        for (int i = 0; i < itemBeans.size(); i++) {
            PatternsLocal.ItemsBean itemBean = itemBeans.get(i);
            if (Integer.valueOf(itemBean.getNum()) == num){
                layout = itemBean.getLayout();
                editBeans = itemBean.getEditText();
            }
        }

        markEditView.setMaterialText(template.getContent());
    }

    @Override
    public void generateWaterMark(TextView selfMaterial,String content) {

//        int size = layout == null ? 0 : layout.size();
//        for (int i = 0; i < size; i++) {
//            List<String> data = layout.get(i);
//            int[] pos_LT = trimLayout(data.get(0));
//            int[] pos_RT = trimLayout(data.get(1));
//            int[] pos_RB = trimLayout(data.get(2));
//            int[] pos_LB = trimLayout(data.get(3));
//            materialBitmap_Self = BitmapUtil.addMarkText(activity,materialBitmap,content,pos_LT[0],pos_LT[1]);
//        }
//        markEditView.setMaterialPicture(materialBitmap_Self);
        int size = editBeans == null ? 0 : editBeans.size();
        for (int i = 0; i < size; i++) {
            EditTextBean editBean = editBeans.get(i);
            int width = BitmapUtil.dip2px(editBean.getWidth()/2.0f),
                    height = BitmapUtil.dip2px(editBean.getHeight()/2.0f);
            int gravity = editBean.getGravity();
            int textSize = editBean.getTextSize()/2;
            int color;
            try{
                color = Color.parseColor(editBean.getTextColor());
            }catch (Exception e){
                color = 0xff000000;
            }
            int top = BitmapUtil.dip2px(editBean.getTopMargin()/2.2f),
                    bottom = BitmapUtil.dip2px(editBean.getBottomMargin()/2.0f),
                    left = BitmapUtil.dip2px(editBean.getLeftMargin()/2.0f),
                    right = BitmapUtil.dip2px(editBean.getRightMargin()/2.0f);
//specified process
            int[] numA = {9,10,11,12,13,14};
            for (int number:numA) {
                if(num == number)
                    top = BitmapUtil.dip2px(editBean.getTopMargin()/2.8f);
                if(num ==14){
                    bottom = BitmapUtil.dip2px(editBean.getBottomMargin()/2.2f);
                    selfMaterial.setLineSpacing(1,0.9f);
                }
            }
            switch (num) {
                case 36:
                case 37:
                case 38:
                case 40:
                case 41:
                case 42:
                case 43:
//                    selfMaterial.setSingleLine();
                    break;
            }
//set textView
            if(fdi == 1004){
                selfMaterial.setWidth(width);
                selfMaterial.setHeight(height);
            }
            if(fdi == 1003){
                selfMaterial.setWidth(BitmapUtil.dip2px(50));
                selfMaterial.setMaxLines(1);
                selfMaterial.setEllipsize(TextUtils.TruncateAt.END);
            }
            selfMaterial.setTextSize(textSize);
            selfMaterial.setTextColor(color);
            selfMaterial.setText(content);
            template.setContent(content);
            selfMaterial.setPadding(left,top,right,bottom);

            switch (gravity){
                case 0:
                    selfMaterial.setGravity(Gravity.CENTER);
                    selfMaterial.setPadding(left,0,right,0);
                    break;
                case 1:
                    selfMaterial.setGravity(Gravity.END);
                    break;
                case -1:
                    selfMaterial.setGravity(Gravity.START);
                    break;
                case -2:
                    selfMaterial.setGravity(Gravity.CENTER_HORIZONTAL);
                    break;
            }

        }

    }

    @Override
    public void savePicture() {
        if(materialBitmap_Self != null)
        new SavePicMaterial().execute(materialBitmap_Self);
    }

    @Override
    public void savePicture(TextView view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        materialBitmap_Self = bitmap;
        new SavePicMaterial().execute(materialBitmap_Self);
    }

    private class SavePicMaterial extends AsyncTask<Bitmap,Void,String> {
        Bitmap bitmap;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mActivityHelper.showProgressDialog(activity.getString(R.string.processing));
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            String fileName = null;
            try {
                bitmap = params[0];

                String picName = "/Label-"+ template.getFdi() + "-" + template.getCategory() + "-" +
                        template.getNum() + "-" + TimeUtils.dtFormat(new Date(),"yyyyMMddHHmmss");

                fileName = ImageUtils.saveToFile(FileUtils.getInst().getLabelMaterialPath(FileUtils.MarkSelf) +
                         picName , false, bitmap);

            } catch (Exception e) {
                e.printStackTrace();
                mActivityHelper.toast(activity.getString(R.string.process_error), Toast.LENGTH_LONG);
            }
            return fileName;
        }

        @Override
        protected void onPostExecute(String fileName) {
            super.onPostExecute(fileName);
            mActivityHelper.dismissProgressDialog();
            if (StringUtils.isEmpty(fileName)) {
                return;
            }

            Template2 self = null;
            try {
                self = db.selector(Template2.class).where("id", "=", template.getId()).findFirst();
                if (self == null) {
                    template.setImgPath(fileName);
                    db.save(template);
                } else {
                    db.update(Template2.class, WhereBuilder.b("id", "=", template.getId()),
                            new KeyValue("imgPath", fileName),new KeyValue("content",template.getContent()));
                }
            } catch (DbException e) {
                e.printStackTrace();
            }

            Template2 newMark = null;
            try {
                newMark = db.selector(Template2.class).where("id", "=", template.getId()).findFirst();
            } catch (DbException e) {
                e.printStackTrace();
            }
            if(newMark == null) newMark = template;
            Intent intent = new Intent();
            intent.putExtra("newMark",newMark);
            activity.setResult(Activity.RESULT_OK,intent);
            activity.finish();

        }
    }
}