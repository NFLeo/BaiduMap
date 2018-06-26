package project.leo.com.baidumapproject.location.location;

import android.content.Context;

import com.baidu.location.BDLocation;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * @desc: 获取上次定位信息
 * @author: Leo
 * @date: 2016/11/16
 */
public class LocationLateKnownOnSubscribe implements ObservableOnSubscribe<BDLocation> {

    private final Context context;

    public LocationLateKnownOnSubscribe(Context context) {
        this.context = context;
    }

    @Override
    public void subscribe(final ObservableEmitter<BDLocation> subscriber) {
        BDLocation lateKnownLocation = LocationManager.get(context).getLateKnownLocation();
        if (lateKnownLocation != null) {
            subscriber.onNext(lateKnownLocation);
            subscriber.onComplete();
        } else {
            LocationManager.LocationListener locationListener = new LocationManager.LocationListener() {

                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    subscriber.onNext(bdLocation);
                    subscriber.onComplete();
                }
            };
            LocationManager.get(context).locate(locationListener);
        }
    }
}