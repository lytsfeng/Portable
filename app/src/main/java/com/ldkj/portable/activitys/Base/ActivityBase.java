package com.ldkj.portable.activitys.Base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.ldkj.portable.R;
import com.ldkj.portable.controls.CustomToast;
import com.ldkj.portable.services.ProtableService;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by john on 15-3-4.
 */
public class ActivityBase extends Activity{


    private ProgressDialog m_ProgressDialog;

    private String oldMsg = "";
    private int oldResId;

    /**
     * 显示提示消息
     * @param pMsg
     */
    protected void ShowMsg(String pMsg){
        if(!oldMsg.equalsIgnoreCase(pMsg)){
            oldMsg = pMsg;
            Toast.makeText(this, pMsg, Toast.LENGTH_SHORT).show();
        }

    }
    protected void ShowMsg(int pResID) {
        if(oldResId != pResID){
            oldResId = pResID;
            Toast.makeText(this, pResID, Toast.LENGTH_SHORT).show();
        }
    }

    protected void ShowCustonToast(String pMsg){
    	CustomToast.showToast(this, pMsg, Toast.LENGTH_SHORT);
    }
    protected void ShowCustomToast(int pResID){
    	CustomToast.showToast(this, pResID, Toast.LENGTH_SHORT);
    }

    protected void OpenActivity(Class<?> pClass) {
        Intent _Intent = new Intent();
        _Intent.setClass(this, pClass);
        startActivity(_Intent);
//        finish();
    }

    protected LayoutInflater GetLayouInflater() {
        LayoutInflater _LayoutInflater = LayoutInflater.from(this);
        return _LayoutInflater;
    }

    public void SetAlertDialogIsClose(DialogInterface pDialog,Boolean pIsClose)
    {
        try {
            Field _Field = pDialog.getClass().getSuperclass().getDeclaredField("mShowing");
            _Field.setAccessible(true);
            _Field.set(pDialog, pIsClose);
        } catch (Exception e) {
        }
    }

    protected AlertDialog ShowAlertDialog(int p_TitelResID,String p_Message,DialogInterface.OnClickListener p_ClickListener)
    {
        String _Title = getResources().getString(p_TitelResID);
        return ShowAlertDialog(_Title, p_Message, p_ClickListener);
    }

    protected AlertDialog ShowAlertDialog(String p_Title,String p_Message,DialogInterface.OnClickListener p_ClickListener)
    {
        return new AlertDialog.Builder(this)
                .setTitle(p_Title)
                .setMessage(p_Message)
                .setPositiveButton(R.string.ButtonTextYes, p_ClickListener)
                .setNegativeButton(R.string.ButtonTextNo, null)
                .show();
    }

    protected void ShowProgressDialog(int p_TitleResID,int p_MessageResID) {
        m_ProgressDialog = new ProgressDialog(this);
        m_ProgressDialog.setTitle(getString(p_TitleResID));
        m_ProgressDialog.setMessage(getString(p_MessageResID));
        m_ProgressDialog.show();
    }


    protected void DismissProgressDialog() {
        if(m_ProgressDialog != null)
        {
            m_ProgressDialog.dismiss();
        }
    }

    //连续按两次退出系统
    private static Boolean isExit = false;
    protected void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            ShowMsg("再按一次退出程序");
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
//            int pid = android.os.Process.myPid(); //获得自己的pid
//            android.os.Process.killProcess(pid);
        	
            stopService(new Intent(this, ProtableService.class));
            finish();
            System.exit(0);
        }
    }



}
