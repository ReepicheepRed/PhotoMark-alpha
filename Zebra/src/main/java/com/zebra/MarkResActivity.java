package com.zebra;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zebra.adapter.MarkResAdapter;
import com.zebra.base.BaseActivity;
import com.zebra.bean.PatternsMark;
import com.zebra.contract.MarkResContract;
import com.zebra.presenter.MarkResPresenterImpl;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/30.
 */
@ContentView(R.layout.activity_mark_res)
public class MarkResActivity extends BaseActivity implements MarkResContract.View,MarkResAdapter.OnItemClickListener{
    private MarkResContract.Presenter markResPresenter;

    @ViewInject(R.id.actionbar_title)
    private TextView title;

    @ViewInject(R.id.mark_res_rc)
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<PatternsMark> data;


    @Event({
            R.id.actionbar_back
    })
    private void viewClick(View view){
        switch (view.getId()){
            case R.id.actionbar_back:
                finish();
                break;
        }
    }

    @Override
    protected void initView() {
        markResPresenter = new MarkResPresenterImpl(this,this);

        title.setText(R.string.add_label);

        data = new ArrayList<>();
        adapter = new MarkResAdapter(this, data);
        ((MarkResAdapter)adapter).setOnItemClickListener(this);
        layoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MarkResAdapter.SpacesItemDecoration(8));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        super.initData();
        markResPresenter.obtainMaterialResource(getIntent());
    }

    @Override
    public void showMarkResource(List<PatternsMark> list) {
        data = list;
        ((MarkResAdapter)adapter).setData(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showTitle(CharSequence charSequence) {
        title.setText(charSequence);
    }

    @Override
    public void onItemClick(int position) {
        markResPresenter.onItemClick(position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        markResPresenter.onActivityResult(requestCode, resultCode, data);
    }

}
