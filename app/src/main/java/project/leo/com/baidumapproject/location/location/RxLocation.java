package project.leo.com.baidumapproject.location.location;

import android.content.Context;

import com.baidu.location.BDLocation;

import io.reactivex.Observable;

/**
 * Describe : RxLocation
 * Created by Leo on 2018/3/31.
 */
public class RxLocation {

    private static RxLocation instance = new RxLocation();

    private RxLocation() {
    }

    public static RxLocation get() {
        return instance;
    }

    public Observable<BDLocation> locate(Context context) {
        return Observable.create(new LocationOnSubscribe(context));
    }

    public Observable<BDLocation> locateLastKnown(Context context) {
        return Observable.create(new LocationLateKnownOnSubscribe(context));
    }
}