package cn.fengmang.file.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import cn.fengmang.file.R;

/**
 * 用于所有的DialogFragment的基类
 * 
 * @Author : DanBin
 * @Date : 2016年12月20日下午4:01:02
 * 
 */
public abstract class BaseDialogFragment extends DialogFragment implements OnKeyListener {
	
	protected View mRootView;

	protected abstract String getUmengTag();

	protected abstract int setContentView();

	protected abstract void initUI();

	protected abstract void fillData();
	
	protected Activity mActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mRootView == null) {
			mRootView = inflater.inflate(setContentView(), container, false);
			initUI();
		}
		ViewGroup parent = (ViewGroup) mRootView.getParent();
		if (parent != null) {
			parent.removeView(mRootView);
		}
		fillData();
		return mRootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}
	
	@Override
	public void onActivityCreated(Bundle bundle) {
		super.onActivityCreated(bundle);
		mActivity = getActivity();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setDialogSize();
	}

	/* 设置屏幕大小 */
	protected void setDialogSize() {
		Dialog dlg = getDialog();
		dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		WindowManager.LayoutParams params = dlg.getWindow().getAttributes();
		DisplayMetrics dm = getResources().getDisplayMetrics();
		params.width = dm.widthPixels;
		params.height = dm.heightPixels;
		dlg.getWindow().setAttributes(params);
	}

	@Override
	public void onPause() {
		super.onPause();	
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dlg = new Dialog(getActivity(), R.style.fm_default_dialog);
		dlg.setOnKeyListener(this);
		return dlg;
	}

	/**
	 * Fragment不可用状态
	 * @return
	 */
	protected boolean notAlive() {
		if (!isAdded() || isDetached()) {
			return true;
		}
		return false;
	}

}
