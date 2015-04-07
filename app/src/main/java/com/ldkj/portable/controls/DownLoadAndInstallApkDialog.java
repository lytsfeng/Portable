package com.ldkj.portable.controls;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dom4j.swing.BranchTreeNode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.sax.StartElementListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ldkj.portable.R;
import com.ldkj.portable.controls.base.DialogBase;
import com.ldkj.portable.tools.Util;




public class DownLoadAndInstallApkDialog extends DialogBase implements android.view.View.OnClickListener{
	
	
	public interface DialogCallback{
		public void doCancel(boolean isCancel);
	}
	
	
	private Context context;
	private ProgressBar progressBar; 
	private Button btnOk;
	private Button btnCancel;
	private TextView title;
	private boolean isExit = false;
	private static final String APK_PATH= "apk/tts.apk";
	private static final String PREFERENCES_DOWNLOAD_NAME  = "com_ldkj_portable_DownLoadAndInstallApkDialog";
	private static final String PREFERENCES_DOWNLOAD_ISINSTALL = "install";
	
	private DownLoadApk downLoadApkTask;
	
	private DialogCallback callback;
	
	
	
	public void setCallback(DialogCallback callback) {
		this.callback = callback;
	}

	private boolean isInstall = false;
	private SharedPreferences preferences;
	private AppReceiver receiver;
	
	public DownLoadAndInstallApkDialog(Context context) {
		super(context);
		this.context  = context;
		setContentView(R.layout.dialog_download_install_apk);
		progressBar = (ProgressBar)findViewById(R.id.download_install_apk_progressbar);
		btnOk = (Button)findViewById(R.id.download_install_apk_ok);
		btnOk.setOnClickListener(this);
		btnCancel = (Button)findViewById(R.id.download_install_apk_cancel);
		btnCancel.setOnClickListener(this);
		title = (TextView)findViewById(R.id.download_install_apk_title);
		
		if(downLoadApkTask == null){
			downLoadApkTask = new DownLoadApk();
		}
		
		File _File = new File(Util.APK_PATH);
		if(_File.exists()){
			_File.delete();
		}
		preferences = context.getSharedPreferences(PREFERENCES_DOWNLOAD_NAME, context.MODE_PRIVATE);
		isInstall = preferences.getBoolean(PREFERENCES_DOWNLOAD_ISINSTALL, isInstall);
		initButton();
	}

	private void initButton() {
		if( isInstall){
			title.setText(R.string.setting_tip);
			btnOk.setText(R.string.setting);
		}else if ( !isInstall) {
			title.setText(R.string.install_tip);
			btnOk.setText(R.string.install);
		}
		preferences.edit().putBoolean(PREFERENCES_DOWNLOAD_ISINSTALL, isInstall).commit();
	}
	
	private void btnClick(View view){
		int _id = view.getId();
		String _ViewText = null;
		if(view instanceof Button){
			_ViewText = ((Button)view).getText().toString();
		}
		switch (_id) {
		case R.id.download_install_apk_ok:
			if(!TextUtils.isEmpty(_ViewText)){
				if (_ViewText.equalsIgnoreCase(context.getResources().getString(R.string.install))) {
					if(downLoadApkTask.getStatus() != AsyncTask.Status.RUNNING){
						downLoadApkTask.execute(Util.APK_PATH);
					}
				}else if (_ViewText.equalsIgnoreCase(context.getResources().getString(R.string.setting))) {
					setTTS();
					isExit = true;
					dismiss();
				}
			}
			break;
		default:
			isExit = true;
			dismiss();
			break;
		}
	}
	@Override
	public void onClick(View v) {
		btnClick(v);
	}
	
	/**
     * 安装APK
     * @param apkFile
     */
    protected void installApk(String apkFile) {
    	startReceive();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(apkFile)),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}
    
   
    private void startReceive(){
    	stopReceive();
    	receiver = new AppReceiver();
    	IntentFilter _filter = new IntentFilter();
    	_filter.addAction("android.intent.action.PACKAGE_ADDED");
    	_filter.addDataScheme("package");
		context.registerReceiver(receiver, _filter);
    }
    private void stopReceive(){
    	if(receiver != null){
    		context.unregisterReceiver(receiver);
    		receiver = null;
    	}
    }

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		if(isExit){
			stopReceive();
			super.dismiss();
		}
		else
			return;
	}

	/**
     * 打开tts设置
     */
    protected void setTTS() {
		Intent intent = new Intent();
		intent.setAction("com.android.settings.TTS_SETTINGS");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	
	
	class DownLoadApk extends AsyncTask<String, Integer, Void>{
		@Override
		protected void onProgressUpdate(Integer... values) {
			progressBar.setProgress(values[0]);
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			installApk(Util.APK_PATH);
			initButton();
			super.onPostExecute(result);
		}
		public boolean copyApkFromAssets(Context context, String fileName, String path) {
		     boolean copyIsFinish = false;
		     try {
		       InputStream is = context.getAssets().open(fileName);
		       int file_length = is.available();
		       File file = new File(path);
		       file.createNewFile();
		       FileOutputStream fos = new FileOutputStream(file);
		       byte[] temp = new byte[1024];
		       int i = 0;
		       int total_length = 0;
		       while ((i = is.read(temp)) > 0) {
		         fos.write(temp, 0, i);
		         total_length += i;
		         int value = (int) ((total_length / (float) file_length) * 100);
		         onProgressUpdate(value);
		         try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		       }
		       fos.close();
		       is.close();
		       copyIsFinish = true;
		     } catch (IOException e) {
		       e.printStackTrace();
		     }
		     return copyIsFinish;
		   }
		@Override
		protected Void doInBackground(String... params) {
			copyApkFromAssets(context, APK_PATH, params[0]);
			return null;
		}
	}
	
    class AppReceiver extends BroadcastReceiver{

    	private static final int PACKAGE_NAME_START_INDEX = 8;
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent == null)
            {
                return;
            }
            
            if(intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED))
            {
                String data = intent.getDataString();
                
                if(data == null || data.length() <= PACKAGE_NAME_START_INDEX)
                {
                    return;
                }
                String packageName = data.substring(PACKAGE_NAME_START_INDEX);
                if(data.indexOf("tts") != -1){
                	isInstall = true;
                	initButton();
                }
            }
        }
    	
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		 if(keyCode == KeyEvent.KEYCODE_BACK){
	            isExit = true;
	            dismiss();
	        }
		
		return super.onKeyDown(keyCode, event);
	}
	
	
    
    
    
}
