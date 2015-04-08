package com.ldkj.portable.controls.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.ldkj.portable.R;
import com.ldkj.portable.activitys.Single;
import com.ldkj.portable.beans.ColorBean;
import com.ldkj.portable.tools.Util;

import java.util.ArrayList;

/**
 * Created by john on 15-3-16.
 */
public class MapManager implements LocationSource, AMapLocationListener {

	private AMap aMap;
	private Single single;
	private LatLng latLng = null;
	private Marker CurrentPosition = null;
	public ArrayList<CircleOptions> circles = new ArrayList<CircleOptions>();
	private ArrayList<Circle> circleArrayList = new ArrayList<Circle>();

	private final static int CHECK_POSITION_INTERVAL = 6 * 1000; // 重新获取位置信息的时间间隔
	private final static int CHECK_POSITION_DISTANCE = 20; // 重新获取位置信息的距离间隔
	private final static String LOCATION_CONFIG_NAME = "com_ldkj_portable_location";
	private final static String LOCATION_LATITUDE  = "latitude";
	private final static String LOCATION_LONGITUDE = "longitude";

	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private SharedPreferences preferences;

	private boolean isshowLine = false;
	public boolean isChage = true;
	private ColorBean colorBean;

	public MapManager(MapView mapView, Single single) {
		this.aMap = mapView.getMap();
		this.single = single;

	}

	public void setIsshowLine(boolean isshowLine) {
		this.isshowLine = isshowLine;
		if (isshowLine) {
			initLine();
		} else {
			removeLine();
		}

	}

	public void setCircles(ArrayList<CircleOptions> circles) {
		this.circles = circles;
		initLine();
	}

	private void initLine() {
		if (isshowLine) {
			int _Size = circles.size();
			for (int i = 0; i < _Size; i++) {
				circleArrayList.add(aMap.addCircle(circles.get(i)));
			}
		}
	}

	private void removeLine() {

		int size = circleArrayList.size();
		for (int i = 0; i < size; i++) {
			circleArrayList.get(i).remove();
		}
		circleArrayList.clear();
	}

	public void initMap() {
		setUpMap();
		SetCompass();
	}

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.getUiSettings().setCompassEnabled(true);
		aMap.getUiSettings().setScaleControlsEnabled(true);
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.getUiSettings().setLogoPosition(
				AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
		aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
		aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
	}

	// 定位成功回调函数
	@Override
	public void onLocationChanged(AMapLocation aMapLocation) {
		if (mListener != null && aMapLocation != null) {
			SetMapMarker(aMapLocation);
		}
	}

	private static final double DIS = 20; // 两点之间的最小距离

	private void SetMapMarker(AMapLocation location) {
		LatLng _latLng ;
		if (location == null) {
			_latLng = getLastLocation();
		}else {
			_latLng = new LatLng(location.getLatitude(),
					location.getLongitude());
		}
		if (latLng != null) {

			if (AMapUtils.calculateLineDistance(latLng, _latLng) < DIS) {
				return;
			}
		}
		isChage = true;
		latLng = _latLng;
		saveLocation();
		AddMarker(R.drawable.error);
		SetCompass();
		show();
	}

	private int oldDrawableId;

	public void AddMarker(int pDrawableId) {

		if (oldDrawableId == pDrawableId) {
			return;
		}
		oldDrawableId = pDrawableId;
		if (CurrentPosition != null) {
			CurrentPosition.remove();
		}
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(latLng);
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(pDrawableId);
		markerOptions.icon(bitmap);
		CurrentPosition = aMap.addMarker(markerOptions);
	}

	private void show() {
		if (isChage && isshowLine) {
			SetCompass();
			int _Color = Color.argb(255, colorBean.red, colorBean.green,
					colorBean.blue);
			CircleOptions circle = new CircleOptions().center(latLng).radius(5)
					.strokeWidth(1).strokeColor(_Color).fillColor(_Color);
			circles.add(circle);
			circleArrayList.add(aMap.addCircle(circle));
			isChage = false;
		}
	}

	/**
	 * @param pColor
	 */
	public void setColor(ColorBean pColor) {
		this.colorBean = pColor;
		show();
	}

	public void SetCompass() {
		getLastLocation();
		aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
	}

	@Override
	public void activate(OnLocationChangedListener onLocationChangedListener) {
		mListener = onLocationChangedListener;
		latLng = null;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(single);
			mAMapLocationManager.requestLocationUpdates(
			/* LocationProviderProxy.AMapNetwork */
					LocationManagerProxy.GPS_PROVIDER, CHECK_POSITION_INTERVAL,
					CHECK_POSITION_DISTANCE, this);
		}
	}

	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}

	public void clearMap() {
		removeLine();
		circles.clear();
		isChage = true;
	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}



	private void openPreferences(){
		if(preferences == null){
			preferences = single.getSharedPreferences(LOCATION_CONFIG_NAME, Context.MODE_APPEND);
		}
	}

	private LatLng getLastLocation(){
		openPreferences();
		String _Lat = preferences.getString(LOCATION_LATITUDE,"30.67").trim();
		String _lon = preferences.getString(LOCATION_LONGITUDE,"104.06").trim();
		LatLng _latLng = new LatLng(Double.parseDouble(_Lat),Double.parseDouble(_lon));
		latLng = _latLng;
		return _latLng;
	}

	private void saveLocation(){
		if(latLng == null){
			return;
		}
		openPreferences();
		SharedPreferences.Editor _edit = preferences.edit();
		_edit.putString(LOCATION_LATITUDE,latLng.latitude+"");
		_edit.putString(LOCATION_CONFIG_NAME,latLng.longitude+"");
		_edit.commit();
	}


}
