package cn.fengmang.file;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.fengmang.file.utils.MemHelper;
import cn.fengmang.libui.flying.DrawableFlyingFrameView;
import cn.fengmang.file.utils.PermissionsUtil;
import cn.fengmang.libui.recycler.FMRecyclerView;
import cn.fengmang.libui.widget.XRecyclerView;
import cn.fengmang.libui.widget.V27GridLayoutManager;

public class FMainActivity extends FMBaseActivity {

    private XRecyclerView mTvList;
    private GridLayoutManager mGridLayoutManager;
    private DrawableFlyingFrameView mFlyingView;

    private static List<MenuItem> mDatas = new ArrayList<>();

    static {
        mDatas.add(new MenuItem("应用管理", 0));
        mDatas.add(new MenuItem("文件管理", 1));
        mDatas.add(new MenuItem("文件分类", 2));
        mDatas.add(new MenuItem("远程管理", 2));
        mDatas.add(new MenuItem("内存清理", 2));
        mDatas.add(new MenuItem("大文件管理", 2));
    }

    private MenuAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fm_activity_main);
        initView();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionsUtil.verifyStoragePermissions(this);      //API23+权限获取
        }
        mFlyingView = DrawableFlyingFrameView.build(this);
        mFlyingView.setFlyingDrawable(getResources().getDrawable(R.drawable.hover_item));
        MemHelper.printfTvInfo(this);
        try {
            testCreateFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void initView() {
        mTvList = ((XRecyclerView) findViewById(R.id.trv));
        mGridLayoutManager = new V27GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false);
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
        mTvList.setOnItemListener(new XRecyclerView.OnItemListener() {
            @Override
            public void onItemPreSelected(XRecyclerView parent, View itemView, int position) {

            }

            @Override
            public void onItemSelected(XRecyclerView parent, View itemView, int position) {
                mFlyingView.onMoveTo(itemView, 1.0f, 1.0f, 0f);
            }

            @Override
            public void onItemClick(XRecyclerView parent, View itemView, int position) {
                if (position == 0) {
                    startActivity(new Intent(FMainActivity.this, FileTestActivity.class));
                } else if (position == 1) {
                    startActivity(new Intent(FMainActivity.this, FMFileActivity.class));
                }
            }

            @Override
            public boolean onItemLongClick(XRecyclerView parent, View itemView, int position) {
                return false;
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


    private void testCreateFile() throws IOException {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdCard = Environment.getExternalStorageDirectory();
            File testFile = new File(sdCard, "index");
            if (!testFile.exists()) {
                testFile.mkdirs();
                File temp;
                for (int i = 0; i < 1200; i++) {
                    temp = new File(testFile, String.valueOf(i));
                    temp.createNewFile();
                }
            }
        }

    }
}
