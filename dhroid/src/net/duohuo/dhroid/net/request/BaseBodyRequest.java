package net.duohuo.dhroid.net.request;

import net.duohuo.dhroid.net.OkGo;
import net.duohuo.dhroid.net.model.HttpParams;
import net.duohuo.dhroid.net.request.BaseRequest;
import net.duohuo.dhroid.net.request.HasBody;
import net.duohuo.dhroid.net.utils.HttpUtils;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * ================================================ 作
 * 者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy0216 版 本：1.0 创建日期：16/8/9
 * 描 述： 修订历史： ================================================
 */
public abstract class BaseBodyRequest<R extends BaseBodyRequest> extends
		BaseRequest<R> implements HasBody<R> {

	protected MediaType mediaType; // 上传的MIME类型
	protected String string; // 上传的文本内容
	protected String json; // 上传的Json
	protected byte[] bs; // 上传的字节数据

	JSONObject jo;

	protected RequestBody requestBody;

	public BaseBodyRequest(String url) {
		super(url);
		if (OkGo.HTTPTYPE == 2) {
			jo = new JSONObject();
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public R requestBody(RequestBody requestBody) {
		this.requestBody = requestBody;
		return (R) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public R params(String key, File file) {
		params.put(key, file);
		return (R) this;
	}

	@Override
	public R params(String key, String value) {

		// key - value
		if (OkGo.HTTPTYPE == 1) {
			super.params(key, value);

		} else {
			try {
				jo.put(key, value);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.mediaType = HttpParams.MEDIA_TYPE_JSON;

		}
		return (R) this;
	}

	// @Override
	// public R params(String key, String value) {
	//
	//
	//
	//
	// // TODO Auto-generated method stub
	// return (R) this;
	//
	//
	// }

	@SuppressWarnings("unchecked")
	@Override
	public R addFileParams(String key, List<File> files) {
		params.putFileParams(key, files);
		return (R) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public R addFileWrapperParams(String key,
			List<HttpParams.FileWrapper> fileWrappers) {
		params.putFileWrapperParams(key, fileWrappers);
		return (R) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public R params(String key, File file, String fileName) {
		params.put(key, file, fileName);
		return (R) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public R params(String key, File file, String fileName,
			MediaType contentType) {
		params.put(key, file, fileName, contentType);
		return (R) this;
	}

	/** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
	@SuppressWarnings("unchecked")
	@Override
	public R upString(String string) {
		this.string = string;
		this.mediaType = HttpParams.MEDIA_TYPE_PLAIN;
		return (R) this;
	}

	/** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
	@SuppressWarnings("unchecked")
	@Override
	public R upJson(String json) {
		this.json = json;
		this.mediaType = HttpParams.MEDIA_TYPE_JSON;
		return (R) this;
	}

	/** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
	@SuppressWarnings("unchecked")
	@Override
	public R upBytes(byte[] bs) {
		this.bs = bs;
		this.mediaType = HttpParams.MEDIA_TYPE_STREAM;
		return (R) this;
	}

	@Override
	public RequestBody generateRequestBody() {
		if (requestBody != null)
			return requestBody; // 自定义的请求体
		if (string != null && mediaType != null)
			return RequestBody.create(mediaType, string); // post上传字符串数据
		if (json != null && mediaType != null)
			return RequestBody.create(mediaType, json); // post上传json数据
		if (bs != null && mediaType != null)
			return RequestBody.create(mediaType, bs); // post上传字节数组
		if (jo != null && mediaType != null)
			return RequestBody.create(mediaType, jo.toString());

		return HttpUtils.generateMultipartRequestBody(params);
	}
}