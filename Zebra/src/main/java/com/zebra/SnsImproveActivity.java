package com.zebra;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.zebra.adapter.SnsAdapter;
import com.zebra.base.BaseActivity;
import com.zebra.bean.Personal;
import com.zebra.contract.SnsImproveContract;
import com.zebra.presenter.SnsImprovePresenterImpl;
import com.zebra.util.FileUtil;
import com.zebra.util.Utility;
import com.zebra.view.PopupWindow_Avatar;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/12/12.
 */
@ContentView(R.layout.activity_improvement)
public class SnsImproveActivity extends BaseActivity implements SnsImproveContract.View,SnsAdapter.OnItemClickListener{
    private SnsImproveContract.Presenter snsImprovePresenter;

    @ViewInject(R.id.actionbar_title_sns)
    private TextView title;
    @ViewInject(R.id.improve_avatar_iv)
    private ImageView avatar;

    @ViewInject(R.id.improve_rv)
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Personal> data;

    private PopupWindow_Avatar menuWindow;

    @Event({
            R.id.actionbar_back_iv_sns,
            R.id.improve_avatar_iv,
            R.id.improve_complete_btn
    })
    private void viewClick(View view){
        switch (view.getId()){
            case R.id.actionbar_back_iv_sns:
                finish();
                break;
            case R.id.improve_avatar_iv:
                menuWindow = new PopupWindow_Avatar(this, itemsOnClick);
                menuWindow.showAtLocation(findViewById(R.id.activity_improve_rl),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.improve_complete_btn:
                break;
        }
    }

    @Override
    protected void initView() {
        snsImprovePresenter = new SnsImprovePresenterImpl(this,this);

        title.setText(R.string.improveInfo);

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        data = new ArrayList<>();
        adapter = new SnsAdapter(this,data);
        ((SnsAdapter)adapter).setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    public void onItemClick(int position) {
        String[] titleType = getResources().getStringArray(R.array.sns_info);
        Intent intent = new Intent();
        if (position == 4)
            intent.setClass(this,SnsCategoryActivity.class);
        else{
            intent.setClass(this,SnsImproveChildActivity.class);
            intent.putExtra("infoType", titleType[position]);
        }
        startActivity(intent);
    }

    private static final String IMAGE_FILE_NAME = "avatarImage.jpg";// 头像文件名称
    private String urlpath;			// 图片本地路径
    private String resultStr = "";	// 服务端返回结果集
    private static ProgressDialog pd;// 等待进度圈
    private static final int REQUESTCODE_PICK = 0;		// 相册选图标记
    private static final int REQUESTCODE_TAKE = 1;		// 相机拍照标记
    private static final int REQUESTCODE_CUTTING = 2;	// 图片裁切标记


    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                // 拍照
                case R.id.pop_avatar_photo:
                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //下面这句指定调用相机拍照后的照片存储的路径
                    takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(new File(Environment.getExternalStorageDirectory() , IMAGE_FILE_NAME)));
                    startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    break;
                // 相册选择图片
                case R.id.pop_avatar_album:
                    Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                    // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                    pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(pickIntent, REQUESTCODE_PICK);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_PICK:// 直接从相册获取
                try {
                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();// 用户点击取消操作
                }
                break;
            case REQUESTCODE_TAKE:// 调用相机拍照
                File temp = new File(Environment.getExternalStorageDirectory()  + "/" + IMAGE_FILE_NAME);
                startPhotoZoom(Uri.fromFile(temp));
                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    setPicToView(data);
                }
                break;

        }

    }

    private Uri uritempFile;
    /**
     * 裁剪图片方法实现
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * 保存裁剪之后的图片数据
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            // 取得SDCard图片路径做显示
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(null, photo);
            String dateStr = Utility.FORMAT_NUM.format(System.currentTimeMillis());
            int random =(int)(Math.random()*900)+100;
            urlpath = FileUtil.saveFile(this, "avatar" + dateStr + random + ".jpg", photo);
            x.image().bind(avatar,urlpath);
        }
    }
}
