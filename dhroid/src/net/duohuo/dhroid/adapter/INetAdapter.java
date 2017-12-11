package net.duohuo.dhroid.adapter;

import java.util.List;

import net.duohuo.dhroid.net.model.DResponse;
import net.duohuo.dhroid.net.model.Response;

/**
 * 网络adapter接口
 * 
 * @author Administrator
 * 
 */
public interface INetAdapter {

	public String getTag();

	public void refresh();

	public void setOnLoadSuccess(LoadSuccessCallBack loadSuccessCallBack);

	public void removeOnLoadSuccess(LoadSuccessCallBack loadSuccessCallBack);

	public void setOnTempLoadSuccess(LoadSuccessCallBack loadSuccessCallBack);

	public Boolean hasMore();

	public void showNext();

	// public boolean isLoding();

	public void showNextInDialog();

	public int getPageNo();

	public List getValues();

	public interface LoadSuccessCallBack {
		public void callBack(DResponse response);
	}
}
