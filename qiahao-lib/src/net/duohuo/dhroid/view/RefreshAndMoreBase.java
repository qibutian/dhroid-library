package net.duohuo.dhroid.view;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import in.srain.cube.views.ptr.loadmore.LoadMoreContainer;
import in.srain.cube.views.ptr.loadmore.LoadMoreContainerBase;
import in.srain.cube.views.ptr.loadmore.LoadMoreDefaultFooterView;
import in.srain.cube.views.ptr.loadmore.LoadMoreHandler;
import in.srain.cube.views.ptr.loadmore.LoadMoreRecycleViewContainer;
import in.srain.cube.views.ptr.loadmore.LoadMoreUIHandler;
import net.duohuo.dhroid.R;
import net.duohuo.dhroid.adapter.INetAdapter;
import net.duohuo.dhroid.adapter.INetAdapter.LoadSuccessCallBack;
import net.duohuo.dhroid.adapter.NetBeanAdapter;
import net.duohuo.dhroid.net.model.DResponse;
import net.duohuo.dhroid.util.DhUtil;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public abstract class RefreshAndMoreBase<T> extends LinearLayout {

	public Context mContext;

	public PtrFrameLayout mPtrFrame;

	public INetAdapter mAdapter;

	public LoadMoreContainerBase loadMoreContainer;

	public OnLoadSuccess onLoadSuccess;

	public View mheadV, mEmptyV;

	public LinearLayout emptyLayout;

	public LayoutInflater mLayoutInflater;

	public OnEmptyListenser onEmptyListenser;

	public View loadMoreV;

	public RefreshAndMoreBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
	}

	public void setContentView(int layout) {
		mLayoutInflater.inflate(layout, this);
		initView();
		initPtr();
		initLoadMoreContainer();
	}

	public abstract void initView();

	public void setHeadView() {
		final StoreHouseHeader header = new StoreHouseHeader(mContext);
		header.setPadding(0, DhUtil.dip2px(mContext, 15), 0,
				DhUtil.dip2px(mContext, 10));
		header.initWithString("GUOHUAI");
		header.setTextColor(getResources().getColor(R.color.text_hui999999));
		mPtrFrame.disableWhenHorizontalMove(true);
		mPtrFrame.addPtrUIHandler(header);
		mPtrFrame.setHeaderView(header);
		mPtrFrame.setPinContent(false);
	}

	public void initLoadMoreContainer() {

		loadMoreV = new LoadMoreDefaultFooterView(getContext());
		loadMoreV.setVisibility(GONE);
		loadMoreContainer.setLoadMoreView(loadMoreV);
		loadMoreContainer.setLoadMoreUIHandler((LoadMoreUIHandler) loadMoreV);
		loadMoreContainer.setAutoLoadMore(true);

		loadMoreContainer.setLoadMoreHandler(new LoadMoreHandler() {
			@Override
			public void onLoadMore(LoadMoreContainer loadMoreContainer) {
				if (mAdapter != null) {
					mAdapter.showNext();
				}
			}
		});
	}

	public void initPtr() {

		setHeadView();

		mPtrFrame.setPtrHandler(new PtrHandler() {
			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				if (mAdapter != null) {
					mAdapter.refresh();
				}
			}

			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame,
					View content, View header) {
				return PtrDefaultHandler.checkContentCanBePulledDown(frame,
						loadMoreContainer.getChildAt(0), header);
			}
		});
	}

	public void refresh() {
		mPtrFrame.autoRefresh(true);
	}

	public LoadMoreContainerBase getLoadMoreListViewContainer() {
		return loadMoreContainer;
	}

	/**
	 * 获取内容域
	 * 
	 * @return
	 */
	public T getContentView() {
		return (T) loadMoreContainer.getChildAt(0);
	}

	public void setEmptyView(View empty) {
		mEmptyV = empty;
		if (mEmptyV != null) {
			emptyLayout.addView(mEmptyV);
		}
	}

	public void setEmptyViewTop(View empty) {
		mEmptyV = empty;
		if (mEmptyV != null) {
			LayoutParams params = new LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT);
			params.gravity = Gravity.TOP;
			emptyLayout.addView(mEmptyV, params);
		}
	}

	public void setAdapter(INetAdapter adapter) {
		mAdapter = adapter;
		mAdapter.setOnLoadSuccess(new LoadSuccessCallBack() {

			@Override
			public void callBack(DResponse response) {

				if (onLoadSuccess != null) {
					onLoadSuccess.loadSuccess(response);
				}

				if (mAdapter.getPageNo() == 1) {
					if (mEmptyV != null) {
						emptyLayout
								.setVisibility(mAdapter.getValues().size() == 0 ? View.VISIBLE
										: View.GONE);
					}

					if (onEmptyListenser != null) {
						onEmptyListenser
								.onempty(mAdapter.getValues().size() != 0 ? false
										: true);
					}

					loadMoreContainer.setShowLoadingForFirstPage(mAdapter
							.hasMore());
					loadMoreContainer.loadMoreFinish(!mAdapter.hasMore(),
							mAdapter.hasMore());
				} else {
					loadMoreContainer.loadMoreFinish(mAdapter.getValues()
							.size() != 0 ? false : true, mAdapter.hasMore());

				}

				mPtrFrame.refreshComplete();
			}

		});

	}

	public OnLoadSuccess getOnLoadSuccess() {
		return onLoadSuccess;
	}

	public void setOnLoadSuccess(OnLoadSuccess onLoadSuccess) {
		this.onLoadSuccess = onLoadSuccess;
	}

	public interface OnLoadSuccess {
		void loadSuccess(DResponse response);
	}

	public OnEmptyListenser getOnEmptyListenser() {
		return onEmptyListenser;
	}

	public void setOnEmptyListenser(OnEmptyListenser onEmptyListenser) {
		this.onEmptyListenser = onEmptyListenser;
	}

	public interface OnEmptyListenser {
		void onempty(boolean isempty);
	}

	public PtrFrameLayout getmPtrFrame() {
		return mPtrFrame;
	}

	public void setmPtrFrame(PtrFrameLayout mPtrFrame) {
		this.mPtrFrame = mPtrFrame;
	}

}
