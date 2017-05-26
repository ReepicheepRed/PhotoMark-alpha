package com.zebra.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/12/1.
 */

public abstract class BaseAdapterForRV<T> extends RecyclerView.Adapter{

    private Context mContext;
    private List<T> mData;

    public BaseAdapterForRV(Context mContext, List<T> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public Context getContext(){
        return mContext;
    }

    public void setData(List<T> list){
        this.mData = list;
    }

    public List<T> getData(){
        return mData;
    }

    @Override
    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) ;

    @Override
    public abstract void onBindViewHolder(RecyclerView.ViewHolder holder, int position) ;

    @Override
    public int getItemCount() {
        return this.mData != null ? mData.size() : 0;
    }

    public void clear(){
        if(mData != null){
            mData.clear();
            this.notifyDataSetChanged();
        }
    }
}
