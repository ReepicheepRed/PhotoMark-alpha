package com.zebra.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zebra.R;
import com.zebra.bean.Category;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/12/12.
 */

public class CategoryAdapter extends ListBaseAdapter<Category> {
    private LayoutInflater mInflater;
    private Holder holder;
    public CategoryAdapter(Context context, List<Category> list) {
        super(context, list);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_category, null);
            holder = new Holder();
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }

        if(getCount() == 0){
            return  convertView;
        }

        final Category category = getData().get(position);
        holder.title.setText(category.getFname());

        return convertView;
    }

    private class Holder {
        @ViewInject(R.id.item_Category_name)
        public TextView title;
        @ViewInject(R.id.item_Category_iv)
        public ImageView tag;
    }
}
