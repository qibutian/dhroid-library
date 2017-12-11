package net.duohuo.dhroid.callback;

import net.duohuo.dhroid.Const;
import net.duohuo.dhroid.net.model.DResponse;
import net.duohuo.dhroid.net.model.JSONObjectN;
import net.duohuo.dhroid.util.DhUtil;
import net.duohuo.dhroid.util.JSONUtil;
import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public abstract class ListBeanCallBack<T> extends NormalCallBack<DResponse<T>> {

	public String fromWhat = "";
	DResponse<T> dResponse = new DResponse<T>();

	public ListBeanCallBack(Context context) {
		super(context);

	}

	public ListBeanCallBack(Context context, boolean showDialog) {
		super(context, showDialog);
	}

	public ListBeanCallBack(Context context, String msg) {
		super(context, msg);
	}

	public ListBeanCallBack<T> setFormWhat(String fromWhat) {
		this.fromWhat = fromWhat;
		return this;
	}

	/**
	 * 数据获取后---异步 解析数据
	 */
	@Override
	public DResponse<T> convertSuccess(Response response) throws Exception {

		if (!response.isSuccessful()) {
			Log.d("duohuo_result", request.getUrl() + " " + request.getMethod()
					+ " " + request.getParams() + " " + response.code() + " "
					+ response.body());
			return null;
		}

		dResponse.cache = false;
		// Log.d("duohuo_result", request.getUrl() + " " + request.getMethod()
		// + " " + request.getParams() + " " + response.body().string()
		// + "");

		String result = response.body().string();

		JSONObject jo = new JSONObject(result);
		// Gson gson = new Gson();

		boolean isSuccess = JSONUtil.getString(jo, Const.response_success)
				.equals(Const.response_success_result);

		String str;

		if (isSuccess) {
			if (TextUtils.isEmpty(fromWhat)) {
				str = result;
			} else {
				str = JSONUtil.getString(jo, fromWhat);
			}
		} else {
			str = result;
		}

		// List<T> list = gson.fromJson(str, new TypeToken<List<T>>() {
		// }.getType());

		ObjectMapper mapper = DhUtil.getDefaultObjectMapper();

		if (isSuccess) {
			@SuppressWarnings("unchecked")
			List<T> list = (List<T>) mapper.readValue(str,
					new TypeReference<List<T>>() {
					});
			dResponse.list = list;
		} else {

			dResponse.list = new ArrayList<>();
		}

		dResponse.result = result;

		dResponse.msg = JSONUtil.getString(jo, Const.response_msg);
		dResponse.success = isSuccess;
		response.close();
		return dResponse;
	}

	@Override
	public void onSuccess(DResponse<T> dResponse, Call call, Response response) {
		super.onSuccess(dResponse, call, response);
		if (!dResponse.isSuccess()) {

			JSONObject data = null;
			try {
				data = new JSONObject(dResponse.result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			dialog.showToastShort(mContext,
					JSONUtil.getString(data, Const.response_msg));

			onFailUI(dResponse, call);
			return;
		}
		// TODO Auto-generated method stub
		onsuccessUI(dResponse, call);
	}

	public abstract void onsuccessUI(DResponse<T> response, Call call);

	public void onFailUI(DResponse<T> response, Call call) {
	}

}
