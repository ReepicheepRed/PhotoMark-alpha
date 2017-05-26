package com.xiaopo.flying.photolayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaopo.flying.photolayout.bean.PuzzleStyle;

import org.xutils.x;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/29.
 */

public class PuzzleStyleAdapter extends RecyclerView.Adapter<PuzzleStyleAdapter.ViewHolder> implements View.OnClickListener{
    private static final String TAG = PuzzleStyleAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private RecyclerView mRecyclerView;
    private OnItemClickListener onItemClickListener;
    private Context mContext;
    private TypedArray colors;
    private boolean[] flag;
    private boolean isColor;
    private int[] drswables;
    private List<PuzzleStyle> data;
    public PuzzleStyleAdapter(Context context) {
        mContext = context;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        colors = context.getResources().obtainTypedArray(R.array.color_array_puzzle);
        drswables = new int[]{
                R.mipmap.yangshi_icon_1_moren,
                R.mipmap.yangshi_icon_1_xuanze,
                R.mipmap.yangshi_icon_2_moren,
                R.mipmap.yangshi_icon_2_xuanze,
                R.mipmap.yangshi_icon_3_moren,
                R.mipmap.yangshi_icon_3_xuanze,
                R.mipmap.yangshi_icon_4_moren,
                R.mipmap.yangshi_icon_4_xuanze,
                R.mipmap.yangshi_icon_5_moren,
                R.mipmap.yangshi_icon_5_xuanze,
        };
    }

    public PuzzleStyleAdapter(Context context, List<PuzzleStyle> data) {
        this(context);
        this.data = data;
    }

    public List<PuzzleStyle> getData() {
        return data;
    }

    public void setData(List<PuzzleStyle> data) {
        this.data = data;
    }

    public boolean isColor() {
        return isColor;
    }

    public void setColor(boolean color) {
        isColor = color;
    }

    public TypedArray getColors() {
        return colors;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_process_style, parent, false);
        itemView.setOnClickListener(this);
        return new PuzzleStyleAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(isColor) {
            holder.frameLayout.setVisibility(View.GONE);
            holder.color.setVisibility(View.VISIBLE);
            holder.color.setBackgroundColor(colors.getColor(position, Color.WHITE));
            return;
        }

        if(getItemCount() <= 0) return;

        PuzzleStyle puzzleStyle = data.get(position);
        holder.color.setVisibility(View.GONE);
        holder.frameLayout.setVisibility(View.VISIBLE);
//        holder.style.setImageResource(drswables[position*2]);
        x.image().bind(holder.style,puzzleStyle.getFilepath());
        if(puzzleStyle.isSelected())
            holder.token.setVisibility(View.VISIBLE);
        else
            holder.token.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return isColor ? colors.length() : data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView color;

        ImageView style,token;

        FrameLayout frameLayout;

        private ViewHolder(View itemView) {
            super(itemView);
            color = (TextView) itemView.findViewById(R.id.item_puzzle_color_tv);
            style = (ImageView) itemView.findViewById(R.id.item_puzzle_style_iv);
            token = (ImageView) itemView.findViewById(R.id.item_puzzle_selected_iv);
            frameLayout = (FrameLayout) itemView.findViewById(R.id.item_puzzle_style_fl);
        }
    }

    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(Context context, float dpValue) {
            float scale = context.getResources().getDisplayMetrics().density;
            this.space = (int) (dpValue * scale + 0.5f);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,RecyclerView parent,RecyclerView.State state) {
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
