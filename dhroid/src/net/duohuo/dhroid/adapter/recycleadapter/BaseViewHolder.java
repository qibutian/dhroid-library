package net.duohuo.dhroid.adapter.recycleadapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.duohuo.dhroid.adapter.FieldMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/4.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {
    Map<Integer, View> views;

    List<FieldMap> fields;

    public BaseViewHolder(View view, List<FieldMap> fields) {
        super(view);
        views = new HashMap<Integer, View>();
        for (Iterator<FieldMap> iterator = fields.iterator(); iterator
                .hasNext();) {
            FieldMap fieldMap = iterator.next();
            views.put(fieldMap.getRefId(),
                    view.findViewById(fieldMap.getRefId()));
        }
    }

    public BaseViewHolder(View view) {
        super(view);
    }

    public void put(Integer id, View v) {
        views.put(id, v);
    }

    public View get(Integer id) {
        return views.get(id);
    }

}