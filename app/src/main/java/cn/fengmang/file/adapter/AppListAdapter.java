package cn.fengmang.file.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import cn.fengmang.baselib.ELog;
import cn.fengmang.file.R;
import cn.fengmang.file.bean.AppInfo;
import cn.fengmang.file.utils.DateUtil;
import cn.fengmang.file.utils.FileOptHelper;
import cn.fengmang.file.utils.PackageUtils;
import cn.fengmang.file.utils.SpannableUtils;

/**
 * Created by Administrator on 2018/7/23.
 */

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder> {

    private List<AppInfo> appInfoList;
    private Context mContext;
    private PackageManager mPackageManager;

    public AppListAdapter(List<AppInfo> appInfoList, Context context) {
        mContext = context;
        this.appInfoList = appInfoList;
        mPackageManager = mContext.getPackageManager();
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fm_app_item_layout, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {
        AppInfo appInfo = appInfoList.get(position);
        holder.tvAppName.setText(appInfo.getmPackageInfo().applicationInfo.loadLabel(mPackageManager).toString());
        final String packageName = appInfo.getmPackageInfo().packageName;
        highlightVersion(holder.tvAppVersion, appInfo.getmPackageInfo().versionName);
        holder.tvAppPackageName.setText(packageName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            holder.imgAppIcon.setBackground(appInfo.getmPackageInfo().applicationInfo.loadIcon(mPackageManager));
        } else {
            holder.imgAppIcon.setBackgroundDrawable(appInfo.getmPackageInfo().applicationInfo.loadIcon(mPackageManager));
        }
        highlightMd5(holder.tvAppMd5, PackageUtils.getFingerprintMd5(packageName, mContext));
        highlightSha1(holder.tvAppSha1, PackageUtils.getFingerprintSha1(packageName, mContext));
        //应用装时间
        long firstInstallTime = appInfo.getmPackageInfo().firstInstallTime;
        //应用最后一次更新时间
        long lastUpdateTime = appInfo.getmPackageInfo().lastUpdateTime;
        ELog.d("首次安装:" + DateUtil.timestampToDateString(firstInstallTime) + "更新时间:" + DateUtil.timestampToDateString(lastUpdateTime));
        String sourceDir = appInfo.getmPackageInfo().applicationInfo.sourceDir;
        ELog.d("sourceDir:" + sourceDir);
        File file = new File(sourceDir);
        //得到apk的大小
        long size = file.length();
        ELog.d("size:" + FileOptHelper.convertStorage(size));
        holder.tvAppSize.setText(FileOptHelper.convertStorage(size));
    }

    @Override
    public int getItemCount() {
        return appInfoList != null ? appInfoList.size() : 0;
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAppName;
        private TextView tvAppVersion;
        private TextView tvAppPackageName;
        private TextView tvAppSize;
        private TextView tvAppMd5;
        private TextView tvAppSha1;
        private ImageView imgAppIcon;

        public AppViewHolder(View itemView) {
            super(itemView);
            tvAppName = itemView.findViewById(R.id.tvAppName);
            tvAppVersion = itemView.findViewById(R.id.tvAppVersion);
            tvAppPackageName = itemView.findViewById(R.id.tvAppPackageName);
            tvAppSize = itemView.findViewById(R.id.tvAppSize);
            imgAppIcon = itemView.findViewById(R.id.imgAppIcon);
            tvAppMd5 = itemView.findViewById(R.id.tvAppMd5);
            tvAppSha1 = itemView.findViewById(R.id.tvAppSha1);
        }
    }

    private void highlightVersion(TextView textView, String version) {
        if(TextUtils.isEmpty(version)){
            textView.setText(mContext.getString(R.string.fm_version_info, "0"));
        }else{
            String text = mContext.getString(R.string.fm_version_info, version);
            SpannableStringBuilder sb = SpannableUtils.matcherSearchTitle(Color.parseColor("#87CEFA"), text, version);
            textView.setText(sb);
        }
    }

    private void highlightMd5(TextView textView, String md5) {
        String text = mContext.getString(R.string.fm_md5_info, md5);
        SpannableStringBuilder sb = SpannableUtils.matcherSearchTitle(Color.WHITE, text, md5);
        textView.setText(sb);
    }

    private void highlightSha1(TextView textView, String sha1) {
        String text = mContext.getString(R.string.fm_sha1_info, sha1);
        SpannableStringBuilder sb = SpannableUtils.matcherSearchTitle(Color.WHITE, text, sha1);
        textView.setText(sb);
    }
}
