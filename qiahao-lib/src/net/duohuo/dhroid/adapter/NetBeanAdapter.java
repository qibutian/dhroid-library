package net.duohuo.dhroid.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import net.duohuo.dhroid.Const;
import net.duohuo.dhroid.adapter.INetAdapter.LoadSuccessCallBack;
import net.duohuo.dhroid.callback.ListBeanCallBack;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.IocContainer;
import net.duohuo.dhroid.net.OkGo;
import net.duohuo.dhroid.net.cache.CacheMode;
import net.duohuo.dhroid.net.model.DResponse;
import net.duohuo.dhroid.net.request.GetRequest;
import net.duohuo.dhroid.util.BeanUtil;
import net.duohuo.dhroid.util.JSONUtil;
import net.duohuo.dhroid.util.MapUtil;
import okhttp3.Call;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;

/**
 * 
 * 将网络数据封装为bean然后绑定,默认使用缓存机制
 * 
 * @author duohuo-jinghao
 * 
 */
public class NetBeanAdapter<T> extends BeanAdapter<T> implements INetAdapter {
	public List<FieldMap> fields;

	public GetRequest request;

	public int pageNo = 0;

	private int step = Const.netadapter_step_default;

	public String timeline = null;

	public boolean hasMore = true;

	public Integer total = 0;

	IDialog dialoger;

	private List<LoadSuccessCallBack> loadSuccessCallBackList;

	private LoadSuccessCallBack tempLoadSuccessCallBack;

	String fromWhat;

	Dialog progressDialoger;

	public String pageParams = Const.netadapter_page_no;

	public String stepParams = Const.netadapter_step;

	Boolean isLoading = false;

	public boolean showProgressOnLoadFrist = true;

	public void setPageParams(String pageParams) {
		this.pageParams = pageParams;
	}

	public void setStepParams(String stepParams) {
		this.stepParams = stepParams;
	}

	public NetBeanAdapter(String url, Context context, int mResource) {
		super(context, mResource);
		request = OkGo.get(url);
		fields = new ArrayList<FieldMap>();
		dialoger = IocContainer.getShare().get(IDialog.class);
		useCache(CacheMode.FIRST_CACHE_THEN_REQUEST);
	}

	public void fromWhat(String fromWhat) {
		this.fromWhat = fromWhat;
	}

	public NetBeanAdapter<T> addField(String key, Integer refid) {
		FieldMap bigMap = new FieldMapImpl(key, refid);
		fields.add(bigMap);
		return this;
	}

	public NetBeanAdapter<T> addField(String key, Integer refid, String type) {
		FieldMap bigMap = new FieldMapImpl(key, refid, type);
		fields.add(bigMap);
		return this;
	}

	public NetBeanAdapter<T> addField(FieldMap fieldMap) {
		fields.add(fieldMap);
		return this;
	}

	public void cleanParams() {
		request.removeAllParams();
	}

	public GetRequest addparam(String key, Object value) {
		return request.params(key, value + "");
	}

	public GetRequest addparams(String key, List<String> value) {
		return request.addUrlParams(key, value);
	}

	public GetRequest getRequest() {

		return request;

	}

	public void cancel() {
		request.getCall().cancel();
	}

	public void refresh() {
		if (!isLoading) {
			pageNo = 0;
			showNext();
		}
	}

	public void refreshInDialog() {
		if (!isLoading) {
			pageNo = 0;
			progressDialoger = dialoger.showProgressDialog(mContext, "加载中");
			showNextInDialog();
		}
	}

	public void setOnLoadSuccess(LoadSuccessCallBack loadSuccessCallBack) {
		if (this.loadSuccessCallBackList == null) {
			this.loadSuccessCallBackList = new ArrayList<INetAdapter.LoadSuccessCallBack>();
		}
		this.loadSuccessCallBackList.add(loadSuccessCallBack);

	}

	public void setOnTempLoadSuccess(LoadSuccessCallBack loadSuccessCallBack) {
		this.tempLoadSuccessCallBack = loadSuccessCallBack;
	}

	public Boolean hasMore() {
		return this.hasMore;
	}

	private void execuse(boolean showDialog) {

		ListBeanCallBack<T> callback = new ListBeanCallBack<T>(mContext,
				showDialog) {

			@Override
			public void onsuccessUI(DResponse<T> response, Call call) {
				if (progressDialoger != null && progressDialoger.isShowing()) {
					progressDialoger.dismiss();
					progressDialoger = null;
				}

				try {
					JSONObject jo = new JSONObject(response.result);
					total = JSONUtil.getInt(jo, Const.response_total);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (pageNo == total || total == 0) {  
					hasMore = false;
					if (dialoger != null && pageNo > 1) {
						dialoger.showToastShort(mContext,
								Const.netadapter_no_more);
					}
				} else {
					hasMore = true;
				}

				if (pageNo == 1) {
					clear();
				}

				List<T> list = response.getList();

				if (list != null) {
					addAll(list);
				}

				// 列表数据只使用第一页的缓存数据,下面请求都不用缓存
				request.setCacheMode(CacheMode.NO_CACHE);

				isLoading = false;
				if (tempLoadSuccessCallBack != null) {
					tempLoadSuccessCallBack.callBack(response);
					tempLoadSuccessCallBack = null;
				}
				if (loadSuccessCallBackList != null) {
					for (Iterator<LoadSuccessCallBack> iterator = loadSuccessCallBackList
							.iterator(); iterator.hasNext();) {
						LoadSuccessCallBack loadSuccessCallBack = (LoadSuccessCallBack) iterator
								.next();
						loadSuccessCallBack.callBack(response);
					}
				}

			}

			@Override
			public void onFailUI(DResponse<T> response, Call call) {
				// TODO Auto-generated method stub
				super.onFailUI(response, call);
				hasMore = false;
				if (progressDialoger != null && progressDialoger.isShowing()) {
					progressDialoger.dismiss();
					progressDialoger = null;
				}

				isLoading = false;
				if (tempLoadSuccessCallBack != null) {
					tempLoadSuccessCallBack.callBack(response);
					tempLoadSuccessCallBack = null;
				}
				if (loadSuccessCallBackList != null) {
					for (Iterator<LoadSuccessCallBack> iterator = loadSuccessCallBackList
							.iterator(); iterator.hasNext();) {
						LoadSuccessCallBack loadSuccessCallBack = (LoadSuccessCallBack) iterator
								.next();
						loadSuccessCallBack.callBack(response);
					}
				}
			}

			@Override
			public void onCacheSuccess(DResponse<T> dResponse, Call call) {
				// TODO Auto-generated method stub
				super.onCacheSuccess(dResponse, call);
				if (progressDialoger != null && progressDialoger.isShowing()) {
					progressDialoger.dismiss();
					progressDialoger = null;
				}
				dResponse.cache = true;
				List<T> list = dResponse.getList();

				if (list != null) {
					addAll(list);
				}

			}

			@Override
			public void onError(Call call, okhttp3.Response response,
					Exception e) {
				// TODO Auto-generated method stub
				super.onError(call, response, e);
				isLoading = false;
				pageNo--;
				if (progressDialoger != null && progressDialoger.isShowing()) {
					progressDialoger.dismiss();
					progressDialoger = null;
				}

				if (loadSuccessCallBackList != null) {
					for (Iterator<LoadSuccessCallBack> iterator = loadSuccessCallBackList
							.iterator(); iterator.hasNext();) {
						LoadSuccessCallBack loadSuccessCallBack = (LoadSuccessCallBack) iterator
								.next();
						DResponse<T> dResponse = new DResponse<T>();
						dResponse.setSuccess(false);
						loadSuccessCallBack.callBack(dResponse);
					}
				}

			}

		};
		callback.setFormWhat(fromWhat);
		request.execute(callback);

	}

	public void showNext() {
		synchronized (isLoading) {
			if (isLoading)
				return;
			isLoading = true;
		}
		// if (showProgressOnLoadFrist && pageNo == 0) {
		// progressDialoger = dialoger.showProgressDialog(mContext, "加载中");
		// }
		pageNo++;
		request.params(pageParams, pageNo);
		request.params(stepParams, step);
		request.params(Const.netadapter_timeline, timeline);
		execuse(false);

	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public boolean isHasMore() {
		return hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	public void showProgressOnFrist(boolean isShow) {
		showProgressOnLoadFrist = isShow;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public void showNextInDialog() {

		synchronized (isLoading) {
			if (isLoading)
				return;
			isLoading = true;
		}

		pageNo++;
		if (progressDialoger != null && !progressDialoger.isShowing()) {
			progressDialoger = dialoger.showProgressDialog(mContext, "加载中");
		}
		request.params(pageParams, pageNo);
		request.params(stepParams, step);
		request.params(Const.netadapter_timeline, timeline);
		execuse(true);
	}

	@Override
	public void bindView(View itemV, int position, T jo) {
		for (Iterator<FieldMap> iterator = fields.iterator(); iterator
				.hasNext();) {
			FieldMap fieldMap = iterator.next();
			View v = itemV.findViewById(fieldMap.getRefId());
			String value = null;

			if (jo instanceof LinkedHashMap) {
				LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) jo;
				value = MapUtil.getString(map, fieldMap.getKey()) + "";
			} else {
				Log.e("duohuo", "NetBeanAdapter对象传人错误");
				value = BeanUtil.getProperty(jo, fieldMap.getKey()) + "";
			}

			if (fieldMap instanceof FieldMapImpl && fixer != null) {
				Object gloValue = fixer.fix(value, fieldMap.getType());
				bindValue(position, v, gloValue,
						fixer.imageOptions(fieldMap.getType()));
			} else {
				Object ovalue = fieldMap.fix(itemV, position, value, jo);
				bindValue(position, v, ovalue,
						fixer.imageOptions(fieldMap.getType()));
			}
		}
	}

	public void useCache(CacheMode cacheMode) {
		request.cacheMode(cacheMode);
	}

	@Override
	public void removeOnLoadSuccess(LoadSuccessCallBack loadSuccessCallBack) {
		if (loadSuccessCallBackList != null) {
			loadSuccessCallBackList.remove(loadSuccessCallBack);
		}
	}

	@Override
	public String getTag() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> getValues() {
		// TODO Auto-generated method stub
		return mVaules;
	}

}
