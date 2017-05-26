package com.zebra.presenter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.coolstar.makeposter.view.posterview.IPosterView;
import com.martin.poster.Layer;
import com.martin.poster.Model;
import com.zebra.R;
import com.zebra.adapter.MarkPosterEditAdapter;
import com.zebra.base.BaseActivity;
import com.zebra.bean.PatternsLocal;
import com.zebra.bean.PatternsMark;
import com.zebra.bean.Template2;
import com.zebra.contract.MarkPosterEditContract;
import com.zebra.contract.MarkPosterEditContract.IKeyBoardVisibleListener;
import com.zebra.util.BitmapUtil;
import com.zebra.util.FileUtil;
import com.zebra.util.SavePicture;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import com.zebra.bean.PatternsLocal.ItemsBean.EditTextBean;

import org.xutils.x;

import static com.zebra.util.Utility.trimLayout;

/**
* Created by zhiPeng.S on 2016/12/22
*/

public class MarkPosterEditPresenterImpl implements MarkPosterEditContract.Presenter{
    private BaseActivity activity;
    private MarkPosterEditContract.View markPosterEditView;
    private IPosterView posterView;
    private List<Layer> layers = new ArrayList<>();
    private RequestManager requestManager;
    private Bitmap cover, layer1, layer2, layer3;

    private String floor;
    private List<String> samples;
    private List<Bitmap> bitmaps;
    private int category,num;
    private List<List<String>> layout ;
    private List<EditTextBean> posterText;

    @Override
    public void clearPosterData(){
        layers.clear();
        cover = null;
        samples.clear();
        bitmaps.clear();
        layout.clear();
        posterText.clear();
    }
    static final int MaxCoverWidth = 456;
    static final int MaxLayerWidth = 400;

    public MarkPosterEditPresenterImpl(BaseActivity activity, MarkPosterEditContract.View markPosterEditView) {
        this.activity = activity;
        this.markPosterEditView = markPosterEditView;
        requestManager = Glide.with(activity);
        samples = new ArrayList<>();
        bitmaps = new ArrayList<>();
    }

    public MarkPosterEditPresenterImpl(BaseActivity activity, MarkPosterEditContract.View markPosterEditView,IPosterView posterView) {
        this(activity,markPosterEditView);
        this.posterView = posterView;
    }

    public void releasePresenter(){
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEventBuildPoster(Bitmap bmp){
        Log.d("EventBusTest","Poster.presenter.event");
        posterView.onBuildSuccess(bmp);
    }

    @Override
    public void savePicture(Bitmap bitmap) {
        new SavePicture(activity).execute(bitmap);
        activity.finish();
    }

    @Override
    public void onItemClick(int position, View view) {
        final int font = 0, color = 1;
        MarkPosterEditAdapter adapter = markPosterEditView.getAdapter();
        int style = !adapter.isColor() ? font : color;
        switch (style){
            case font:
                List<Typeface> typeFaces = adapter.getTypeFaces();
                Typeface typeFace = typeFaces.get(position);
                markPosterEditView.setCurTextFont(typeFace);
                break;
            case color:
                TypedArray colors = adapter.getColors();
                markPosterEditView.setCurTextColor(colors.getColor(position, Color.WHITE));
                break;
        }
    }

    @Override
    public void generatePoster(Intent intent) {
        try {
            Template2 patterns = (Template2) intent.getExtras().getSerializable("poster");
            if (patterns == null) return;
            switch(patterns.getCategory()){
                case 1006:
                    category = 1;
                    break;
                case 1007:
                    category = 2;
                    break;
                case 1008:
                    category = 3;
                    break;
                case 1025:
                    category = 4;
                    break;
                case 1026:
                    category = 5;
                    break;
                case 1027:
                    category = 6;
                    break;
                case 1028:
                    category = 7;
                    break;
                case 1029:
                    category = 8;
                    break;
                case 1030:
                    category = 9;
                    break;
            }
            num = patterns.getNum();
            floor = patterns.getOriPath();
            samples = patterns.getSamPath();
            if(samples.size() != category ) {
                Toast.makeText(activity,"The num of sample picture isn't match template",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String patternsStr = FileUtil.getFileContent(activity, "patterns_" + category + ".json");
            List<PatternsLocal> plList = JSON.parseArray(patternsStr, PatternsLocal.class);
            List<PatternsLocal.ItemsBean> itemBeans = plList.get(0).getItems();
            PatternsLocal.ItemsBean itemCurrent = null;
            for (int i = 0; i < itemBeans.size(); i++) {
                PatternsLocal.ItemsBean itemBean = itemBeans.get(i);
                if (Integer.valueOf(itemBean.getNum()) == num){
                    itemCurrent = itemBean;
                }
            }
            if (itemCurrent == null) return;
            layout = itemCurrent.getLayout();
            posterText = itemCurrent.getEditText();
            String posterSize = itemCurrent.getSize();
            int size[] = trimLayout(posterSize);
            if(size[0] == size[1])
                markPosterEditView.setPosterViewSize(BitmapUtil.dip2px(640),BitmapUtil.dip2px(640));

            new ShowPosterTask().execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class ShowPosterTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            loadingPosterPicture();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showPoster();
        }
    }

    private void showPoster(){
        Model model = new  Model(cover, layers);
        markPosterEditView.showPosterView(model);
        setPosterText();
    }

    // for poster picture
    private void loadingPosterPicture(){
        try {
            cover = requestManager.load(floor).asBitmap().into(MaxCoverWidth, MaxCoverWidth).get();
            int size = samples == null ? 0 : samples.size();
            for (int i = 0; i < size; i++) {
                Bitmap bitmap = requestManager.load(samples.get(i)).asBitmap().into(MaxLayerWidth, MaxLayerWidth).get();
                int bmpSize = BitmapUtil.getBitmapSize(bitmap);
                while (bmpSize > 800000){
                    bitmap = BitmapUtil.small(bitmap);
                    bmpSize = BitmapUtil.getBitmapSize(bitmap);
                }
                bitmaps.add(bitmap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        float scale = x.app().getResources().getDisplayMetrics().density;
        int range = scale >= 4 ? 4 : scale >= 3 ? 3 : scale >= 2 ? 2 : (int) scale;
        int size = bitmaps == null ? 0 : bitmaps.size();
        for (int i = 0; i < size; i++) {
            List<String> data = layout.get(i);
            int[] pos_LT = trimLayout(data.get(0));
            int[] pos_RT = trimLayout(data.get(1));
            int[] pos_RB = trimLayout(data.get(2));
            int[] pos_LB = trimLayout(data.get(3));

            switch (range) {
                case 2:
                    layers.add(new Layer(bitmaps.get(i), 0)
                            .markPoint(BitmapUtil.dip2px(pos_LT[0] / 1.2f), BitmapUtil.dip2px(pos_LT[1] / 1.2f))
                        .markPoint(BitmapUtil.dip2px(pos_RT[0] / 1.2f), BitmapUtil.dip2px(pos_RT[1] / 1.2f))
                        .markPoint(BitmapUtil.dip2px(pos_RB[0] / 1.2f), BitmapUtil.dip2px(pos_RB[1] / 1.2f))
                        .markPoint(BitmapUtil.dip2px(pos_LB[0] / 1.2f), BitmapUtil.dip2px(pos_LB[1] / 1.2f))
                        .build());
                    break;
                case 3:
                    layers.add(new Layer(bitmaps.get(i), 0)
                            .markPoint(BitmapUtil.dip2px(pos_LT[0] / 1.9f), BitmapUtil.dip2px(pos_LT[1] / 1.9f))
                            .markPoint(BitmapUtil.dip2px(pos_RT[0] / 1.9f), BitmapUtil.dip2px(pos_RT[1] / 1.9f))
                            .markPoint(BitmapUtil.dip2px(pos_RB[0] / 1.9f), BitmapUtil.dip2px(pos_RB[1] / 1.9f))
                            .markPoint(BitmapUtil.dip2px(pos_LB[0] / 1.9f), BitmapUtil.dip2px(pos_LB[1] / 1.9f))
                            .build());
                    break;
                case 4:
                    layers.add(new Layer(bitmaps.get(i), 0)
                            .markPoint(BitmapUtil.dip2px(pos_LT[0] / 2.5f), BitmapUtil.dip2px(pos_LT[1] / 2.5f))
                            .markPoint(BitmapUtil.dip2px(pos_RT[0] / 2.5f), BitmapUtil.dip2px(pos_RT[1] / 2.5f))
                            .markPoint(BitmapUtil.dip2px(pos_RB[0] / 2.5f), BitmapUtil.dip2px(pos_RB[1] / 2.5f))
                            .markPoint(BitmapUtil.dip2px(pos_LB[0] / 2.5f), BitmapUtil.dip2px(pos_LB[1] / 2.5f))
                            .build());
                    break;
                default:
                    layers.add(new Layer(bitmaps.get(i), 0)
                            .markPoint(BitmapUtil.dip2px(pos_LT[0] / 1.0f), BitmapUtil.dip2px(pos_LT[1] / 1.0f))
                            .markPoint(BitmapUtil.dip2px(pos_RT[0] / 1.0f), BitmapUtil.dip2px(pos_RT[1] / 1.0f))
                            .markPoint(BitmapUtil.dip2px(pos_RB[0] / 1.0f), BitmapUtil.dip2px(pos_RB[1] / 1.0f))
                            .markPoint(BitmapUtil.dip2px(pos_LB[0] / 1.0f), BitmapUtil.dip2px(pos_LB[1] / 1.0f))
                            .build());
            }
        }
    }

    //for poster text
    private void setPosterText(){
        int size = posterText == null ? 0 : posterText.size();
        for (int i = 0; i < size; i++) {
            EditTextBean editBean = posterText.get(i);
            int width = editBean.getWidth(), height = editBean.getHeight();
            int LGravity = editBean.getLayoutGravity(), gravity = editBean.getGravity();
            int topMargin = editBean.getTopMargin();
            int leftMargin = editBean.getLeftMargin(), rightMargin = editBean.getRightMargin();
            int textSize = editBean.getTextSize();
            int color = Color.parseColor(editBean.getTextColor());

            TextView textView = new TextView(activity);
//            textView.setBackgroundColor(R.color.transparent);
            textView.setTextSize(textSize/2);
            textView.setTextColor(color);
            textView.setText(R.string.edit_tip);
            textView.setMaxLines(1);
            textView.setEllipsize(TextUtils.TruncateAt.END);
//            if(editBean.isSingleLine()){
//                textView.setSingleLine();
//            }

            switch (gravity){
                case 0:
                    textView.setGravity(Gravity.CENTER);
                    break;
                case 1:
                    textView.setGravity(Gravity.RIGHT);
                    break;
                case -1:
                    textView.setGravity(Gravity.LEFT);
                    break;
            }

            if (width != -1 && width != -2) width = BitmapUtil.dip2px(width/2.0f);
            if (height != -1 && height != -2) height = BitmapUtil.dip2px(height/2.0f);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            switch (LGravity){
                case 0:
                    layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, -1);
                    break;
                case 1:
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, -1);
                    break;
                case -1:
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, -1);
                    break;
            }

            int type = Integer.valueOf(String.valueOf(category).concat(String.valueOf(num)));
            switch (type){
                case 11:
                    layoutParams.topMargin = BitmapUtil.dip2px(topMargin/2.2f);
                    break;
                case 22:
                    layoutParams.topMargin = BitmapUtil.dip2px(topMargin/1.8f);
                    break;
                case 33:
                    layoutParams.topMargin = BitmapUtil.dip2px(topMargin/1.6f);
                    break;
                case 35:
                    layoutParams.topMargin = BitmapUtil.dip2px(topMargin/1.6f);
                    break;
                case 71:
                    layoutParams.topMargin = BitmapUtil.dip2px(topMargin/1.6f);
                    break;
                case 81:
                    layoutParams.topMargin = BitmapUtil.dip2px(topMargin/1.5f);
                    break;
                default:
                    layoutParams.topMargin = BitmapUtil.dip2px(topMargin/2.0f);

            }


            layoutParams.leftMargin = BitmapUtil.dip2px(leftMargin/2.0f);
            layoutParams.rightMargin = BitmapUtil.dip2px(rightMargin/2.0f);

            markPosterEditView.addView(textView,layoutParams);
        }
    }

    @Override
    public void filtrateTemplate(Intent intent){
        ArrayList<PatternsMark> patternsMarks = (ArrayList<PatternsMark>) intent.getExtras().getSerializable("patterns");
        markPosterEditView.showPosterPatterns(patternsMarks);
    }


    private boolean isVisiableForLast = false;

    @Override
    public void addOnSoftKeyBoardVisibleListener(Activity activity, final IKeyBoardVisibleListener listener) {
        final View decorView = activity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect rect = new Rect();
            decorView.getWindowVisibleDisplayFrame(rect);
            //计算出可见屏幕的高度
            int displayHight = rect.bottom - rect.top;
            //获得屏幕整体的高度
            int hight = decorView.getHeight();
            //获得键盘高度
            int keyboardHeight = hight-displayHight;
            boolean visible = (double) displayHight / hight < 0.8;
            if(visible != isVisiableForLast){
                listener.onSoftKeyBoardVisible(visible,keyboardHeight );
            }
            isVisiableForLast = visible;
        });
    }
}