package com.ldkj.portable.activitys.Base;

import java.util.Locale;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amap.api.maps.MapsInitializer;
import com.ldkj.portable.R;
import com.ldkj.portable.beans.ColorBean;
import com.ldkj.portable.controls.AboutDialog;
import com.ldkj.portable.controls.DownLoadAndInstallApkDialog;
import com.ldkj.portable.controls.NumberDialog;
import com.ldkj.portable.controls.SelectGridDialog;
import com.ldkj.portable.controls.SelectListDialog;
import com.ldkj.portable.controls.DownLoadAndInstallApkDialog.DialogCallback;
import com.ldkj.portable.activitys.OfflineMapActivity;
import com.ldkj.portable.tools.Util;

/**
 * Created by john on 15-3-12.
 */
public abstract class ActivityFrame extends ActivityBase implements
		OnInitListener {

	private static final String MAPKEY = "mapkey";

	private LinearLayout linearLayoutMap;
	private LinearLayout linearLayoutSingle;
	private LinearLayout linearLayoutMapParam;
	private ImageView colorBar;
	private Bitmap bitmap;

	public static final double COLORMAX = 120.0;
	public static final double COLORMIN = -40.0;

	protected boolean isMap = true;
	protected TextToSpeech speech;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// //隐藏标题
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// //设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_single);
		// showMenu();
		if (savedInstanceState != null) {
			isMap = (Boolean) savedInstanceState.get(MAPKEY);
		}
		MapsInitializer.sdcardDir = Util.getSdCacheDir(this);
		init();
		AddListener();
		setMap(isMap);

		// 检查TTS数据是否已经安装并且可用
//
//		speech = new TextToSpeech(this, this);
	}

	private void init() {
		linearLayoutMap = (LinearLayout) findViewById(R.id.linearLayout_map);
		linearLayoutSingle = (LinearLayout) findViewById(R.id.linearLayout_single);
		linearLayoutMapParam = (LinearLayout) findViewById(R.id.linearLayout_map_param);
		colorBar = (ImageView) findViewById(R.id.colorbar);
		bitmap = ((BitmapDrawable) colorBar.getDrawable()).getBitmap();

	}

	private void AddListener() {
		linearLayoutMapParam.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setMap(false);
			}
		});
	}

	public void setMap(boolean isMap) {
		this.isMap = isMap;
		if (!isMap) {
			linearLayoutMap.setVisibility(View.GONE);
			linearLayoutSingle.setVisibility(View.VISIBLE);
			initSpectrumView();
		} else {
			linearLayoutMap.setVisibility(View.VISIBLE);
			linearLayoutSingle.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(MAPKEY, isMap);
	}

	/**
	 * 显示数字键盘dialog
	 * 
	 * @param pResId
	 *            触发该方法的控件id
	 */
	protected void ShowNumberDialog(int pResId) {
		NumberDialog _Dialog = new NumberDialog(this, pResId);
		_Dialog.show();
	}

	/**
	 * 打开list选择对话框
	 * 
	 * @param pResId
	 *            触发该方法的控件id
	 * @param pResArrayId
	 *            list中用到的数组id
	 */
	protected void ShowSelectDialog(int pResId, int pResArrayId) {
		SelectListDialog _ItemDialog = new SelectListDialog(this, pResId,
				pResArrayId);
		_ItemDialog.show();
	}

	protected void ShowSelectGridDialog(int pResId, int pResArrayId) {
		SelectGridDialog _SelectGridDialog = new SelectGridDialog(this, pResId,
				pResArrayId);
		_SelectGridDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
		case R.id.menu_map:
			setMap(true);
			break;
		case R.id.menu_single:
			setMap(false);
			break;
		case R.id.menu_offlineMap:
			OpenActivity(OfflineMapActivity.class);
			break;
		case R.id.menu_about:
			AboutDialog _Dialog = new AboutDialog(this);
			_Dialog.show();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!isMap) {
				setMap(true);
				return true;
			} else {
				exitBy2Click();
			}
		}
		return false;
	}

	private ColorBean _Bean = new ColorBean();

	protected ColorBean color(double pLeve) {

		if (pLeve > 120) {
			pLeve = 120;
			_Bean.red = 255;
			_Bean.green = 0;
			_Bean.blue = 0;
			return _Bean;
		}
		if (pLeve < -40) {
			pLeve = -40;
			_Bean.red = 0;
			_Bean.blue = 255;
			_Bean.green = 0;
			return _Bean;
		}
		pLeve += 40;
		double left = colorBar.getLeft();
		double top = colorBar.getTop();
		double right = colorBar.getRight();
		double bottom = colorBar.getBottom();
		int _x = (int) ((right - left) / 2);
		int _y = (int) (bottom + top - (bottom - top) / (COLORMAX - COLORMIN)
				* pLeve);
		int _pixel = bitmap.getPixel(_x, _y);
		_Bean.red = Color.red(_pixel);
		_Bean.blue = Color.blue(_pixel);
		_Bean.green = Color.green(_pixel);
		return _Bean;
	}

	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		if (status == TextToSpeech.SUCCESS) {  
            int result = speech.setLanguage(Locale.CHINA);  
            if (result == TextToSpeech.LANG_MISSING_DATA  
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {  
            	DownLoadAndInstallApkDialog dialog = new DownLoadAndInstallApkDialog(this);
            	dialog.setCallback(new DialogCallback() {
					
					@Override
					public void doCancel(boolean isCancel) {
						if(isCancel){
							if(speech != null){
								speech.shutdown();
								speech = null;
								ShowCustomToast(R.string.error);
							}
						}
					}
				});
            	dialog.show();
            }  
        } 
	}
	protected void startTTS(){
		if(speech == null){
			speech = new TextToSpeech(this,this);
		}
	}
	protected abstract void initSpectrumView();

}
