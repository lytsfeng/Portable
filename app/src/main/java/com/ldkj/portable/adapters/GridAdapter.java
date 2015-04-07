package com.ldkj.portable.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ldkj.portable.R;

/**
 * Created by john on 15-3-12.
 */
public class GridAdapter extends BaseAdapter {

    private String[] arrays;
    private Context context;

    public GridAdapter(Context context,String[] arrays) {
        this.context = context;
        this.arrays = arrays;

    }

    @Override
    public int getCount() {
        return arrays.length;
    }

    @Override
    public Object getItem(int position) {
        return arrays[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Hodler hodler = null;
        if(convertView == null){
            convertView  = LayoutInflater.from(context).inflate(R.layout.grid_item,null);
            hodler = new Hodler();
            hodler.textView = (TextView)convertView.findViewById(R.id.grid_list_tv);
            convertView.setTag(hodler);
        }else {
            hodler = (Hodler)convertView.getTag();
        }
        hodler.textView.setText(arrays[position]);

        return convertView;
    }

    class Hodler{
        public TextView textView;
    }

}
