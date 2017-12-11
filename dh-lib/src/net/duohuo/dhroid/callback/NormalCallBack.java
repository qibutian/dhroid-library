package net.duohuo.dhroid.callback;

import java.net.UnknownHostException;

import org.json.JSONObject;

import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.IocContainer;
import net.duohuo.dhroid.util.NetworkUtils;
import okhttp3.Call;
import okhttp3.Response;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;

import net.duohuo.dhroid.net.OkGo;
import net.duohuo.dhroid.net.callback.AbsCallback;
import net.duohuo.dhroid.net.request.BaseRequest;

public abstract class NormalCallBack<T> extends AbsCallback<T> {

	IDialog dialog;

	Context mContext;

	boolean showDialog = true;

	String loadMsg = "加载中...";

	BaseRequest request;

	Dialog progressDialog;

	public NormalCallBack(Context context) {
		mContext = context;
	}

	public NormalCallBack(Context context, boolean showDialog) {
		mContext = context;
		this.showDialog = showDialog;
	}

	public NormalCallBack(Context context, String msg) {
		mContext = context;
		this.loadMsg = msg;
	}

	@Override
	public void onBefore(BaseRequest request) {
		// TODO Auto-generated method stub
		super.onBefore(request);
		this.request = request;
		if (request.getTag() == null) {
			request.tag(mContext);
		}

		dialog = IocContainer.getShare().get(IDialog.class);
		boolean hasNet = NetworkUtils.isNetworkAvailable();
		if (!hasNet) {
			request.getCall().cancel();
			dialog.showToastShort(mContext, "网络连接异常,请重试!");
			onErray();
		} else {
			if (showDialog) {
				progressDialog = dialog.showProgressDialog(mContext, loadMsg);
			}
		}

	}

	@Override
	public void onAfter(T t, Exception e) {
		// TODO Auto-generated method stub
		super.onAfter(t, e);
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	@Override
	public void onError(Call call, Response response, Exception e) {
		// TODO Auto-generated method stub
		super.onError(call, response, e);
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		if (e instanceof UnknownHostException) {
			dialog.showToastShort(mContext, "域名不对可能是没有配置网络权限");
		} else if (e instanceof java.net.SocketTimeoutException) {
			dialog.showToastShort(mContext, "网络超时");
		} else {
			dialog.showToastShort(mContext, e.toString());
		}
		onErray();
	}

	@Override
	public void onSuccess(T t, Call call, Response response) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	@Override
	public T convertSuccess(Response response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void onErray() {
	}

}
