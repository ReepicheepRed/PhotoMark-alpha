package com.zebra;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.zebra.adapter.CategoryAdapter;
import com.zebra.adapter.ListBaseAdapter;
import com.zebra.base.BaseActivity;
import com.zebra.bean.Category;
import com.zebra.contract.CategoryContract;
import com.zebra.presenter.CategoryPresenterImpl;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/12/12.
 */
@ContentView(R.layout.activity_category)
public class SnsCategoryActivity extends BaseActivity implements CategoryContract.View{

    private CategoryContract.Presenter categoryPresenter;

    @ViewInject(R.id.actionbar_title_sns)
    private TextView title;

    @ViewInject(R.id.category_lv)
    private ListView listView;
    private ListBaseAdapter<Category> adapter;
    private List<Category> data;


    @Event({
            R.id.actionbar_back_iv_sns
    })
    private void viewClick(View view){
        switch (view.getId()){
            case R.id.actionbar_back_iv_sns:
                finish();
                break;
        }
    }

    @Override
    protected void initView() {
        categoryPresenter = new CategoryPresenterImpl(this,this);

        title.setText(R.string.category);

        data = new ArrayList<>();
        adapter = new CategoryAdapter(this,data);
        listView.setAdapter(adapter);
        categoryPresenter.obtainCategory();
    }

    @Override
    public void showCategory(List<Category> data){
        this.data = data;
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }

}
