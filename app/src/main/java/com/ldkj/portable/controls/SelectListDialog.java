package com.ldkj.portable.controls;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ldkj.portable.R;
import com.ldkj.portable.adapters.ListAdapter;
import com.ldkj.portable.controls.base.DialogBase;

/**
 * Created by john on 15-3-10.
 */
public class SelectListDialog extends DialogBase implements AdapterView.OnItemClickListener{

    private Context context;
    private ListView listView;
    private ListAdapter adapter;
    private String[] values;
    private int resId;

    public SelectListDialog(Context context, int resId, int arrayId) {
        super(context);
        this.context = context;
        this.resId = resId;
        setContentView(R.layout.dialog_select_list);
        listView = (ListView) findViewById(R.id.dialog_select_item);
        values = context.getResources().getStringArray(arrayId);
        adapter = new ListAdapter(context, values);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((OnNumberDialogListener) context).SetValueFinish(values[position], resId,false);
        dismiss();
    }
}
