package project.leo.com.baidumapproject.location.location;

import android.content.Context;

import com.baidu.location.BDLocation;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Describe : 立即开启定位
 * Created by Leo on 2018/3/31.
 */
public class LocationOnSubscribe implements ObservableOnSubscribe<BDLocation> {

    private final Context context;

    public LocationOnSubscribe(Context context) {
        this.context = context;
    }

    @Override
    public void subscribe(final ObservableEmitter<BDLocation> subscriber) {
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