package project.leo.com.baidumapproject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import project.leo.com.baidumapproject.location.location.LocationSubscriber;
import project.leo.com.baidumapproject.location.location.RxLocation;

/**
 * 演示覆盖物的用法
 */
public class OverlayDemo extends FragmentActivity {

    /**
     * MapView 是地图主控件
     */
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Marker mMarkerA;
    private Marker mMarkerB;
    private Marker mMarkerC;
    private Marker mMarkerD;
    private InfoWindow mInfoWindow;
    private TextView tvPOI;
    private PoiSuggestController suggestController;

    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bdA = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_marka);
    BitmapDescriptor bdB = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_markb);
    BitmapDescriptor bdC = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_markc);
    BitmapDescriptor bdD = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_markd);
    BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_gcoding);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay);
        mMapView = findViewById(R.id.bmapView);
        tvPOI = findViewById(R.id.tv_poi);
        suggestController = new PoiSuggestController(this, "杭州");
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);
        initLocation();
        initOverlay();
        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                Button button = new Button(getApplicationContext());
                button.setBackgroundResource(R.mipmap.popup);
                OnInfoWindowClickListener listener = null;
                if (marker == mMarkerA || marker == mMarkerD) {
                    button.setText("更改位置");
                    button.setTextColor(Color.BLACK);
                    button.setWidth(300);

                    listener = new OnInfoWindowClickListener() {
                        public void onInfoWindowClick() {
                            LatLng ll = marker.getPosition();
                            LatLng llNew = new LatLng(ll.latitude + 0.005,
                                    ll.longitude + 0.005);
                            marker.setPosition(llNew);
                            mBaiduMap.hideInfoWindow();
                        }
                    };
                    LatLng ll = marker.getPosition();
                    mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -47, listener);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                } else if (marker == mMarkerB) {
                    button.setText("更改图标");
                    button.setTextColor(Color.BLACK);
                    button.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            marker.setIcon(bd);
                            mBaiduMap.hideInfoWindow();
                        }
                    });
                    LatLng ll = marker.getPosition();
                    mInfoWindow = new InfoWindow(button, ll, -47);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                } else if (marker == mMarkerC) {
                    button.setText("删除");
                    button.setTextColor(Color.BLACK);
                    button.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            marker.remove();
                            mBaiduMap.hideInfoWindow();
                        }
                    });
                    LatLng ll = marker.getPosition();
                    mInfoWindow = new InfoWindow(button, ll, -47);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                }
                return true;
            }
        });

        suggestController.setItemClickCallBack(new AddressListDialogFragment.ItemClick() {
            @Override
            public void itemResult(PoiInfo data) {
                Toast.makeText(OverlayDemo.this, data.name, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initLocation() {
        mBaiduMap.setMyLocationEnabled(true);

        RxLocation.get().locateLastKnown(this)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LocationSubscriber() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onLocatedSuccess(BDLocation location) {
                        super.onLocatedSuccess(location);
                        mBaiduMap.setMyLocationData(new MyLocationData.Builder()
                                .accuracy(location.getRadius()).latitude(location.getLatitude())
                                .longitude(location.getLongitude()).build());
                        MapStatus.Builder builder = new MapStatus.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(12f);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                    }

                    @Override
                    public void onLocatedFail(BDLocation bdLocation, String msg) {
                        Toast.makeText(OverlayDemo.this, "定位失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void initOverlay() {
        createCovers();

        // add marker overlay
        LatLng llA = new LatLng(39.963175, 116.400244);
        LatLng llB = new LatLng(39.942821, 116.369199);
        LatLng llC = new LatLng(39.939723, 116.425541);
        LatLng llD = new LatLng(39.906965, 116.401394);

        MarkerOptions ooA = new MarkerOptions().position(llA).icon(bdA)
                .zIndex(9).draggable(true);
        // 掉下动画
        ooA.animateType(MarkerAnimateType.drop);
        mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
        MarkerOptions ooB = new MarkerOptions().position(llB).icon(bdB)
                .zIndex(5);
        // 掉下动画
        ooB.animateType(MarkerAnimateType.drop);
        mMarkerB = (Marker) (mBaiduMap.addOverlay(ooB));
        MarkerOptions ooC = new MarkerOptions().position(llC).icon(bdC)
                .perspective(false).anchor(0.5f, 0.5f).rotate(30).zIndex(7);
        // 生长动画
        ooC.animateType(MarkerAnimateType.grow);
        mMarkerC = (Marker) (mBaiduMap.addOverlay(ooC));
        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
        giflist.add(bdA);
        giflist.add(bdB);
        giflist.add(bdC);
        MarkerOptions ooD = new MarkerOptions().position(llD).icons(giflist)
                .zIndex(0).period(10);
        // 生长动画
        ooD.animateType(MarkerAnimateType.grow);
        mMarkerD = (Marker) (mBaiduMap.addOverlay(ooD));

        // add ground overlay
        LatLng southwest = new LatLng(39.92235, 116.380338);
        LatLng northeast = new LatLng(39.947246, 116.414977);
        LatLngBounds bounds = new LatLngBounds.Builder().include(northeast)
                .include(southwest).build();

        MapStatusUpdate u = MapStatusUpdateFactory
                .newLatLng(bounds.getCenter());
        mBaiduMap.setMapStatus(u);

        mBaiduMap.setOnMarkerDragListener(new OnMarkerDragListener() {
            public void onMarkerDrag(Marker marker) {
            }

            public void onMarkerDragEnd(Marker marker) {
                Toast.makeText(
                        OverlayDemo.this,
                        "拖拽结束，新位置：" + marker.getPosition().latitude + ", "
                                + marker.getPosition().longitude,
                        Toast.LENGTH_LONG).show();
            }

            public void onMarkerDragStart(Marker marker) {
            }
        });
    }

    private void createCovers() {
        createView("http://video.wutatoutiao.com/testfile/cover/7c546dd299629604b120b078db77994c.jpg?&r=8463", new LatLng(30.323175, 120.110244));
        createView("http://video.wutatoutiao.com/testfile/cover/abe7502c97093845a2ff4d4e02883086.jpg?&r=6571", new LatLng(30.323715, 120.110204));
        createView("http://video.wutatoutiao.com/testfile/cover/306e6cbadfbf9dcbf3f7ba20bce7343b.jpg?&r=9771", new LatLng(30.320175, 120.180244));
        createView("http://video.wutatoutiao.com/testfile/cover/40a463e3835b3369cd9225c60a56c2be.jpg?&r=4975", new LatLng(30.393100, 120.190244));
    }

    private void createView(String imageStr, final LatLng latLng) {
        final View view = LayoutInflater.from(OverlayDemo.this).inflate(R.layout.view_map_maker, null);
        final ImageView image = view.findViewById(R.id.iv_logo);
        Glide.with(this).asBitmap().apply(new RequestOptions().circleCrop()).load(imageStr).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                image.setImageBitmap(resource);
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
                MarkerOptions ooA = new MarkerOptions().position(latLng).icon(bitmapDescriptor).zIndex(9).draggable(true);
                // 掉下动画
                ooA.animateType(MarkerAnimateType.grow);
                mBaiduMap.addOverlay(ooA);
                return false;
            }
        }).submit();

    }

    @Override
    protected void onPause() {
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mMapView.onDestroy();
        suggestController.destpry();
        super.onDestroy();
        // 回收 bitmap 资源
        bdA.recycle();
        bdB.recycle();
        bdC.recycle();
        bdD.recycle();
        bd.recycle();
    }
}
