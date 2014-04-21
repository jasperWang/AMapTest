package cn.boqii.app.ui;

import java.util.ArrayList;

import cn.boqii.app.util.Constants;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

public class LocationActivity extends Activity implements LocationSource,
		AMapLocationListener, OnMarkerClickListener, OnInfoWindowClickListener,
		InfoWindowAdapter {

	private MapView mapView;
	private AMap aMap;
	private ArrayList<Marker> mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_layout);
		mapView = (MapView) findViewById(R.id.mMapView);
		mapView.onCreate(savedInstanceState);
		mList = new ArrayList<Marker>();
	}

	@Override
	protected void onStart() {
		super.onStart();
		init();
	}

	private UiSettings mUiSettings;

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
		System.out.println("init........");
	}

	private void setUpMap() {
		// 自定义系统定位小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
		myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
		myLocationStyle.strokeWidth(5);// 设置画的宽度
		aMap.setMyLocationStyle(myLocationStyle);// 把设置好的定位样式舔脚的地图中
		// 设置定位资源，即设置定位监听 ，设置后定位按钮才可以点击
		aMap.setLocationSource(this);
		aMap.setMapType(AMap.MAP_TYPE_NORMAL); // 设置地图模式：标准或卫星
		mUiSettings = aMap.getUiSettings();// 得到地图控件设置对象
		mUiSettings.setZoomControlsEnabled(true);// 设置缩放空间是否显示，默认显示
		mUiSettings.setMyLocationButtonEnabled(true);// 设置定位控件是否显示，默认显示
		aMap.setMyLocationEnabled(true);// 设置定位控件是否可用，默认可用( 是否可触发定位并显示定位层 )
		mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);// 设置高德logo显示位置，默认左边
		mUiSettings.setCompassEnabled(true);// 显示地图默认的指南针
		mUiSettings.setScaleControlsEnabled(true);
		// 设置比例尺是否启用，默认不启用，范围5m-500km
		// float scale = aMap.getScalePerPixel(); //获取当前比例尺
		// Toast.makeText(LocationActivity.this, "每像素代表" + scale
		// +"米",Toast.LENGTH_LONG).show();

		aMap.setOnMarkerClickListener(this);
		aMap.setOnInfoWindowClickListener(this);
		aMap.setInfoWindowAdapter(this);
		addMarkersToMap();// 往地图上添加marker
	}

	private LatLng latlng = new LatLng(36.061, 103.834);

	/**
	 * 在地图上添加marker
	 */
	private void addMarkersToMap() {
		// 声明一个动画帧集合。
		ArrayList<BitmapDescriptor> gifLst = new ArrayList<BitmapDescriptor>();
		// 添加每一帧图片。
		gifLst.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		gifLst.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
		gifLst.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

		// 设置图标基点，图标经纬坐标，标题，附加文本（内容），帧动画
		// 图标有近大远小效果，用户可移动标记，刷新一次图片资源的周期
		Marker marker1 = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
				.position(Constants.CHENGDU).title("成都市")
				.snippet("成都市:30.679879, 104.064855").icons(gifLst)
				.perspective(true).draggable(true).period(50));
		mList.add(marker1);

		MarkerOptions markerOption = new MarkerOptions();
		markerOption.position(Constants.SHANGHAI);
		markerOption.title("上海市").snippet("上海市：31.238068, 121.501654");
		markerOption.perspective(true);
		markerOption.draggable(true);
		markerOption.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.arrow));
		Marker marker2 = aMap.addMarker(markerOption);
		mList.add(marker2);

		// 默认图标
		Marker marker = aMap.addMarker(new MarkerOptions()
				.position(latlng)
				.title("好好学习")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				.perspective(true).draggable(true));
		marker.setRotateAngle(90);// 设置marker旋转90度
		//marker.showInfoWindow();// 设置默认显示一个infowinfow
		mList.add(marker);
	}

	/**
	 * marker点击时跳动一下
	 */
	public void jumpPoint(final Marker marker) {
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		// 将屏幕位置和地理坐标（经纬度）进行转换的类
		Projection proj = aMap.getProjection();
		// 根据传入的图位置（经纬度）得到一个屏幕位置
		Point startPoint = proj.toScreenLocation(Constants.SHANGHAI);
		// 设置偏移点的坐标想x,y
		startPoint.offset(0, -100);
		// 根据转入的屏幕位置返回一个地图位置（经纬度）
		final LatLng startLatLng = proj.fromScreenLocation(startPoint);
		final long duration = 1500;
		// 定义设置动画效果的实例
		final Interpolator interpolator = new BounceInterpolator();
		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float t = interpolator.getInterpolation((float) elapsed
						/ duration);
				double lng = t * Constants.SHANGHAI.longitude + (1 - t)
						* startLatLng.longitude;
				double lat = t * Constants.SHANGHAI.latitude + (1 - t)
						* startLatLng.latitude;
				marker.setPosition(new LatLng(lat, lng));
				if (t < 1.0) {
					handler.postDelayed(this, 16);
				}
			}
		});
	}

	// -----------------重写avtivity的生命周期方法 ------------------

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	// -------------------------定位资源重写方法----------------------

	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);

			/*
			 * mAMapLocManager.setGpsEnable(false);//
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
			 */

			// 第一个参数是定位provider（定位模式），第二个参数时间最短是2000毫秒，
			// 第三个参数距离间隔单位是米，第四个参数是定位监听者
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 2000, 10, this);
			System.out.println("已经激活。。。");
		}

	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);// 移除位置管理服务
			mAMapLocationManager.destory();// 销毁位置管理服务
		}
		mAMapLocationManager = null;
		System.out.println("停止定位。。。。");
	}

	// --------------------------位置监听重写的方法------------------------

	/**
	 * 此方法已废弃
	 */
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		System.out.println(mListener + "=====" + aLocation);
		if (mListener != null) {
			// 将定位信息显示在地图上
			mListener.onLocationChanged(aLocation);
		}
	}

	// ----------------click---------------------

	@Override
	public void onInfoWindowClick(Marker marker) {
		Toast.makeText(this, "你点击了infoWindow窗口" + marker.getTitle(),
				Toast.LENGTH_SHORT).show();
		Toast.makeText(this,
				"当前地图可视区域内Marker数量:" + aMap.getMapScreenMarkers().size(),
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * 对marker标注点点击响应事件
	 */
	@Override
	public boolean onMarkerClick(Marker marker) {
		if (marker.equals(mList.get(1))) {
			if (aMap != null) {
				jumpPoint(marker);
			}
		}
		 marker.showInfoWindow();
		System.out.println("判断跳动结束.......");
		return false;
	}

	public void onAction(View v) {

	}

	// ---------------------adapter-----------------

	@Override
	public View getInfoContents(Marker arg0) {

		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {

		return null;
	}

}
