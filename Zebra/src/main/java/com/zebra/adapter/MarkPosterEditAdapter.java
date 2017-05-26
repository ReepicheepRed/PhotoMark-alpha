package com.zebra.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zebra.R;
import com.zebra.util.BitmapUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/29.
 */

public class MarkPosterEditAdapter extends RecyclerView.Adapter<MarkPosterEditAdapter.ViewHolder> implements View.OnClickListener{
    private static final String TAG = MarkPosterEditAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private RecyclerView mRecyclerView;
    private OnItemClickListener onItemClickListener;
    private Context mContext;
    private TypedArray colors;
    private String[] fonts;
    private boolean[] flag;
    private List<Typeface> typeFaces = new ArrayList<>();
    private boolean isColor;
    public MarkPosterEditAdapter(Context context) {
        mContext = context;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        colors = context.getResources().obtainTypedArray(R.array.color_array);
        try {
            fonts = context.getAssets().list("fonts");
            for (String font : fonts) {
                Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/" + font);
                typeFaces.add(typeFace);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        flag = new boolean[fonts.length];
        flag[0] = true;
    }

    public boolean isColor() {
        return isColor;
    }

    public void setColor(boolean color) {
        isColor = color;
    }

    public List<Typeface> getTypeFaces() {
        return typeFaces;
    }

    public TypedArray getColors() {
        return colors;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_poster_edit, parent, false);
        itemView.setOnClickListener(this);
        return new MarkPosterEditAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(isColor) {
            holder.font.setVisibility(View.GONE);
            holder.color.setVisibility(View.VISIBLE);
            holder.color.setBackgroundColor(colors.getColor(position, Color.WHITE));
            return;
        }
        holder.font.setVisibility(View.VISIBLE);
        holder.color.setVisibility(View.GONE);
        if(flag[position])
            holder.token.setVisibility(View.VISIBLE);
        else
            holder.token.setVisibility(View.INVISIBLE);
        holder.name.setText(fonts[position]);
    }

    @Override
    public int getItemCount() {
        return isColor ? colors.length() : fonts.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @ViewInject(R.id.iPoster_color)
        TextView color;

        @ViewInject(R.id.iPoster_rl)
        RelativeLayout font;

        @ViewInject(R.id.iPoster_token_iv)
        ImageView token;

        @ViewInject(R.id.iPoster_font_tv)
        TextView name;

        @ViewInject(R.id.iPoster_download_btn)
        Button download;


        private ViewHolder(View itemView) {
            super(itemView);
            x.view().inject(this, itemView);
        }
    }

    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = BitmapUtil.dip2px(x.app(),space);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.top = space;
            outRect.left = space;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        int childAdapterPosition = mRecyclerView.getChildAdapterPosition(v);
        if (onItemClickListener!=null) {
            onItemClickListener.onItemClick(childAdapterPosition,v);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position, View view);
    }
}
