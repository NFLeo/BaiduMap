package project.leo.com.baidumapproject.location.location;

import android.content.Context;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class LocationManager {

    private LocationClient realClient;
    private static volatile LocationManager proxyClient;

    private LocationClientOption locationOption;

    private LocationManager(Context context) {

        realClient = new LocationClient(context);

        LocationClientOption locationOption = returnLocationOption();
        //设置定位参数
        realClient.setLocOption(locationOption);
    }

    public static LocationManager get(Context context) {
        if (proxyClient == null) {
            synchronized (LocationManager.class) {
                if (proxyClient == null) {
                    proxyClient = new LocationManager(context.getApplicationContext());
                }
            }
        }
        return proxyClient;
    }

    /**
     * 开启定位
     * @param locationListener 定位监听
     */
    public void locate(final BDAbstractLocationListener locationListener) {
        LocationListener listener = new LocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                locationListener.onReceiveLocation(bdLocation);
                destoryLocation(this);
            }
        };

        realClient.registerLocationListener(listener);

        if (!realClient.isStarted()) {
            realClient.start();
        }
    }

    /**
     * 初始默认的option
     *
     * @return
     */
    private LocationClientOption returnLocationOption() {
        /*
        * 高精度定位模式：这种定位模式下，会同时使用网络定位和GPS定位，优先返回最高精度的定位结果；
        * 低功耗定位模式：这种定位模式下，不会使用GPS进行定位，只会使用网络定位（WiFi定位和基站定位）；
        * 仅用设备定位模式：这种定位模式下，不需要连接网络，只使用GPS进行定位，这种模式下不支持室内环境的定位。
        * */
        if (locationOption == null) {
            locationOption = new LocationClientOption();
            // 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            // 可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            locationOption.setCoorType("bd09ll");
            locationOption.setOpenGps(true);
            //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
            locationOption.setLocationNotify(true);
            // 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            locationOption.setIgnoreKillProcess(true);
            // 可选，默认false，设置是否收集CRASH信息，默认收集
            locationOption.SetIgnoreCacheException(false);
            // 可选，设置是否需要地址信息，默认不需要
            locationOption.setIsNeedAddress(true);
            // 可选，设置是否需要设备方向结果
            locationOption.setNeedDeviceDirect(false);
            // 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            locationOption.setIsNeedLocationDescribe(true);
            // 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            locationOption.setIsNeedLocationPoiList(true);
        }
        return locationOption;
    }

    public static class LocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) { }
    }

    private void destoryLocation(BDAbstractLocationListener listener)
    {
        if (null != realClient) {
            realClient.unRegisterLocationListener(listener);
            realClient.stop();
//            realClient = null;
//            locationOption = null;
        }
    }

    //获取上次定位地址
    public BDLocation getLateKnownLocation() {
        return realClient.getLastKnownLocation();
    }
}