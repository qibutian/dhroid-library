package net.duohuo.dhroid.net.model;

import java.io.Serializable;
import java.util.List;

import net.duohuo.dhroid.util.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class DResponse<T> implements Serializable {

	private static final long serialVersionUID = 5213230387175987834L;

	public boolean success;
	public String msg;
	public List<T> list;

	public T data;

	public String result;

	public boolean cache;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public boolean isCache() {
		return cache;
	}

	public void setCache(boolean cache) {
		this.cache = cache;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public JSONObject jSONFrom(String prefix) {
		if (result != null) {
			try {
				return JSONUtil.getJSONObject(new JSONObject(result), prefix);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public JSONObject jSON() {
		if (result != null) {
			try {
				return new JSONObject(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

}
