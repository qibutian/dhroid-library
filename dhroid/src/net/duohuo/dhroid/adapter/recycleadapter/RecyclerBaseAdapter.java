package net.duohuo.dhroid.adapter.recycleadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.duohuo.dhroid.adapter.FieldMap;
import net.duohuo.dhroid.adapter.FieldMapImpl;
import net.duohuo.dhroid.adapter.ValueFix;
import net.duohuo.dhroid.ioc.IocContainer;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerBaseAdapter<T> extends
		RecyclerView.Adapter<BaseViewHolder> {

	public List<FieldMap> fields;

	public List<T> mVaules = null;

	private final Object mLock = new Object();

	protected int mResource;

	protected boolean mNotifyOnChange = true;

	public LayoutInflater mInflater;

	public Context mContext;

	public ValueFix fixer;

	public static int headViewType = 10086;

	private static final int BASE_ITEM_TYPE_HEADER = 100000;
	private static final int BASE_ITEM_TYPE_FOOTER = 200000;

	private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<View>();
	private SparseArrayCompat<View> mFootViews = new SparseArrayCompat<View>();

	OnItemClickListener onItemClickListener;

	OnItemLongClickListener onItemLongClickListener;

	public RecyclerBaseAdapter(Context context, int mResource) {
		super();
		fields = new ArrayList<FieldMap>();
		this.mResource = mResource;
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mVaules = new ArrayList<T>();
		fixer = IocContainer.getShare().get(ValueFix.class);
	}

	/**
	 * 添加Field
	 * 
	 * @param key
	 * @param refid
	 * @return
	 */
	public RecyclerBaseAdapter addField(String key, Integer refid) {
		FieldMap bigMap = new FieldMapImpl(key, refid);
		fields.add(bigMap);
		return this;
	}

	/**
	 * 添加Field
	 * 
	 * @param key
	 * @param refid
	 * @param type
	 * @return
	 */
	public RecyclerBaseAdapter addField(String key, Integer refid, String type) {
		FieldMap bigMap = new FieldMapImpl(key, refid, type);
		fields.add(bigMap);
		return this;
	}

	/**
	 * 添加Field
	 * 
	 * @param fieldMap
	 * @return
	 */
	public RecyclerBaseAdapter addField(FieldMap fieldMap) {
		fields.add(fieldMap);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getValues() {
		return (List<T>) mVaules;
	}

	public void add(T one) {
		synchronized (mLock) {
			mVaules.add(one);
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void addAll(List<T> ones) {
		synchronized (mLock) {
			mVaules.addAll(ones);
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void insert(int index, T one) {
		synchronized (mLock) {
			mVaules.add(index, one);
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void remove(int index) {
		synchronized (mLock) {
			mVaules.remove(index);
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void clear() {
		synchronized (mLock) {
			mVaules.clear();
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void setNotifyOnChange(boolean notifyOnChange) {
		mNotifyOnChange = notifyOnChange;
	}

	public Object getItem(int position) {
		return mVaules.get(position);
	}

	@SuppressWarnings("unchecked")
	public <T> T getTItem(int position) {
		return (T) mVaules.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public String getTItemId(int position) {

		return position + "";
	}

	public abstract void bindView(View itemV, BaseViewHolder holder,
			int position, T jo);

	/**
	 * 将值和控件绑定 可以防止图片的移位
	 * 
	 * @param position
	 * @param v
	 * @param o
	 */
	public void bindValue(final Integer position, View v, Object o,
			DisplayImageOptions options) {
		if (o == null)
			o = "";
		if (v instanceof ImageView) {
			ImageView imagev = (ImageView) v;
			if (o instanceof Drawable) {
				imagev.setImageDrawable((Drawable) o);
			} else if (o instanceof Bitmap) {
				imagev.setImageBitmap((Bitmap) o);
			} else if (o instanceof Integer) {
				imagev.setImageResource((Integer) o);
			} else if (o instanceof String) {
				ImageLoader.getInstance().displayImage((String) o,
						(ImageView) v, options);
			}
		} else if (v instanceof TextView) {
			if (o instanceof CharSequence) {
				((TextView) v).setText((CharSequence) o);
			} else {
				((TextView) v).setText(o.toString());
			}
		}
	}

	public void onBindViewHolder(BaseViewHolder viewHolder,
			final int position) {

		if (isHeaderViewPos(position)) {
			return;
		}
		if (isFooterViewPos(position)) {
			return;
		}
		int realPosition = position - getHeadersCount();
		bindView(viewHolder.itemView, viewHolder, realPosition,
				mVaules.get(realPosition));

		viewHolder.itemView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (onItemClickListener != null) {
					onItemClickListener.onItemClick(v, position);
				}

			}
		});

		viewHolder.itemView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				if (onItemLongClickListener != null) {
					onItemLongClickListener.onItemLogClick(v, position);
				}

				return true;
			}
		});

	}

	@Override
	public int getItemCount() {

		if (mVaules == null) {
			return getHeadersCount() + getFootersCount();
		} else {
			return getHeadersCount() + getFootersCount() + mVaules.size();
		}

	}

	@Override
	public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (mHeaderViews.get(viewType) != null) {

			return new HeadOrFootViewHolder(mHeaderViews.get(viewType));

		} else if (mFootViews.get(viewType) != null) {
			return new HeadOrFootViewHolder(mFootViews.get(viewType));
		}
		return buildNormalHolder(viewType, parent);
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		WrapperUtils.onAttachedToRecyclerView(this, recyclerView,
				new WrapperUtils.SpanSizeCallback() {
					@Override
					public int getSpanSize(GridLayoutManager layoutManager,
							GridLayoutManager.SpanSizeLookup oldLookup,
							int position) {
						int viewType = getItemViewType(position);
						if (mHeaderViews.get(viewType) != null) {
							return layoutManager.getSpanCount();
						} else if (mFootViews.get(viewType) != null) {
							return layoutManager.getSpanCount();
						}
						if (oldLookup != null)
							return oldLookup.getSpanSize(position);
						return 1;
					}
				});
	}

	@Override
	public void onViewAttachedToWindow(BaseViewHolder holder) {
		// TODO Auto-generated method stub
		// onViewAttachedToWindow(holder);
		int position = holder.getLayoutPosition();
		if (isHeaderViewPos(position) || isFooterViewPos(position)) {
			WrapperUtils.setFullSpan(holder);
		}
	}

	public abstract BaseViewHolder buildNormalHolder(int viewType,
			ViewGroup parent);

	public abstract int getMyItemViewType(int position);

	@Override
	public int getItemViewType(int position) {
		if (isHeaderViewPos(position)) {
			return mHeaderViews.keyAt(position);
		} else if (isFooterViewPos(position)) {
			return mFootViews.keyAt(position - getHeadersCount()
					- getRealItemCount());
		}
		return getMyItemViewType(position - getHeadersCount());
	}

	private int getRealItemCount() {
		if (mVaules == null) {
			return 0;
		}
		return mVaules.size();
	}

	private boolean isHeaderViewPos(int position) {
		return position < getHeadersCount();
	}

	private boolean isFooterViewPos(int position) {
		return position >= getHeadersCount() + getRealItemCount();
	}

	public void addHeaderView(View view) {
		mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
	}

	public void addFootView(View view) {
		mFootViews.put(mFootViews.size() + BASE_ITEM_TYPE_FOOTER, view);
	}

	public int getHeadersCount() {
		return mHeaderViews.size();
	}

	public int getFootersCount() {
		return mFootViews.size();
	}

	/**
	 * 
	 * 大家都用的viewholder
	 * 
	 */


	public class HeadOrFootViewHolder extends BaseViewHolder {

		public HeadOrFootViewHolder(View view) {
			super(view);
		}

	}

	public OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public OnItemLongClickListener getOnItemLongClickListener() {
		return onItemLongClickListener;
	}

	public void setOnItemLongClickListener(
			OnItemLongClickListener onItemLongClickListener) {
		this.onItemLongClickListener = onItemLongClickListener;
	}

	public interface OnItemClickListener {
		void onItemClick(View itemV, int position);
	}

	public interface OnItemLongClickListener {
		void onItemLogClick(View itemV, int position);
	}

}
