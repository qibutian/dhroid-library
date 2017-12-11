package net.duohuo.dhroid.adapter.recycleadapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.duohuo.dhroid.adapter.FieldMap;
import net.duohuo.dhroid.adapter.FieldMapImpl;
import net.duohuo.dhroid.bean.ViewParams;
import net.duohuo.dhroid.util.BeanUtil;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;

public class BeanRecyclerAdapter<T> extends RecyclerBaseAdapter<T> {
	int width;

	double itemwidth;

	LayoutParams params;

	List<ViewParams> viewparamsList;

	int padding;

	public BeanRecyclerAdapter(Context context, int mResource) {
		super(context, mResource);
	}

	public BeanRecyclerAdapter(Context context, int mResource, double column,
			int padding,int  leftpadding) {
		super(context, mResource);
		viewparamsList = new ArrayList<ViewParams>();
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);

		width = wm.getDefaultDisplay().getWidth();

		itemwidth = (width-leftpadding) / column;

		this.padding = padding;

	}

	// 相对于屏幕的比例
	public void setViewHeight(int id, double width, double height) {
		ViewParams params = new ViewParams();
		params.setId(id);
		params.setWidth((itemwidth - padding) * width);
		params.setHeight((itemwidth - padding) * height);
		viewparamsList.add(params);

	}

	@Override
	public void bindView(View itemV, BaseViewHolder holder, int position, T jo) {

		for (Iterator<FieldMap> iterator = fields.iterator(); iterator
				.hasNext();) {
			FieldMap fieldMap = iterator.next();
			View v = itemV.findViewById(fieldMap.getRefId());
			String value = null;

			if (jo instanceof LinkedHashMap) {
				LinkedHashMap map = (LinkedHashMap) jo;
				value = map.get(fieldMap.getKey()) + "";
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

	@Override
	public BaseViewHolder buildNormalHolder(int viewType, ViewGroup parent) {


		View v = mInflater.from(mContext).inflate(mResource, parent, false);

		if (itemwidth != 0) {
			for (Iterator iterator = viewparamsList.iterator(); iterator
					.hasNext();) {
				ViewParams params = (ViewParams) iterator.next();
				View childView = v.findViewById(params.id);
				if (childView != null) {
					childView.getLayoutParams().height = (int) params
							.getHeight();
					childView.getLayoutParams().width = (int) params.getWidth();
				}

			}
		}
		// TODO Auto-generated method stub
		BaseViewHolder holder = new BaseViewHolder(v, fields);

		return holder;
	}

	@Override
	public int getMyItemViewType(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List getValues() {
		// TODO Auto-generated method stub
		return mVaules;
	}

}
