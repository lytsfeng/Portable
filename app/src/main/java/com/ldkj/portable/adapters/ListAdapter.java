package com.ldkj.portable.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ldkj.portable.R;

/**
 * Created by john on 15-3-10.
 */
public class ListAdapter extends BaseAdapter {

    private String[] values = null;
    private Context context = null;

    public ListAdapter(Context context, String[] pValues){
        this.context = context;
        values = pValues;
    }
    @Override
    public int getCount() {
        return values.length;
    }

    @Override
    public Object getItem(int position) {
        return values[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Hodler hodler = null;
        if(convertView == null){
            convertView  = LayoutInflater.from(context).inflate(R.layout.list_item,null);
            hodler = new Hodler();
            hodler.textView = (TextView)convertView.findViewById(R.id.radio_list_tv);
            convertView.setTag(hodler);
        }else {
            hodler = (Hodler)convertView.getTag();
        }
        hodler.textView.setText(values[position]);

        return convertView;
    }

    class Hodler{
        public TextView textView;
    }
}
