package postermaster;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coolstar.makeposter.utils.ColoredSnackbar;
import com.coolstar.makeposter.view.posterview.IPosterView;
import com.coolstar.makeposter.view.posterview.StyleMenuHelper;
import com.coolstar.makeposter.widget.textposter.ColorMatrixFactory;
import com.coolstar.makeposter.widget.textposter.IPoster;
import com.coolstar.makeposter.widget.textposter.TextDrawer;
import com.martin.poster.Layer;
import com.martin.poster.Model;
import com.martin.poster.ModelView;
import com.martin.poster.OnLayerSelectListener;
import com.martin.poster.PosterView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.xiaopo.flying.poiphoto.Define;
import com.xiaopo.flying.poiphoto.PhotoPicker;
import com.zebra.R;
import com.zebra.adapter.MarkPosterEditAdapter;
import com.zebra.adapter.MarkPosterEditStyleAdapter;
import com.zebra.base.BaseActivity;
import com.zebra.bean.Font;
import com.zebra.bean.PatternsMark;
import com.zebra.bean.Template2;
import com.zebra.contract.MarkPosterEditContract;
import com.zebra.presenter.MarkPosterEditPresenterImpl;
import com.zebra.util.BitmapUtil;
import com.zebra.util.Utility;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_poster_edit)
public class MainActivity extends BaseActivity implements MarkPosterEditContract.View,OnLayerSelectListener,
        IPosterView,TextDrawer.OnGestureChangedListener, MarkPosterEditAdapter.OnItemClickListener, View.OnClickListener,
        View.OnTouchListener,MarkPosterEditContract.IKeyBoardVisibleListener,TextWatcher {
    private MarkPosterEditContract.Presenter markPosterEditPresenter;
    @ViewInject(R.id.posterView)
    private PosterView posterView;
    @ViewInject(R.id.pView_square)
    private PosterView posterView_squ;
    private Layer selectedLayer;
    @ViewInject(R.id.actionbar_title)
    private TextView title;
    @ViewInject(R.id.actionbar_right)
    private TextView save;
    private  Bitmap posterBitmap;

    @ViewInject(R.id.poster_rv)
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Font> data;

    @ViewInject(R.id.poster_et)
    private EditText editText;

    private TextView curEditText;
    private int mDeviceWidth = 0;
    private RecyclerView.ItemDecoration decoration1,decoration2;

    @ViewInject(R.id.poster_fonts_tv)
    private TextView font_tv;
    @ViewInject(R.id.poster_text_color_tv)
    private TextView color_tv;

    @Override
    protected void initView() {
        markPosterEditPresenter = new MarkPosterEditPresenterImpl(this,this,this);
        menuHelper = new StyleMenuHelper(this);
        initPosterStyle();
        title.setText(R.string.product_poster);
        save.setText(R.string.save);
        editText.addTextChangedListener(this);

        adapter = new MarkPosterEditAdapter(this);
        decoration1 = new MarkPosterEditAdapter.SpacesItemDecoration(0);
        decoration2  = new MarkPosterEditAdapter.SpacesItemDecoration(15);
        if(((MarkPosterEditAdapter)adapter).isColor()){
            layoutManager = new GridLayoutManager(this,5);
            recyclerView.addItemDecoration(decoration2);
            setSelectStyle(true);
        }else{
            layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
            recyclerView.addItemDecoration(decoration1);
            setSelectStyle(false);
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        ((MarkPosterEditAdapter) adapter).setOnItemClickListener(this);

        poster_edit_rl.setOnTouchListener(this);
    }

    private void setSelectStyle(boolean isColor){
        if(isColor){
            color_tv.setSelected(true);
            font_tv.setSelected(false);
            color_tv.setTextColor(getResources().getColor(R.color.light_blue));
            font_tv.setTextColor(getResources().getColor(R.color.gray));
            return;
        }
        color_tv.setSelected(false);
        font_tv.setSelected(true);
        color_tv.setTextColor(getResources().getColor(R.color.gray));
        font_tv.setTextColor(getResources().getColor(R.color.light_blue));
    }

    @Override
    public void onBackPressed() {
        if(isStylePanelShowing()){
            hideStylePanel();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void setPosterViewSize(int width, int height) {
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(width,height);
        layoutParams.topMargin = BitmapUtil.dip2px(240/2.0f);
        layoutParams.leftMargin = BitmapUtil.dip2px(70/2.0f);
        layoutParams.rightMargin = BitmapUtil.dip2px(70/2.0f);
        posterView.setLayoutParams(layoutParams);
    }

    private boolean isSquare;
    @Override
    public void setSquarePosterView(boolean flag) {
        isSquare = flag;
    }

    @Override
    protected void initData() {
        super.initData();
        mDeviceWidth = getResources().getDisplayMetrics().widthPixels;
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        markPosterEditPresenter.generatePoster(getIntent());
        markPosterEditPresenter.filtrateTemplate(getIntent());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
            panelHideHeight = mBottomPanel.getHeight();

    }

    @Override
    protected void onPause() {
        super.onPause();
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    protected void onDestroy() {
        markPosterEditPresenter.releasePresenter();
        super.onDestroy();
    }
    InputMethodManager imm;
    @Event({
            R.id.actionbar_back,
            R.id.actionbar_right,
            R.id.poster_fonts_tv,
            R.id.poster_text_color_tv,
            R.id.poster_et,
            R.id.poster_sure_btn,
            R.id.poster_cancel_btn
    })
    private void viewClick(View view){

        switch (view.getId()){
            case R.id.actionbar_back:
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                finish();
                break;
            case R.id.actionbar_right:
                Bitmap bitmap = posterView.getResult();
                markPosterEditPresenter.savePicture(bitmap);
                break;
            case R.id.poster_et:
                editText.setInputType(InputType.TYPE_NULL);
                if (isStylePanelShowing()) {
                    editText.setInputType(InputType.TYPE_CLASS_TEXT);
                    imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
                } else {
                    SelectModifyStyle(R.id.poster_fonts_tv);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                break;
            case R.id.poster_fonts_tv:
                SelectModifyStyle(view.getId());
                break;
            case R.id.poster_text_color_tv:
                SelectModifyStyle(view.getId());
                break;
            case R.id.poster_cancel_btn:
                editText.setText("");
                break;
            case R.id.poster_sure_btn:
                String inputStr = editText.getText().toString();
                if(curEditText == null) {
                    toast("please select edit area", Toast.LENGTH_SHORT);
                    return;
                }
                curEditText.setText(inputStr);
                SelectModifyStyle(selectId);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
        }
    }

    @Override
    public void showPosterView(Model model) {
        posterView.setModel(model);
        posterView.getModelView().setOnLayerSelectListener(this);
        posterBitmap= posterView.getResult();
        posterView_squ.setVisibility(View.GONE);
    }

    @Override
    public void addView(View view, RelativeLayout.LayoutParams layoutParams) {
        view.setOnClickListener(this);
        posterView.addView(view,layoutParams);
        if(curEditText == null && view instanceof TextView)
            curEditText = (TextView)view;
    }

    @Override
    public void onSelected(Layer layer) {
        selectedLayer = layer;
        showSelectedPhotoDialog();
    }

    @Override
    public void dismiss(Layer layer) {
        if (selectedLayer == layer) {
            selectedLayer = null;
            posterView.dissMenu();
        }
    }

    private void showSelectedPhotoDialog() {
        PhotoPicker.newInstance()
                .setMaxCount(1)
                .pick(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Define.DEFAULT_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> paths = data.getStringArrayListExtra(Define.PATHS);
            String path = paths.get(0);

            final Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    ModelView modelView = posterView.getModelView();
                    selectedLayer.resetLayer(bitmap,bitmap);
                    selectedLayer.caculateDrawLayer(modelView.getWidth() * 1.0f / 720);
                    modelView.invalidate();
                }


                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Snackbar.make(posterView,"Replace Failed!",Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Picasso.with(this)
                    .load("file:///" + path)
                    .resize(mDeviceWidth, mDeviceWidth)
                    .centerInside()
                    .config(Bitmap.Config.RGB_565)
                    .into(target);
        }
    }

    private int selectId;
    private boolean isUsedColor;
    private void SelectModifyStyle(int id){
        if(!isStylePanelShowing()){
            showStylePanel();
        }else if(id == selectId){
            hideStylePanel();
        }
        selectId = id;
        switch (id){
            case R.id.poster_fonts_tv:
                setSelectStyle(false);
                layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.removeItemDecoration(decoration2);
                recyclerView.addItemDecoration(decoration1);
                ((MarkPosterEditAdapter)adapter).setColor(false);
                adapter.notifyDataSetChanged();
                break;
            case R.id.poster_text_color_tv:
                setSelectStyle(true);
                layoutManager = new GridLayoutManager(this,5);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.removeItemDecoration(decoration1);
                recyclerView.addItemDecoration(decoration2);
//                if(!isUsedColor){
//                    recyclerView.addItemDecoration(new MarkPosterEditAdapter.SpacesItemDecoration(15));
//                    isUsedColor = true;
//                }
                ((MarkPosterEditAdapter)adapter).setColor(true);
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public MarkPosterEditAdapter getAdapter() {
        return (MarkPosterEditAdapter)adapter;
    }

    @Override
    public void onItemClick(int position, View view) {
        markPosterEditPresenter.onItemClick(position,view);
    }

    //    Mock MarkPoster
    @ViewInject(R.id.poster_text_drawer)
    private TextDrawer mDrawer;
    @ViewInject(R.id.poster_bottom_panel)
    private View mBottomPanel;
    private StyleMenuHelper menuHelper;
//    IPosterView start
    private float panelHideHeight,keyboardHeight;
    private boolean isPanelShowing = false;


    @Override
    public void showStylePanel() {
        if(mBottomPanel!=null&& !isPanelShowing){
            isPanelShowing = true;
            ObjectAnimator transAnim =  ObjectAnimator.ofFloat(mBottomPanel,"translationY",0,-panelHideHeight);
            transAnim.setDuration(500);
            transAnim.start();
        }
    }

    @Override
    public void hideStylePanel() {
        if(mBottomPanel!=null&&isPanelShowing){
            ObjectAnimator transAnim =  ObjectAnimator.ofFloat(mBottomPanel,"translationY",-panelHideHeight,0);
            transAnim.setDuration(500);
            transAnim.start();
            isPanelShowing = false;
        }
    }

    @Override
    public boolean isStylePanelShowing() {
        return isPanelShowing;
    }

    @Override
    public void setTextColor(int color) {
        if(mDrawer!=null){
            mDrawer.setTextColor(color);
        }
    }

    @Override
    public void setFont(Typeface font) {
        mDrawer.setFont(font);
    }

    @Override
    public void setTextShadow(boolean checked) {
        mDrawer.setShadow(checked);
    }

    @Override
    public void setTextAlign(Layout.Alignment alignment) {
        mDrawer.setTextAlign(alignment);
    }

    @Override
    public void showMessage(String msg) {
        Snackbar snackbar = Snackbar.make(mDrawer,msg,Snackbar.LENGTH_SHORT);
        ColoredSnackbar.info(snackbar).show();
    }

    private ArrayList<String> inputList = new ArrayList<String>(10);
    @Override
    public void addPosterText(String posterText) {
        inputList.add(posterText);
        String[] tmpArr = (String[]) inputList.toArray(new String[0]);
        mDrawer.setTexts(tmpArr);
        menuHelper.getStyleViewHolder().setStyleIndex(0);
    }

    @Override
    public void setTextSize(float size) {
        mDrawer.setTextSize(size);
    }

    @Override
    public void onBuildSuccess(Bitmap bmp) {
        if(bmp!=null){
            showMessage("海报保存到相册成功");
        }
    }

    @Override
    public IPoster getPoster() {
        return mDrawer.getPoster();
    }

    @Override
    public void setPoster(IPoster poster) {
        mDrawer.setPoster(poster);
    }

    @Override
    public void resetPosterTexts() {
        mDrawer.resetPosterTexts();
    }

    @Override
    public int getDrawerWidth() {
        return mDrawer.getWidth();
    }

    @Override
    public int getDrawerHeight() {
        return mDrawer.getHeight();
    }

    @Override
    public void showTitleAndLogo(String title, Bitmap bmp) {
        mDrawer.setMusicTitleAndLogo(title,bmp);
    }

    @Override
    public void setArtistBackground(Bitmap bmp) {
        mDrawer.setArtistBackground(bmp);
    }
//  IPosterView end

//  TextDrawer Gesture
    @Override
    public void onHorizontalGesture(float distanceX, float totalWidth) {
        mDrawer.changeArtistBmpColorArr(ColorMatrixFactory.selectColorMatrixArr(distanceX>0f?ColorMatrixFactory.DIR_NEXT:ColorMatrixFactory.DIR_PREV));
        showMessage(""+ColorMatrixFactory.getSelectedMatrixTitle());
    }

    @Override
    public void onVerticalGesture(float distanceY, float totalHeight) {
        if(Math.abs(distanceY)>100){
            if(distanceY>0){
                if(isStylePanelShowing()){
                    hideStylePanel();
                }
            }else{
                if(!isStylePanelShowing()){
                    showStylePanel();
                }
            }
        }
    }

    @Override
    public void setCurTextColor(int color) {
        if(curEditText != null)
            curEditText.setTextColor(color);
    }

    @Override
    public void setCurTextFont(Typeface font) {
        if(curEditText != null)
            curEditText.setTypeface(font);
    }

    @Override
    public void onClick(View v) {
        if(v instanceof TextView)
            curEditText = (TextView) v;
        SelectModifyStyle(R.id.poster_fonts_tv);
//        toast("text is clicked", Toast.LENGTH_SHORT);
    }

    @ViewInject(R.id.poster_edit_rl)
    private RelativeLayout poster_edit_rl;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int height = mBottomPanel.getTop();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (y < height) {
                if(isStylePanelShowing()){
                    hideStylePanel();
                }
            }
        }
        return true;
    }

    @ViewInject(R.id.poster_template_rv)
    private RecyclerView recyclerView_style;
    private RecyclerView.Adapter adapter_style;
    private RecyclerView.LayoutManager layoutManager_style;
    private RecyclerView.ItemDecoration decoration_style;
    private List<PatternsMark> data_style;

    private void initPosterStyle(){
        data_style = new ArrayList<>();
        adapter_style = new MarkPosterEditStyleAdapter(this,data_style);
        layoutManager_style = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        decoration_style = new MarkPosterEditStyleAdapter.SpacesItemDecoration(15);
        recyclerView_style.setAdapter(adapter_style);
        recyclerView_style.setLayoutManager(layoutManager_style);
        recyclerView_style.addItemDecoration(decoration_style);
        recyclerView_style.setHasFixedSize(true);
        ((MarkPosterEditStyleAdapter)adapter_style).setOnItemClickListener(this::loadingPattern);
    }

    private Template2 template;
    private void loadingPattern(int position, View view){
        PatternsMark patterns = data_style.get(position);
        template = new Template2();
        template.setFdi(patterns.getFdi());
        template.setCategory(patterns.getCategory());
        template.setNum(patterns.getNum());
        List<PatternsMark.ImginfoBean> images = patterns.getImginfo();
        int size = images.size() + 1;
        String[] url = new String[size];
        for (int i = 0; i < size ; i++) {
            if(i == 0) {
                url[i] = patterns.getImgbig();
                continue;
            }
            url[i] = images.get(i-1).getImg();
        }
        new AsyncTaskSave(this).execute(url);
    }


    public class AsyncTaskSave extends AsyncTask<String, Integer, String[]> {
        private Context mContext;
        private ProgressDialog progressDialog;
        public AsyncTaskSave(Context context){
            mContext = context;
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading...");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String[] doInBackground(String... params) {
            for (int i = 0; i < params.length; i++) {
                try{
                    if(params[i] == null){
                        return null;
                    }
                    String filePath = Utility.convertToSdPath(mContext, params[i]);
                    if(filePath == null){
                        return null;
                    }
                    Bitmap bitmap = Utility.getBitmapFromSdCard(MainActivity.this,params[i]);
                    if(bitmap == null) {
                        URL url = new URL(params[i]);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(6000);
                        if (conn.getResponseCode() == 200) {
                            InputStream input = conn.getInputStream();
                            Bitmap bm = BitmapFactory.decodeStream(input);
                            Utility.saveImageToSd(mContext, params[i], bm);
                        }
                    }
                }catch(Exception e){
                    System.out.println("加载网络图片错误");
                    e.printStackTrace();
                }
            }
            return params;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            List<String> path = new ArrayList<>();
            for (int i = 0; i < result.length; i++) {
                String filePath = Utility.convertToSdPath(mContext, result[i]);
                if(filePath != null){
                    path.add(filePath);
                }
            }

            if(path.size() < result.length) return;

            template.setOriPath(path.get(0));
            path.remove(0);
            template.setSamPath(path);
            template.setContent(getString(R.string.edit_tip));


            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("poster",template);
            intent.putExtras(bundle);
            posterView.removeAllViews();
            markPosterEditPresenter.clearPosterData();
            posterView.viewInit(MainActivity.this);
            markPosterEditPresenter.generatePoster(intent);
        }
    }

    @Override
    public void showPosterPatterns(List<PatternsMark> data_style){
        this.data_style = data_style;
        ((MarkPosterEditStyleAdapter)adapter_style).setData(data_style);
        adapter_style.notifyDataSetChanged();
    }

    @Override
    public void onSoftKeyBoardVisible(boolean visible, int windowBottom) {
        if(visible)
            keyboardHeight = windowBottom;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(curEditText != null)
            curEditText.setText(s.toString());
    }
}
