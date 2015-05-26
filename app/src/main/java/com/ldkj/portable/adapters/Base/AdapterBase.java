package com.ldkj.portable.adapters.Base;

import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by john on 15-5-26.
 */
public abstract class AdapterBase<T> extends BaseAdapter {

    protected ArrayList<T> list;
    public AdapterBase(ArrayList<T> list) {
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
