package com.ldkj.portable.activitys;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.dom4j.DocumentException;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CircleOptions;
import com.ldkj.portable.PortableApplication;
import com.ldkj.portable.R;
import com.ldkj.portable.activitys.Base.ActivityFrame;
import com.ldkj.portable.beans.DeviceConfig;
import com.ldkj.portable.beans.SingleBean;
import com.ldkj.portable.controls.Spectrum;
import com.ldkj.portable.controls.base.DialogBase;
import com.ldkj.portable.controls.map.MapManager;
import com.ldkj.portable.services.ProtableService;
import com.ldkj.portable.services.base.Callback;
import com.ldkj.portable.tools.NetUtil;
import com.ldkj.portable.tools.Util;
import com.ldkj.portable.xmlparsers.DeviceConfigParser;
import com.ldkj.portable.xmlparsers.ParamParser;

public class Single extends ActivityFrame implements
		DialogBase.OnNumberDialogListener, Callback {

	private Spectrum spectrum;

	private ProtableService service = null;

	private TextView level;
	private TextView mapLevel;
	private TextView mapFreq;
	private CheckBox drawLineBox;
	private ImageButton soundBtn;
	private TextView soundparamText;
	private Typeface typeface;

	private LinearLayout linearLayoutSpectrum;

	private DeviceConfig config;
	private SingleBean singleBean;
	private ParamParser paramParser;

	private MapView mapView;
	private MapManager mapManager;
	public SharedPreferences preferences;

	private boolean isReadSpac = false;
	private boolean isSpectrumShow = false;

	private MyHandler handler;

	private static final String MAKERKEY = "makerkey";
	private static final String PREFERENCESNAME = "com_ldkj_portable_activitys_single";
	private static final String PREFERENCESCHECKKEY = "Checked";
	private static final String PREFERENCESSOUNDKEY = "sound";
	private static final String PREFERENCESDEVICEKEY = "device";

	private boolean isSound = false;
	private boolean isDeviceSound = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bindService(new Intent(this, ProtableService.class), connection,
				BIND_AUTO_CREATE);
		initValue();
		if (savedInstanceState != null) {
			ArrayList<CircleOptions> parcelableArrayList = savedInstanceState
					.getParcelableArrayList(MAKERKEY);
			mapManager.setCircles(parcelableArrayList);
		}

		mapView.onCreate(savedInstanceState);
		
		
	}

	/**
	 * 初始化数据
	 */
	private void initValue() {
		typeface = Typeface.createFromAsset(getAssets(), "fonts/DS-DIGIT.TTF");
		linearLayoutSpectrum = (LinearLayout) findViewById(R.id.spectrum);
		level = (TextView) findViewById(R.id.single_level);
		level.setTypeface(typeface);
		mapLevel = (TextView) findViewById(R.id.maplevel);
		mapLevel.setTypeface(typeface);
		mapFreq = (TextView) findViewById(R.id.mapfreq);
		spectrum = new Spectrum(this);
		linearLayoutSpectrum.addView(spectrum.getChart());
		handler = new MyHandler(this/* ,_T.getLooper() */);
		paramParser = ParamParser.getInstance(this, Util.PATH_SERIAL);
		try {
			config = DeviceConfigParser.getInstance(Util.PATH_CONFIG)
					.getConfig();
			singleBean = paramParser.getConfig();
		} catch (DocumentException e) {
			e.printStackTrace();
			config = new DeviceConfig();
			singleBean = new SingleBean();
		}
		mapView = (MapView) findViewById(R.id.map);
		mapManager = new MapManager(mapView, this);

		preferences = this.getSharedPreferences(PREFERENCESNAME, MODE_PRIVATE);
		drawLineBox = (CheckBox) findViewById(R.id.drawline);

		drawLineBox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						showLine(isChecked);
						preferences.edit()
								.putBoolean(PREFERENCESCHECKKEY, isChecked)
								.commit();
					}
				});
		drawLineBox.setChecked(preferences.getBoolean(PREFERENCESCHECKKEY,
				false));

		soundBtn = (ImageButton) findViewById(R.id.sound_map);
		soundparamText = (TextView) findViewById(R.id.sound_param);
		isSound = preferences.getBoolean(PREFERENCESSOUNDKEY, false);
		isDeviceSound = preferences.getBoolean(PREFERENCESDEVICEKEY, true);
		setSoundBtnImage("");
		// showLine(drawLineBox.isChecked());
		// singleThreadPool = Executors.newSingleThreadExecutor();
	}

	/**
	 * 设置是否绘制路线
	 * 
	 * @param isShow
	 */
	private void showLine(boolean isShow) {
		mapManager.setIsshowLine(isShow);
	}

	/**
	 * dialog 回调事件
	 * 
	 * @param v
	 */
	public void OnclickShowNumber(View v) {
		int _ResId = v.getId();
		ShowNumberDialog(_ResId);
	}

	public void OnclickShowSelect(View v) {
		int _resId = v.getId();
		switch (_resId) {
		case R.id.demodulation_mode:
			ShowSelectDialog(_resId, R.array.array_demodulation_mode);
			break;
		case R.id.attenuation_control:
			ShowSelectDialog(_resId, R.array.array_attenuation_control);
			break;
		case R.id.filter_bandwidth:
			ShowSelectGridDialog(_resId, R.array.array_filter_bandwidth);
			break;
		case R.id.freq_bandwidth:
			ShowSelectGridDialog(_resId, R.array.array_freq_bandwidth);
			break;
		case R.id.sound_map:
		case R.id.sound_param:
			ShowSelectDialog(_resId, R.array.array_sound);
			break;
		default:
			break;
		}
	}

	/**
	 * 设置解调模式
	 * 
	 * @param pValue
	 */
	private void setDemodulation(String pValue) {
		if (pValue.equalsIgnoreCase(getResources().getStringArray(
				R.array.array_demodulation_mode)[0])) {
			sendCmd("SYSTEM:CHAnnel:AUDio 0_");
		} else {
			sendCmd("SENS:DEM " + pValue);
			sendCmd("SYSTEM:CHAnnel:AUDio 1_");
		}
	}

	/**
	 * 清除地图
	 * 
	 * @param isInit
	 */
	private void clearMap(Boolean isInit) {
		if (!isInit)
			mapManager.clearMap();
	}

	private void setSoundBtnImage(String pValue) {
		String[] _Value = getResources().getStringArray(R.array.array_sound);
		if (!TextUtils.isEmpty(pValue)) {
			if (_Value[0].equalsIgnoreCase(pValue)) {
				isSound = false;
				isDeviceSound = false;
			} else if (_Value[1].equalsIgnoreCase(pValue)) {
				isSound = true;
				startTTS();
				isDeviceSound = false;
			} else if (_Value[2].equalsIgnoreCase(pValue)) {
				isSound = false;
				isDeviceSound = true;
			}
			Editor _edit = preferences.edit();
			_edit.putBoolean(PREFERENCESSOUNDKEY, isSound);
			_edit.putBoolean(PREFERENCESDEVICEKEY, isDeviceSound);
			_edit.commit();
		}
		if (!isSound && !isDeviceSound) {
			soundBtn.setImageResource(R.drawable.sound_off);
			soundparamText.setText(_Value[0]);
		} else {
			soundBtn.setImageResource(R.drawable.sound_on);
			if (isSound) {
				soundparamText.setText(_Value[1]);
			} else {
				soundparamText.setText(_Value[2]);
			}
		}
		if(service != null){
			service.setSound(isDeviceSound);
		}
	}

	@Override
	public void SetValueFinish(String p_Number, int p_ResId, Boolean isInit) {
		if (p_Number == null)
			return;
		View _v = findViewById(p_ResId);
		if (_v instanceof TextView) {
			((TextView) _v).setText(p_Number);
		}

		switch (p_ResId) {
		case R.id.center_freq:
			sendCmd("SENS:FREQ " + Util.getCenterFreq(this, p_Number));
			singleBean.centFreq = p_Number;
			mapFreq.setText(p_Number);
			spectrum.setCenterFreq(Util.getValue(this, p_Number));
			clearMap(isInit);
			break;
		case R.id.freq_bandwidth:
			sendCmd("FREQ:SPAN " + Util.getCenterFreq(this, p_Number));
			singleBean.freqBandWidth = p_Number;
			spectrum.setBandwidth(Util.getValue(this, p_Number));
			clearMap(isInit);
			break;
		case R.id.attenuation_control:
			sendCmd("INPut:ATT " + p_Number.substring(0, p_Number.length() - 2));
			singleBean.attcontrol = p_Number;
			break;
		case R.id.filter_bandwidth:
			sendCmd("SYSTEM:CHANNEL:BANDwidth 0, "
					+ Util.getCenterFreq(this, p_Number));
			singleBean.filterBandwidth = p_Number;
			clearMap(isInit);
			break;
		case R.id.demodulation_mode:
			setDemodulation(p_Number);
			singleBean.demodulationMode = p_Number;
			break;
		case R.id.sound_map:
		case R.id.sound_param:
			setSoundBtnImage(p_Number);
			isInit = true;
			break;
		}

		if (!isInit) {
			try {
				paramParser.saveConfig(singleBean);
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			service = ((ProtableService.LocalBinder) binder).getService();
			service.addCallback(Single.this);
			service.setSound(isDeviceSound);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			service = null;
		}
	};

	private void sendCmd(String pcmd) {
		if (service != null)
			service.SendCMD(pcmd);
	}

	private byte[] readCmd(String pCmd) {
		if (service != null)
			return service.ReadCMD(pCmd);
		else
			return null;
	}

	/**
	 * 开始单频侧脸
	 */
	private void startSingle() {
		sendCmd("FREQ:MODE CW");
		sendCmd("TRAC:FEED:CONT MTRAC,NEV");
		sendCmd("TRAC:FEED:CONT IFPAN,ALW");
		SetValueFinish(singleBean.centFreq, R.id.center_freq, true);
		SetValueFinish(singleBean.filterBandwidth, R.id.filter_bandwidth, true);
		SetValueFinish(singleBean.freqBandWidth, R.id.freq_bandwidth, true);
		SetValueFinish(singleBean.demodulationMode, R.id.demodulation_mode,
				true);
		SetValueFinish(singleBean.attcontrol, R.id.attenuation_control, true);
		sendCmd("TRAC:FEED:CONT IF,ALW");
		sendCmd("SYSTEM:CHAnnel:IF 1_");
		sendCmd("TRAC:UDP:TAG \"" + NetUtil.getLocalIpAddress() + "\",\""
				+ config.UDPPort + "\",AUDIO\n");
		String cmd = "SENSe:FUNCtion:ON  \"VOLT:AC\",\"FREQ:OFFS\",\"FSTR\",\"AM\",\"AM:POS\",\"AM:NEG\",\"FM\",\"FM:POS\",\"FM:NEG\",\"PM\",\"BAND\"";
		sendCmd(cmd);
		PortableApplication.getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				while (isReadSpac) {
					if (!isMap && spectrum != null) {
						if (isSpectrumShow) {
							byte[] data = readCmd(Util.SPECTRUMDATA);
							if (data != null)
								handler.obtainMessage(
										Util.MSG_CMD_TCP_RECEIVE_SPEC, data)
										.sendToTarget();
						}
					}
					byte[] _IQ = readCmd(Util.IQMDATA1);
					if (_IQ != null) {
						handler.obtainMessage(Util.MSG_CMD_TCP_RECEIVE_IQ,
								Util.getLevel(_IQ)).sendToTarget();
					}
				}
			}
		});

	}

	/**
	 * 绘制频谱
	 * 
	 * @param pObj
	 */
	private void drawSpec(final Object pObj) {

		spectrum.updateChart(pObj);
	}

	/**
	 * 设置电平值
	 * 
	 * @param pValue
	 */
	private void setText(String pValue) {
		if (isMap) {
			mapLevel.setText(pValue);
		} else {
			level.setText(pValue);
		}
		if(isSound){
			if (speech != null && !speech.isSpeaking()) {  
				speech.setPitch(0.5f);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规  
				speech.speak(pValue,  
	                    TextToSpeech.QUEUE_FLUSH, null);  
	        }  
		}
		mapManager.setColor(color(Double.parseDouble(pValue)));
	}

	@Override
	public void netStatus(int status) {
		switch (status) {
		case Util.MSG_NET_OK:
			ShowMsg(getString(R.string.device_conn_ok));
			mapManager.AddMarker(R.drawable.runenter);
			if (isReadSpac == false) {
				isReadSpac = true;
				startSingle();
			}
			break;
		case Util.MSG_NET_ERROR:
			isReadSpac = false;
			mapManager.AddMarker(R.drawable.error);
			ShowMsg(getString(R.string.device_conn_err));
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapManager.deactivate(); // 注销定位
		mapView.onDestroy();
		unbindService(connection);
		// IQThread = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		mapManager.initMap();
		if (isSound) {
			startTTS();
		}
		if (isReadSpac == false) {
			isReadSpac = true;
			startSingle();
		}
		if(service != null){
			service.setSound(isDeviceSound);
		}
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		isReadSpac = false;
		mapView.onPause();
		mapManager.deactivate();
		if (service != null) {
			service.setSound(false);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
		outState.putParcelableArrayList(MAKERKEY, mapManager.circles);
	}

	class MyHandler extends Handler {
		private WeakReference<Single> main = null;

		public MyHandler(Single main, Looper looper) {
			super(looper);
			this.main = new WeakReference<Single>(main);
		}

		public MyHandler(Single main) {
			this.main = new WeakReference<Single>(main);
		}

		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case Util.MSG_CMD_TCP_RECEIVE_SPEC:
				main.get().drawSpec(msg.obj);

				break;
			case Util.MSG_CMD_TCP_RECEIVE_IQ:
				mapManager.AddMarker(R.drawable.runenter);
				double _Value = (Double) msg.obj;
				main.get().setText(String.format("%.2f", _Value));
				break;
			}
		}
	}

	public void btnOnClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.broomMap:
			setTTS();
			clearMap(false);
			break;
		case R.id.compassMap:
			mapManager.SetCompass();
			break;
		default:
			break;
		}
	}
	/**
     * 打开tts设置
     */
    protected void setTTS() {
		Intent intent = new Intent();
		intent.setAction("com.android.settings.TTS_SETTINGS");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		
		initSpectrumView();
		
		super.onConfigurationChanged(newConfig);
	}
	
	protected void initSpectrumView() {
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			linearLayoutSpectrum.setVisibility(View.VISIBLE);
			level.setTextSize(60);
			isSpectrumShow = true;
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			linearLayoutSpectrum.setVisibility(View.GONE);
			level.setTextSize(140);
			isSpectrumShow = false;
		}
	}
	
}
