package cn.fengmang.file;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.fengmang.file.utils.MemHelper;
import cn.fengmang.file.utils.PermissionsUtil;
import cn.fengmang.libui.flying.DrawableFlyingFrameView;
import cn.fengmang.libui.recycler.FMRecyclerView;
import cn.fengmang.libui.recycler.OnItemClickListener;
import cn.fengmang.libui.recycler.OnItemFocusChangeListener;

public class FMainActivity extends FMBaseActivity {

    private FMRecyclerView mTvList;
    private GridLayoutManager mGridLayoutManager;
    private DrawableFlyingFrameView mFlyingView;

    private static List<MenuItem> mDatas = new ArrayList<>();

    static {
        mDatas.add(new MenuItem("文件管理", 0));
        mDatas.add(new MenuItem("应用管理", 1));
        mDatas.add(new MenuItem("文件分类", 2));
        mDatas.add(new MenuItem("远程管理", 2));
        mDatas.add(new MenuItem("内存清理", 2));
        mDatas.add(new MenuItem("大文件管理", 2));
    }

    private MenuAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fm_activity_main);
        initView();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionsUtil.verifyStoragePermissions(this);      //API23+权限获取
        }
        mFlyingView = DrawableFlyingFrameView.build(this);
        mFlyingView.setFlyingDrawable(getResources().getDrawable(R.drawable.hover_item));
        MemHelper.printfTvInfo(this);
    }


    private void initView() {
        mTvList = ((FMRecyclerView) findViewById(R.id.trv));
        mGridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int spansize = 1;
                switch (mAdapter.getItemViewType(position)) {
                    case 0:
                        spansize = 3;
                        break;
                    case 1:
                        spansize = 2;
                        break;
                    case 2:
                    default:
                        spansize = 1;
                        break;
                }
                return spansize;
            }
        });

        mTvList.setLayoutManager(mGridLayoutManager);
        mTvList.setOnItemFocusListener(new OnItemFocusChangeListener() {
            @Override
            public boolean onItemPreSelected(@NonNull RecyclerView parent, @NonNull View itemView, int position) {
                return false;
            }

            @Override
            public boolean onItemSelected(@NonNull RecyclerView parent, @NonNull View itemView, int position) {
                mFlyingView.onMoveTo(itemView, 1.0f, 1.0f, 0f);
                return false;
            }

            @Override
            public boolean onReviseFocusFollow(@NonNull RecyclerView parent, @NonNull View itemView, int position) {
                return false;
            }
        });

        mTvList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View itemView, int position) {
                if (position == 0) {
                    startActivity(new Intent(FMainActivity.this, FMFileActivity.class));
                } else if (position == 1) {
                    startActivity(new Intent(FMainActivity.this, FMAppActivity.class));
                }

            }
        });
        mAdapter = new MenuAdapter();
        mTvList.setAdapter(mAdapter);
    }


    class MenuAdapter extends FMRecyclerView.Adapter<MenuHolder> {
        @Override
        public MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            switch (viewType) {
                case 0:
                    itemView = inflater.inflate(R.layout.fm_menu_item_high_layout, parent, false);
                    break;
                case 1:
                    itemView = inflater.inflate(R.layout.fm_menu_item_mid_layout, parent, false);
                    break;
                case 2:
                default:
                    itemView = inflater.inflate(R.layout.fm_menu_item_layout, parent, false);
                    break;
                //设置监听
            }

            return new MenuHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MenuHolder holder, int position) {
            holder.itemDesc.setText(mDatas.get(position).data);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mDatas.get(position).itemType;
        }
    }

    static class MenuItem {

        public MenuItem(String data, int itemType) {
            this.data = data;
            this.itemType = itemType;
        }

        String data;
        int itemType;
    }

    class MenuHolder extends RecyclerView.ViewHolder {
        public ImageView itemIcon;
        public TextView itemDesc;

        public MenuHolder(View itemView) {
            super(itemView);
            itemIcon = itemView.findViewById(R.id.item_icon);
            itemDesc = itemView.findViewById(R.id.item_desc);
        }
    }



}
