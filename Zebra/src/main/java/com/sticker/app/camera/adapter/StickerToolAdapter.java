package com.sticker.app.camera.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.zebra.R;
import com.sticker.app.model.Addon;

import java.util.List;

/**
 * 
 * 贴纸适配器
 * @author tongqian.ni
 */
public class StickerToolAdapter extends BaseAdapter {

    List<Addon> filterUris;
    Context     mContext;

    public StickerToolAdapter(Context context, List<Addon> effects) {
        filterUris = effects;
        mContext = context;
    }

    public List<Addon> getData() {
        return filterUris;
    }

    public void setData(List<Addon> data) {
        this.filterUris = data;
    }

    @Override
    public int getCount() {
        return filterUris == null ? 1 : filterUris.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return filterUris.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EffectHolder holder = null;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_bottom_tool, null);
            holder = new EffectHolder();
            holder.logo = (ImageView) convertView.findViewById(R.id.effect_image);
            holder.container = (ImageView) convertView.findViewById(R.id.effect_background);
            convertView.setTag(holder);
        } else {
            holder = (EffectHolder) convertView.getTag();
        }


        if(position == 0) {
            holder.logo.setImageResource(R.drawable.tab_btn_quanbu);
            return convertView;
        }
        final Addon effect = (Addon) getItem(position-1);
        Bitmap bm = BitmapFactory.decodeFile(effect.getImgPath());
        holder.logo.setImageBitmap(bm);
        return convertView;
    }


    private class EffectHolder {
        ImageView logo;
        ImageView container;
    }

}
