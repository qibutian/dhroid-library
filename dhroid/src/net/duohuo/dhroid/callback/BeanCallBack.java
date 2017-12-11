package net.duohuo.dhroid.callback;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import net.duohuo.dhroid.Const;
import net.duohuo.dhroid.bean.ViewParams;
import net.duohuo.dhroid.net.model.DResponse;
import net.duohuo.dhroid.util.DhUtil;
import net.duohuo.dhroid.util.JSONUtil;
import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public abstract class BeanCallBack<T> extends NormalCallBack<DResponse<T>> {

	public String fromWhat = "";
	DResponse<T> dResponse = new DResponse<T>();

	public BeanCallBack(Context context) {
		super(context);

	}

	public BeanCallBack(Context context, boolean showDialog) {
		super(context, showDialog);
	}

	public BeanCallBack(Context context, String msg) {
		super(context, msg);
	}

	public BeanCallBack<T> setFormWhat(String fromWhat) {
		this.fromWhat = fromWhat;

		return this;
	}

	/**
	 * 数据获取后---异步 解析数据
	 */
	@SuppressWarnings("unchecked")
	@Override
	public DResponse<T> convertSuccess(Response response) throws Exception {

		if (!response.isSuccessful()) {
			Log.d("duohuo_result", request.getUrl() + " " + request.getMethod()
					+ " " + request.getParams() + " " + response.code() + " "
					+ response.body());
			return null;
		}
		// Log.d("duohuo_result", request.getUrl() + " " + request.getMethod()
		// + " " + request.getParams() + " " + response.body().string()
		// + "");

		String result = response.body().string();

		JSONObject jo = new JSONObject(result);

		String str;
		// Gson gson;

		// if (new TypeToken<T>() {
		// }.getType() instanceof Map) {
		//
		// gson = new GsonBuilder().registerTypeAdapter(new TypeToken<Map>() {
		// }.getType(), new JsonDeserializer<Map>() {
		// @Override
		// public Map<String, Object> deserialize(JsonElement json,
		// Type typeOfT, JsonDeserializationContext context)
		// throws JsonParseException {
		//
		// Map<String, Object> treeMap = new HashMap();
		// JsonObject jsonObject = json.getAsJsonObject();
		// Set<Map.Entry<String, JsonElement>> entrySet = jsonObject
		// .entrySet();
		// for (Map.Entry<String, JsonElement> entry : entrySet) {
		// treeMap.put(entry.getKey(), entry.getValue());
		// }
		// return treeMap;
		// }
		// }).create();
		//
		// } else {
		// gson = new Gson();
		// }

		boolean isSuccess = JSONUtil.getString(jo, Const.response_success)
				.equals(Const.response_success_result);

		if (isSuccess) {
			if (!TextUtils.isEmpty(fromWhat)) {
				str = JSONUtil.getString(jo, fromWhat);
			} else {
				str = result;
			}
		} else {
			str = result;
		}

		// T data = gson.fromJson(str, new TypeToken<T>() {
		// }.getType());

		// T data = (T) JSON.parseObject(str, ((ParameterizedType)
		// getClass().getGenericSuperclass()).getActualTypeArguments()[ 0 ]);
		ObjectMapper mapper = DhUtil.getDefaultObjectMapper();

		JavaType type = mapper.getTypeFactory().constructType(
				((ParameterizedType) getClass().getGenericSuperclass())
						.getActualTypeArguments()[0]);

		T data = (T) mapper.readValue(str, type);

		dResponse.result = result;
		dResponse.data = data;
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
