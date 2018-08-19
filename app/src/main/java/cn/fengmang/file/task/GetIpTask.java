package cn.fengmang.file.task;

import android.content.Context;

import cn.fengmang.baselib.ELog;
import cn.fengmang.file.service.TaskService;
import cn.fengmang.file.utils.NetUtils;

/**
 * Created by Administrator on 2018/8/17.
 */
public class GetIpTask extends TaskService.Task {
    protected Context mContext;

    public GetIpTask(Context Context) {
        tid = 1;
        this.mContext = Context;
    }

    @Override
    public void run() {
        String ip = NetUtils.getIpAddress(mContext);
        ELog.d("本机ip地址" + ip);
    }
}
