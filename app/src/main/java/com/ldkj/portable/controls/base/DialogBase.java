package com.ldkj.portable.controls.base;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import java.lang.annotation.Annotation;

/**
 * Created by john on 15-3-10.
 */
public class DialogBase extends Dialog{
    public interface OnNumberDialogListener {
        public abstract void SetValueFinish(String p_Number, int p_ResId,Boolean isInit);
    }
    public DialogBase(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
