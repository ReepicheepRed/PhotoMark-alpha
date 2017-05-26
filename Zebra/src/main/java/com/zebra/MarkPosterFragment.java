package com.zebra;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.coolstar.makeposter.ExceptionHandler;
import com.coolstar.makeposter.utils.DeviceInfo;
import com.zebra.adapter.MarkPosterAdapter;
import com.zebra.base.BaseFragment;
import com.zebra.bean.PatternsMark;
import com.zebra.contract.MarkPosterContract;
import com.zebra.presenter.MarkPosterPresenterImpl;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/12/15.
 */
@ContentView(R.layout.fragment_mark_poster)
public class MarkPosterFragment extends BaseFragment implements MarkPosterContract.View ,MarkPosterAdapter.OnItemClickListener{
    private MarkPosterContract.Presenter markPosterPresenter;
    @ViewInject(R.id.mPoster_rv)
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<PatternsMark> data;


    public static MarkPosterFragment newInstance(int position){
        MarkPosterFragment fragment = new MarkPosterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position",position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView() {
        markPosterPresenter = new MarkPosterPresenterImpl(getActivity(),this);
        data = new ArrayList<>();
        adapter = new MarkPosterAdapter(getActivity(),data);
        ((MarkPosterAdapter)adapter).setOnItemClickListener(this);
        layoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new MarkPosterAdapter.SpacesItemDecoration(8));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void initData() {
        super.initData();
        DeviceInfo.init(getActivity());
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        markPosterPresenter.obtainPosterResource();
    }

    @Override
    public void showPoster(List<PatternsMark> list) {
        this.data = list;
        ((MarkPosterAdapter)adapter).setData(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position, View view) {
        markPosterPresenter.onItemClick(position,view);
    }
}
