package com.zebra.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sticker.app.model.Addon;
import com.zebra.R;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 
 * 贴纸适配器
 * @author tongqian.ni
 */
public class LabelAdapter2 extends ListBaseAdapter<Addon> {

    List<Addon> filterUris;
    Context     mContext;

    public LabelAdapter2(Context context, List<Addon> effects) {
        super(context,effects);
        filterUris = effects;
        mContext = context;
    }

    @Override
    public int getCount() {
        return filterUris.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mark_label, null);
            holder = new EffectHolder();
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (EffectHolder) convertView.getTag();
        }

        final Addon effect = (Addon) getItem(position);

        return showItem(convertView, holder, effect);
    }

    private View showItem(View convertView, EffectHolder holder, final Addon sticker) {
        x.image().bind(holder.logo,sticker.getPath());
        return convertView;
    }

    private class EffectHolder {
        @ViewInject(R.id.i_mark_iv)
        ImageView logo;
        @ViewInject(R.id.i_label)
        TextView label;
    }

}
