package com.ldkj.portable.controls;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.ldkj.portable.R;
import com.ldkj.portable.controls.base.DialogBase;

/**
 * Created by john on 15-3-10.
 */
public class NumberDialog extends DialogBase implements View.OnClickListener {

    private Context context;
    private int resId;

    private int[] viewIds = {R.id.btnBack,R.id.btnNine,R.id.btnEight,R.id.btnSeven,
            R.id.btnGHZ,R.id.btnSix,R.id.btnFive,R.id.btnFour,
            R.id.btnMHZ,R.id.btnThree,R.id.btnTwo,R.id.btnOne,
            R.id.btnKHZ,R.id.btnDot,R.id.btnZero,R.id.btnDoubleZero};
    public NumberDialog(Context context, int resId) {
        super(context);
        this.context = context;
        this.resId = resId;
        setContentView(R.layout.dialog_number);
        for(int i = 0; i < viewIds.length; i++){
            addListener(viewIds[i]);
        }
    }

    private void addListener(int pViewId){
        findViewById(pViewId).setOnClickListener(this);
    }


    private void onOk(String pValue, String pUnit) {
        if (!pValue.equals(".") && pValue.length() != 0) {
            pValue += pUnit;
        } else {
            pValue = null;
        }
        ((OnNumberDialogListener) context).SetValueFinish(pValue, resId,false);
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


}
