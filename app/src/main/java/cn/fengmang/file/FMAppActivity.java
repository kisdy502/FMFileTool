package cn.fengmang.file;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.fengmang.baselib.ELog;
import cn.fengmang.file.adapter.AppListAdapter;
import cn.fengmang.file.bean.AppInfo;
import cn.fengmang.file.utils.AppHelper;
import cn.fengmang.file.widget.StatusItemView;
import cn.fengmang.file.widget.StatusView;
import cn.fengmang.libui.flying.DrawableFlyingFrameView;
import cn.fengmang.libui.recycler.FMRecyclerView;
import cn.fengmang.libui.recycler.OnItemClickListener;
import cn.fengmang.libui.recycler.OnItemFocusChangeListener;
import cn.fengmang.libui.recycler.OnItemLongClickListener;
import cn.fengmang.libui.recycler.V7LinearLayoutManager;

/**
 * Created by Administrator on 2018/7/23.
 */

public class FMAppActivity extends FMBaseTitleActivity implements OnItemClickListener, OnItemFocusChangeListener, OnItemLongClickListener {

    private FMRecyclerView mRecyclerView;
    private DrawableFlyingFrameView mFlyingView;
    private List<AppInfo> mAppList;
    private AppListAdapter mAdapter;
    private AppReceiver mReceiver;
    private StatusView mStatusView;
    private TextView tvFooter;
    private int mStatus = StatusView.STATUS_USER;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fm_activity_app);
        initReceiver();

    }

    private void initReceiver() {
        mReceiver = new AppReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");        //必不可少
        registerReceiver(mReceiver, filter);
    }


    @Override
    public void initUI() {
        super.initUI();
        mRecyclerView = findViewById(R.id.appList);
        mFlyingView = DrawableFlyingFrameView.build(this);
        mFlyingView.setFlyingDrawable(getResources().getDrawable(R.drawable.hover_item));
        mRecyclerView.setOnItemClickListener(this);
        mRecyclerView.setOnItemFocusListener(this);
        mRecyclerView.setOnItemLongClickListener(this);
        mStatusView = findViewById(R.id.statusView);
        mStatusView.setFocusable(true);
        mStatusView.setOnItemFocusChangeListener(new StatusView.OnItemFocusChangeListener() {
            @Override
            public void onItemFocusChange(StatusItemView itemView) {
                mFlyingView.onMoveTo(itemView, 1.0f, 1.0f, 0);
            }
        });
        mStatusView.setOnItemClickListener(new StatusView.OnItemClickListener() {
            @Override
            public void onItemClickListener(StatusItemView itemView, int status) {
                ELog.d("status:" + status);
                mStatus = status;
                refreshData();
                setSubTitle(String.format("共%d个应用", mAppList.size()));
                setFooter();

            }
        });
        tvFooter = findViewById(R.id.tvFooter);
        setMainTitle("应用列表");
        initData();
        setSubTitle(String.format("共%d个应用", mAppList.size()));
        setFooter();


    }

    private void initData() {
        mAppList = AppHelper.getAppInfoList(this, mStatus);
        V7LinearLayoutManager layoutManager = new V7LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new AppListAdapter(mAppList, this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.requestFocus();
    }

    private void refreshData() {
        mAppList = AppHelper.getAppInfoList(this, mStatus);
        mAdapter = new AppListAdapter(mAppList, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public void onItemClick(RecyclerView parent, View itemView, int position) {
        AppHelper.startAppLaunch(this, mAppList.get(position).getmPackageInfo());
    }

    @Override
    public boolean onItemPreSelected(@NonNull RecyclerView parent, @NonNull View itemView, int position) {
        return false;
    }

    @Override
    public boolean onItemSelected(@NonNull RecyclerView parent, @NonNull View itemView, int position) {
        mFlyingView.onMoveTo(itemView, 1.1f, 1.1f, 0);
        return false;
    }

    @Override
    public boolean onReviseFocusFollow(@NonNull RecyclerView parent, @NonNull View itemView, int position) {
        mFlyingView.onMoveTo(itemView, 1.1f, 1.1f, 0);
        return false;
    }

    @Override
    public boolean onItemLongClick(RecyclerView parent, View itemView, int position) {
        final String packageName = mAppList.get(position).getmPackageInfo().packageName;
        if (packageName.equals(getPackageName())) {
            Toast.makeText(this, "不能卸载自己", Toast.LENGTH_SHORT).show();
        }
        AppHelper.uninstall(packageName, this);
        return true;
    }


    private void setFooter() {
        if (mStatus == StatusView.STATUS_USER) {
            tvFooter.setText("长按OK/确定删除选中的应用");
        } else {
            tvFooter.setText("系统应用需要root权限才能卸载!");
        }
    }


    private class AppReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String packageName = intent.getData().getSchemeSpecificPart();
            ELog.d("packageName:" + packageName);
            ELog.d("action:" + intent.getAction());
            if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {
                if (AppHelper.isSystemApp(packageName, context) && mStatus == StatusView.STATUS_SYSTEM) {
                    addAppInfo(context, packageName);
                } else if (AppHelper.isUserApp(packageName, context) && mStatus == StatusView.STATUS_USER) {
                    addAppInfo(context, packageName);
                }
            } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED)) {

            } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
                removeAppInfo(packageName);
            }
            setSubTitle(String.format("共%d个应用", mAppList.size()));
        }
    }


    private void addAppInfo(Context context, final String packageName) {
        PackageInfo packageInfo = AppHelper.getPackageInfo(packageName, context);
        if (packageInfo != null) {
            AppInfo appInfo = new AppInfo(packageInfo);
            mAppList.add(appInfo);
            mAdapter.notifyItemRangeInserted(mAppList.size() - 1, 1);
        }
    }

    private void removeAppInfo(final String packageName) {
        int index = indexInList(packageName);
        if (index >= 0) {
            mAppList.remove(index);
            mAdapter.notifyItemRangeRemoved(index, 1);
        } else {
            ELog.d("应用不在列表中!");
        }
    }

    public int indexInList(String packageName) {
        int i, size = mAppList.size();
        for (i = 0; i < size; i++) {
            if (mAppList.get(i).getmPackageInfo().packageName.equals(packageName))
                return i;
        }
        return -1;
    }
}
