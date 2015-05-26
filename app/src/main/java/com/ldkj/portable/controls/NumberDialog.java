package com.ldkj.portable.controls;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ldkj.portable.R;
import com.ldkj.portable.adapters.Base.AdapterBase;
import com.ldkj.portable.controls.base.DialogBase;

import java.util.ArrayList;

/**
 * Created by john on 15-3-10.
 */
public class NumberDialog extends DialogBase implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final int LISTMAXCOUT = 5;
    private static final String NAME = "number_config";
    private static final String KEY = "key";

    private Context context;
    private int resId;
    private ListView lvTmpFreq;
    private ArrayList<String> dataList = new ArrayList<>();


    private SharedPreferences preferences;
    private int[] viewIds = {R.id.btnBack, R.id.btnNine, R.id.btnEight, R.id.btnSeven,
            R.id.btnGHZ, R.id.btnSix, R.id.btnFive, R.id.btnFour,
            R.id.btnMHZ, R.id.btnThree, R.id.btnTwo, R.id.btnOne,
            R.id.btnKHZ, R.id.btnDot, R.id.btnZero, R.id.btnDoubleZero};

    public NumberDialog(final Context context, int resId) {
        super(context);
        this.context = context;
        this.resId = resId;
        setContentView(R.layout.dialog_number);

        preferences = context.getSharedPreferences(NAME, Context.MODE_APPEND);
        dataList = getDataList();

        lvTmpFreq = (ListView) findViewById(R.id.id_lv_number_tmp_freq);
        if (dataList.size() <= 0) {
            lvTmpFreq.setVisibility(View.GONE);
        }
        lvTmpFreq.setAdapter(new MyAdapter(dataList));
        for (int i = 0; i < viewIds.length; i++) {
            addListener(viewIds[i]);
        }
        lvTmpFreq.setOnItemClickListener(this);
    }

    private void addListener(int pViewId) {
        findViewById(pViewId).setOnClickListener(this);

    }

    private void addList(String pValue) {
        boolean isflag = false;
        for (String s : dataList) {
            if (pValue.equalsIgnoreCase(s)) {
                isflag = true;
            }
        }
        if (!isflag) {
            if (dataList.size() >= LISTMAXCOUT) {
                dataList.remove(0);
                dataList.add(pValue);
            } else {
                dataList.add(pValue);
            }
            setDataList();
        }
    }

    private ArrayList<String> getDataList() {
        String _value = preferences.getString(KEY, null);
        if (_value != null) {
            String[] _Values = _value.split(",");
            int _count = _Values.length;
            for (int i = 0; i < _count; i++) {
                dataList.add(_Values[i]);
            }
        }
        return dataList;
    }

    private void setDataList() {
        int _coud = dataList.size();
        String _value = "";
        for (int i = 0; i < _coud; i++) {
            _value += dataList.get(i) + ",";
        }
        preferences.edit().putString(KEY, _value).commit();
    }

    private void onOk(String pValue, String pUnit) {

        int i = pValue.indexOf(".");
        if (!pValue.equals(".") && pValue.length() != 0 && pValue.indexOf(".") != (pValue.length() - 1)) {
            pValue += pUnit;
            addList(pValue);
        } else {
            pValue = null;
        }
        ((OnNumberDialogListener) context).SetValueFinish(pValue, resId, false);
        dismiss();
    }

    @Override
    public void onClick(View view) {
        int _ID = view.getId();
        EditText _EditText = (EditText) findViewById(R.id.txtDisplay);
        String _Number = _EditText.getText().toString();
        switch (_ID) {
            case R.id.btnDot:
                if (_Number.indexOf(".") == -1 && _Number.length() != 0) {
                    _Number += ".";
                }
                break;
            case R.id.btnOne:
                _Number += "1";
                break;
            case R.id.btnTwo:
                _Number += "2";
                break;
            case R.id.btnThree:
                _Number += "3";
                break;
            case R.id.btnFour:
                _Number += "4";
                break;
            case R.id.btnFive:
                _Number += "5";
                break;
            case R.id.btnSix:
                _Number += "6";
                break;
            case R.id.btnSeven:
                _Number += "7";
                break;
            case R.id.btnEight:
                _Number += "8";
                break;
            case R.id.btnNine:
                _Number += "9";
                break;
            case R.id.btnZero:
                _Number += "0";
                break;
            case R.id.btnDoubleZero:
                _Number += "00";
                break;
            case R.id.btnBack:
                if (_Number.length() != 0) {
                    _Number = _Number.substring(0, _Number.length() - 1);
                }
                break;
            case R.id.btnGHZ:
                onOk(_Number, context.getString(R.string.ButtonTextGHZ));
                break;
            case R.id.btnMHZ:
                onOk(_Number, context.getString(R.string.ButtonTextMHZ));
                break;
            case R.id.btnKHZ:
                onOk(_Number, context.getString(R.string.ButtonTextKHZ));
                break;
            default:
                break;
        }
        _EditText.setText(_Number);
        _EditText.setSelection(_Number.length());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String _Number = dataList.get(dataList.size() - position - 1);
        ((OnNumberDialogListener) context).SetValueFinish(_Number, R.id.center_freq, false);
        dismiss();
    }

    class MyAdapter extends AdapterBase<String> {


        public MyAdapter(ArrayList<String> list) {
            super(list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Hodler hodler = null;
            if (convertView == null) {
                convertView = NumberDialog.this.getLayoutInflater().inflate(R.layout.item_number_tmp_freq, null);
                hodler = new Hodler();
                hodler.tvValue = (TextView) convertView.findViewById(R.id.id_item_tmp_freq);
                convertView.setTag(hodler);
            } else {
                hodler = (Hodler) convertView.getTag();
            }
            String _Number = list.get(list.size() - position - 1);
            int _len = _Number.trim().length();
            _Number = _Number.substring(0, _len - 3) + "\n" + _Number.substring(_len - 3, _len);
            hodler.tvValue.setText(_Number);
            return convertView;
        }

        class Hodler {
            public TextView tvValue;
        }
    }
}
