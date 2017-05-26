package com.xiaopo.flying.photolayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.xiaopo.flying.photolayout.bean.PuzzleStyle;
import com.xiaopo.flying.photolayout.contract.ProcessContract;
import com.xiaopo.flying.photolayout.presenter.ProcessPresenterImpl;
import com.xiaopo.flying.poiphoto.Define;
import com.xiaopo.flying.poiphoto.PhotoPicker;
import com.xiaopo.flying.puzzle.Line;
import com.xiaopo.flying.puzzle.PuzzleLayout;
import com.xiaopo.flying.puzzle.PuzzleView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProcessActivity extends AppCompatActivity implements View.OnClickListener,
        PuzzleStyleAdapter.OnItemClickListener,SeekBar.OnSeekBarChangeListener,ProcessContract.View{

    private PuzzleLayout mPuzzleLayout;
    private List<String> mBitmapPaths;
    private PuzzleView mPuzzleView;

    private List<Target> mTargets = new ArrayList<>();
    private int mDeviceWidth = 0;

    private ProcessContract.Presenter presenter;
    private List<PuzzleStyle> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        presenter = new ProcessPresenterImpl(this,this);

//        int pieceSize = getIntent().getIntExtra("piece_size", 0);
//        int themeId = getIntent().getIntExtra("theme_id", 0);

        mDeviceWidth = getResources().getDisplayMetrics().widthPixels;

        String baseUrl = getIntent().getStringExtra("baseUrl");
        mBitmapPaths = getIntent().getStringArrayListExtra("photo_path");
        int pieceSize = mBitmapPaths.size();
        int themeId = 0;
        mPuzzleLayout = PuzzleUtil.getPuzzleLayout(pieceSize, themeId);
        presenter.obtainStyleInfo(pieceSize,baseUrl);

        initView();
        loadPhoto();

//       for zebra
        initViewForZebra();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadPhoto() {
        if (mBitmapPaths == null) {
            loadPhotoFromRes();
            return;
        }

        final List<Bitmap> pieces = new ArrayList<>();

        final int count = mBitmapPaths.size() > mPuzzleLayout.getBorderSize() ? mPuzzleLayout.getBorderSize() : mBitmapPaths.size();

        for (int i = 0; i < count; i++) {
            final Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    pieces.add(bitmap);
                    if (pieces.size() == count) {
                        if (mBitmapPaths.size() < mPuzzleLayout.getBorderSize()) {
                            for (int i = 0; i < mPuzzleLayout.getBorderSize(); i++) {
                                mPuzzleView.addPiece(pieces.get(i % count));
                            }
                        } else {
                            mPuzzleView.addPieces(pieces);
                        }
                    }
                    mTargets.remove(this);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Picasso.with(this)
                    .load("file:///" + mBitmapPaths.get(i))
                    .resize(mDeviceWidth, mDeviceWidth)
                    .centerInside()
                    .config(Bitmap.Config.RGB_565)
                    .into(target);

            mTargets.add(target);
        }

    }

    private void loadPhotoFromRes() {
        final List<Bitmap> pieces = new ArrayList<>();

        final int[] resIds = new int[]{
                R.drawable.demo1,
                R.drawable.demo2,
                R.drawable.demo3,
                R.drawable.demo4,
                R.drawable.demo5,
                R.drawable.demo6,
                R.drawable.demo7,
                R.drawable.demo8,
                R.drawable.demo9,
        };

        final int count = resIds.length > mPuzzleLayout.getBorderSize() ? mPuzzleLayout.getBorderSize() : resIds.length;

        for (int i = 0; i < count; i++) {
            final Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    pieces.add(bitmap);
                    if (pieces.size() == count) {
                        if (resIds.length < mPuzzleLayout.getBorderSize()) {
                            for (int i = 0; i < mPuzzleLayout.getBorderSize(); i++) {
                                mPuzzleView.addPiece(pieces.get(i % count));
                            }
                        } else {
                            mPuzzleView.addPieces(pieces);
                        }
                    }
                    mTargets.remove(this);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Picasso.with(this)
                    .load(resIds[i])
                    .config(Bitmap.Config.RGB_565)
                    .into(target);

            mTargets.add(target);
        }
    }

    private boolean isStartPick;
    private void initView() {
        ImageView btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });

        mPuzzleView = (PuzzleView) findViewById(R.id.puzzle_view);

        //TODO the method we can use to change the puzzle view's properties
        mPuzzleView.setPuzzleLayout(mPuzzleLayout);
        mPuzzleView.setMoveLineEnable(true);
        mPuzzleView.setNeedDrawBorder(true);
        mPuzzleView.setNeedDrawOuterBorder(true);
        mPuzzleView.setExtraSize(100);
        mPuzzleView.setBorderWidth(4);
        mPuzzleView.setBorderColor(Color.WHITE);
        mPuzzleView.setSelectedBorderColor(Color.parseColor("#99BBFB"));
//        mPuzzleView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(!isStartPick){
//                    isStartPick = true;
//                    showSelectedPhotoDialog();
//                }
//                return false;
//            }
//        });


        ImageView btnReplace = (ImageView) findViewById(R.id.btn_replace);
        ImageView btnRotate = (ImageView) findViewById(R.id.btn_rotate);
        ImageView btnFlipHorizontal = (ImageView) findViewById(R.id.btn_flip_horizontal);
        ImageView btnFlipVertical = (ImageView) findViewById(R.id.btn_flip_vertical);
        ImageView btnBorder = (ImageView) findViewById(R.id.btn_border);

        btnReplace.setOnClickListener(this);
        btnRotate.setOnClickListener(this);
        btnFlipHorizontal.setOnClickListener(this);
        btnFlipVertical.setOnClickListener(this);
        btnBorder.setOnClickListener(this);

        TextView btnSave = (TextView) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                File file = FileUtil.getNewFile(ProcessActivity.this, "PhotoMark");
//                File file = FileUtil.getNewFile(ProcessActivity.this, "Puzzle");
                mPuzzleView.save(file, new PuzzleView.Callback() {
                    @Override
                    public void onSuccess(String path) {
                        Snackbar.make(view, R.string.prompt_save_success, Snackbar.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("picPath",path);
                        ProcessActivity.this.setResult(2,intent);
                        ProcessActivity.this.finish();

                    }

                    @Override
                    public void onFailed() {
                        Snackbar.make(view, R.string.prompt_save_failed, Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    private void share() {
        final File file = FileUtil.getNewFile(this, "Puzzle");

        mPuzzleView.save(file, new PuzzleView.Callback() {
            @Override
            public void onSuccess(String path) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                Uri uri = Uri.fromFile(file);

                if (uri != null) {
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    shareIntent.setType("image/*");
                    startActivity(Intent.createChooser(shareIntent,
                            getString(R.string.prompt_share)));
                }
            }

            @Override
            public void onFailed() {
                Snackbar.make(mPuzzleView, R.string.prompt_share_failed, Snackbar.LENGTH_SHORT).show();
            }
        });


    }

    // for merging with zebra
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_replace )
                showSelectedPhotoDialog();
        if(view.getId() ==  R.id.btn_rotate)
                mPuzzleView.rotate(90f);
        if(view.getId() ==  R.id.btn_flip_horizontal)
                mPuzzleView.flipHorizontally();
        if(view.getId() == R.id.btn_flip_vertical)
                mPuzzleView.flipVertically();
        if(view.getId() == R.id.btn_border)
                mPuzzleView.setNeedDrawBorder(!mPuzzleView.isNeedDrawBorder());

        selectStyle(view.getId());
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.btn_replace:
//                showSelectedPhotoDialog();
//                break;
//            case R.id.btn_rotate:
//                mPuzzleView.rotate(90f);
//                break;
//            case R.id.btn_flip_horizontal:
//                mPuzzleView.flipHorizontally();
//                break;
//            case R.id.btn_flip_vertical:
//                mPuzzleView.flipVertically();
//                break;
//            case R.id.btn_border:
//                mPuzzleView.setNeedDrawBorder(!mPuzzleView.isNeedDrawBorder());
//                break;
//        }
//    }

    private void showSelectedPhotoDialog() {
        PhotoPicker.newInstance()
                .setMaxCount(1)
                .pick(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isStartPick = false;
        if (requestCode == Define.DEFAULT_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> paths = data.getStringArrayListExtra(Define.PATHS);
            if(paths.size() <= 0) return;
            String path = paths.get(0);

            final Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mPuzzleView.replace(bitmap);

                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Snackbar.make(mPuzzleView,"Replace Failed!",Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Picasso.with(this)
                    .load("file:///" + path)
                    .resize(mDeviceWidth, mDeviceWidth)
                    .centerInside()
                    .config(Bitmap.Config.RGB_565)
                    .into(target);
        }
    }

    //to respect design drawing
    private TextView style_tv,background_tv,border_tv,currentTab;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.ItemDecoration decoration1,decoration2;
    private LinearLayout border_ll;
    private SeekBar inner_sbs,outer_sbs;

    private void initViewForZebra(){
        style_tv = (TextView) findViewById(R.id.style_tv);
        background_tv = (TextView) findViewById(R.id.background_tv);
        border_tv = (TextView) findViewById(R.id.border_tv);

        style_tv.setOnClickListener(this);
        background_tv.setOnClickListener(this);
        border_tv.setOnClickListener(this);

        border_ll = (LinearLayout) findViewById(R.id.process_border_ll);
        inner_sbs = (SeekBar) findViewById(R.id.inner_sbs);
        outer_sbs = (SeekBar) findViewById(R.id.outer_sbs);
        inner_sbs.setOnSeekBarChangeListener(this);
        outer_sbs.setOnSeekBarChangeListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.process_item_rv);
        data = new ArrayList<>();
        adapter = new PuzzleStyleAdapter(this,data);
        ((PuzzleStyleAdapter)adapter).setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        decoration1 = new PuzzleStyleAdapter.SpacesItemDecoration(this,20);
        decoration2  = new PuzzleStyleAdapter.SpacesItemDecoration(this,15);

        selectStyle(R.id.style_tv);

    }

    private void selectStyle(int id){
        if(id == R.id.style_tv){
            selectStyleTab(style_tv);

            border_ll.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            ((PuzzleStyleAdapter)adapter).setColor(false);
            layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.removeItemDecoration(decoration2);
            recyclerView.addItemDecoration(decoration1);
            adapter.notifyDataSetChanged();
        }
        if(id == R.id.background_tv){
            selectStyleTab(background_tv);

            border_ll.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            ((PuzzleStyleAdapter)adapter).setColor(true);
            layoutManager = new GridLayoutManager(this,6);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.removeItemDecoration(decoration1);
            recyclerView.addItemDecoration(decoration2);
            adapter.notifyDataSetChanged();
        }
        if(id == R.id.border_tv){
            selectStyleTab(border_tv);

            border_ll.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            recyclerView.removeItemDecoration(decoration1);
            recyclerView.removeItemDecoration(decoration2);
        }
    }

    private void selectStyleTab(TextView selectTab){
        if(currentTab == null) currentTab = selectTab;
        Drawable[] drawables = currentTab.getCompoundDrawables();
        currentTab.setTextColor(getResources().getColor(R.color.gray_c));
        currentTab.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        currentTab = selectTab;
        currentTab.setTextColor(getResources().getColor(R.color.red_c));
        currentTab.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawables[3]);
    }

    @Override
    public void onItemClick(int position, View view) {
        boolean isColors = ((PuzzleStyleAdapter)adapter).isColor();
        if(isColors){
            int color = ((PuzzleStyleAdapter)adapter).getColors().getColor(position, Color.WHITE);
            mPuzzleView.setBorderColor(color);
            return;
        }
        int pieceSize,themeId = 0;
        PuzzleStyle puzzleStyle = data.get(position);
        pieceSize = puzzleStyle.getFdi();
        try{
            themeId = Integer.valueOf(puzzleStyle.getThemeid());
        }catch (Exception e){
            e.printStackTrace();
        }

        mPuzzleLayout = PuzzleUtil.getPuzzleLayout(pieceSize, themeId);
        mPuzzleView.setPuzzleLayout(mPuzzleLayout);
        loadPhoto();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mPuzzleView.setBorderWidth(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void showStyle(List<PuzzleStyle> data) {
        this.data = data;
        ((PuzzleStyleAdapter)adapter).setData(data);
        adapter.notifyDataSetChanged();
    }
}
