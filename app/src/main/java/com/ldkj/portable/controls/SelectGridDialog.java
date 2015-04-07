package com.ldkj.portable.controls;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ldkj.portable.R;
import com.ldkj.portable.adapters.GridAdapter;
import com.ldkj.portable.adapters.ListAdapter;
import com.ldkj.portable.controls.base.DialogBase;

/**
 * Created by john on 15-3-12.
 */
public class SelectGridDialog extends DialogBase implements AdapterView.OnItemClickListener{
    private String[] values;
    private GridView gridView;
    private GridAdapter adapter;
    private int arrayId;
    private Context context;
    private int resId;
    public SelectGridDialog(Context context,int resId,int arrayId) {
        super(context);
        this.context = context;
        this.arrayId = arrayId;
        this.resId = resId;
        setContentView(R.layout.dialog_select_grid);
        gridView = (GridView)findViewById(R.id.gridview);
        values = context.getResources().getStringArray(arrayId);
        adapter = new GridAdapter(context, values);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((OnNumberDialogListener) context).SetValueFinish(values[position], resId,false);
        dismiss();
    }
}
