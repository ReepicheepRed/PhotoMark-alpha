package com.zebra.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by zhiPeng.S on 2016/12/1.
 */

public class Utility {
    public static final SimpleDateFormat FORMAT_NUM = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);

    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    public static String getLiImageDir(Context context){
        File sd= context.getExternalCacheDir();
        if(sd == null){
            return null;
        }
        String path=sd.getPath();
        File file=new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    public static boolean isSavedToSd(Context context,String url){
        String filePath = convertToSdPath(context, url);
        if(filePath == null){
            return false;
        }
        File file = new File(filePath);
        return file.exists();
    }

    public static String convertToSdPath(Context context,String url){
        if(url == null || url.trim().equals("")){
            return null;
        }
        url = url.trim();
        String baseUrl = Constant.getBaseUrl();
        if(url.startsWith(baseUrl)){
            url = url.replace(baseUrl, "");
        }
//        if(url.startsWith(HEADER_IMAGE_URL_BASE)){
//            url = url.replace(HEADER_IMAGE_URL_BASE, "");
//        }
        String filePath = getLiImageDir(context);
        if(!url.startsWith("/")){
            filePath= filePath + "/";
        }

        filePath = filePath + url;
        return filePath;
    }

    /**
     *
     * 将bm存储到SD卡中
     * @param url 图片在服务器上的相对路径
     * @param bm 从服务器获得的图片
     * @return 存储成功 true ，失败 false
     */
    public static boolean saveImageToSd(Context context,String url, Bitmap bm){
        String sdStatus = Environment.getExternalStorageState();
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        String sdPath = convertToSdPath(context, url);
        if(sdPath == null){
            return false;
        }

        File file = new File(sdPath);
        File dir = file.getParentFile();
        if(!dir.exists()){
            dir.mkdirs();
        }
        BufferedOutputStream bos;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }  catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Bitmap getBitmapFromSdCard(Context context,String url){
        if(!isSavedToSd(context, url)){
            return null;
        }

        String filePath = convertToSdPath(context, url);
        if(filePath == null){
            return null;
        }

        Bitmap bm = BitmapFactory.decodeFile(filePath);
        return bm;
    }

    public static int[] trimLayout(String str){
        int[] position = new int[2];
        String[] posStr = str.substring(1,str.length()-1).split(",");
        position[0] = Integer.valueOf(posStr[0]);
        position[1] = Integer.valueOf(posStr[1]);
        return position;
    }

}
