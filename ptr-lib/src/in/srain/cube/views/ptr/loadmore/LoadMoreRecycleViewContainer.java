package in.srain.cube.views.ptr.loadmore;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;

public class LoadMoreRecycleViewContainer extends LinearLayout implements
		LoadMoreContainer {

	private LoadMoreUIHandler mLoadMoreUIHandler;
	private LoadMoreHandler mLoadMoreHandler;

	private boolean mIsLoading;
	private boolean mHasMore = false;
	private boolean mAutoLoadMore = true;
	private boolean mLoadError = false;

	private boolean mListEmpty = true;
	private boolean mShowLoadingForFirstPage = false;
	private View mFooterView;

	private RecyclerView recycleView;

	private LAYOUT_MANAGER_TYPE layoutManagerType;

	private int[] lastPositions;

	/**
	 * ���һ���ɼ��item��λ��
	 */
	private int lastVisibleItemPosition;

	public enum LAYOUT_MANAGER_TYPE {
		LINEAR, GRID, STAGGERED_GRID
	}

	public LoadMoreRecycleViewContainer(Context context) {
		super(context);
		System.out.println("22222222");
	}

	public LoadMoreRecycleViewContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		System.out.println("1111111");
	}

	public void setFootView(View footView) {
		mFooterView = footView;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		recycleView = (RecyclerView) getChildAt(0);

	}

	/**
	 * @deprecated It's totally wrong. Use {@link #useDefaultFooter} instead.
	 */

	public void init() {

		// if (mFooterView != null) {
		// addFooterView(mFooterView);
		// }

		final LayoutManager layoutManager = recycleView.getLayoutManager();
		if (layoutManagerType == null) {
			if (layoutManager instanceof LinearLayoutManager) {
				layoutManagerType = LAYOUT_MANAGER_TYPE.LINEAR;
			} else if (layoutManager instanceof StaggeredGridLayoutManager) {
				layoutManagerType = LAYOUT_MANAGER_TYPE.STAGGERED_GRID;
			} else if (layoutManager instanceof GridLayoutManager)
				layoutManagerType = LAYOUT_MANAGER_TYPE.GRID;
			else {
				throw new RuntimeException(
						"Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
			}
		}
		recycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {

			private boolean mIsEnd = false;

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				// TODO Auto-generated method stub
				super.onScrolled(recyclerView, dx, dy);
				switch (layoutManagerType) {
				case LINEAR:
					lastVisibleItemPosition = ((LinearLayoutManager) layoutManager)
							.findLastVisibleItemPosition();
					break;
				case GRID:
					lastVisibleItemPosition = ((GridLayoutManager) layoutManager)
							.findLastVisibleItemPosition();
					break;
				case STAGGERED_GRID:
					StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
					if (lastPositions == null) {
						lastPositions = new int[staggeredGridLayoutManager
								.getSpanCount()];
					}
					staggeredGridLayoutManager
							.findLastVisibleItemPositions(lastPositions);
					lastVisibleItemPosition = findMax(lastPositions);
					break;
				}
			}

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView,
					int newState) {
				// TODO Auto-generated method stub
				super.onScrollStateChanged(recyclerView, newState);
				RecyclerView.LayoutManager layoutManager = recycleView
						.getLayoutManager();
				int visibleItemCount = layoutManager.getChildCount();
				int totalItemCount = layoutManager.getItemCount();
				// LogUtils.i("onScrollStateChanged", "visibleItemCount"
				// + visibleItemCount);
				// LogUtils.i("onScrollStateChanged", "lastVisibleItemPosition"
				// + lastVisibleItemPosition);
				// LogUtils.i("onScrollStateChanged", "totalItemCount"
				// + totalItemCount);
				if (visibleItemCount > 0
						&& newState == RecyclerView.SCROLL_STATE_IDLE
						&& lastVisibleItemPosition == totalItemCount - 1) {
					onReachBottom();
					mIsEnd = true;
				} else {
					mIsEnd = false;
				}
			}

		});
	}

	private void tryToPerformLoadMore() {
		if (mIsLoading) {
			return;
		}

		// no more content and also not load for first page
		if (!mHasMore && !(mListEmpty && mShowLoadingForFirstPage)) {
			return;
		}

		mIsLoading = true;

		if (mLoadMoreUIHandler != null) {
			mLoadMoreUIHandler.onLoading(this);
		}
		if (null != mLoadMoreHandler) {
			mLoadMoreHandler.onLoadMore(this);
		}
	}

	private void onReachBottom() {
		// if has error, just leave what it should be
		if (mLoadError) {
			return;
		}
		if (mAutoLoadMore) {
			tryToPerformLoadMore();
		} else {
			if (mHasMore) {
				mLoadMoreUIHandler.onWaitToLoadMore(this);
			}
		}
	}

	@Override
	public void setShowLoadingForFirstPage(boolean showLoading) {
		mShowLoadingForFirstPage = showLoading;
	}

	@Override
	public void setAutoLoadMore(boolean autoLoadMore) {
		mAutoLoadMore = autoLoadMore;
	}

	@Override
	public void setLoadMoreView(View view) {
		// has not been initialized
		if (recycleView == null) {
			mFooterView = view;
			return;
		}
		// remove previous
		// if (mFooterView != null && mFooterView != view) {
		// removeFooterView(view);
		// }

		// add current
		mFooterView = view;
		mFooterView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				tryToPerformLoadMore();
			}
		});

		// addFooterView(view);
	}

	private int findMax(int[] lastPositions) {
		int max = lastPositions[0];
		for (int value : lastPositions) {
			if (value > max) {
				max = value;
			}
		}
		return max;
	}

	@Override
	public void setLoadMoreUIHandler(LoadMoreUIHandler handler) {
		mLoadMoreUIHandler = handler;
	}

	@Override
	public void setLoadMoreHandler(LoadMoreHandler handler) {
		mLoadMoreHandler = handler;
	}

	/**
	 * page has loaded
	 * 
	 * @param emptyResult
	 * @param hasMore
	 */
	@Override
	public void loadMoreFinish(boolean emptyResult, boolean hasMore) {
		mLoadError = false;
		mListEmpty = emptyResult;
		mIsLoading = false;
		mHasMore = hasMore;

		if (mLoadMoreUIHandler != null) {
			mLoadMoreUIHandler.onLoadFinish(this, emptyResult, hasMore);
		}
	}

	@Override
	public void loadMoreError(int errorCode, String errorMessage) {
		mIsLoading = false;
		mLoadError = true;
		if (mLoadMoreUIHandler != null) {
			mLoadMoreUIHandler.onLoadError(this, errorCode, errorMessage);
		}
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {

	}

}