package net.duohuo.dhroid.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.duohuo.dhroid.util.BeanUtil;
import android.content.Context;
import android.util.Log;
import android.view.View;

public class PSAdapter<T> extends BeanAdapter<T> {
	// 字段(值和view的id)
	public List<FieldMap> fields;

	public PSAdapter(Context context, int mResource) {
		super(context, mResource);
		fields = new ArrayList<FieldMap>();
	}

	public PSAdapter<T> addField(String key, Integer refid) {
		FieldMap bigMap = new FieldMapImpl(key, refid);
		fields.add(bigMap);
		return this;
	}

	public  PSAdapter<T> addField(String key, Integer refid, String type) {
		FieldMap bigMap = new FieldMapImpl(key, refid, type);
		fields.add(bigMap);
		return this;
	}

	public  PSAdapter<T> addField(FieldMap fieldMap) {
		fields.add(fieldMap);
		return this;
	}


	@Override
	public void bindView(View itemV, int position, Object jo) {
		boolean newViewHolder = false;
		ViewHolder viewHolder = (ViewHolder) itemV.getTag();
		if (viewHolder == null) {
			newViewHolder = true;
			viewHolder = new ViewHolder();
			itemV.setTag(viewHolder);
		}

		for (Iterator<FieldMap> iterator = fields.iterator(); iterator
				.hasNext();) {
			FieldMap fieldMap = iterator.next();
			View v = null;
			if (newViewHolder) {
				v = itemV.findViewById(fieldMap.getRefId());
				viewHolder.put(fieldMap.getRefId(), v);
			} else {
				v = viewHolder.get(fieldMap.getRefId());
			}
			String value = null;
			if (jo instanceof Map) {
				Map map = (Map) jo;
				value = map.get(fieldMap.getKey()) + "";
			} else {
				Log.e("duohuo", "NetBeanAdapter对象传人错误");
				value = BeanUtil.getProperty(jo, fieldMap.getKey()) + "";
			}
			// Object valueobj = BeanUtil.getProperty(jo, fieldMap.getKey());
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
}
