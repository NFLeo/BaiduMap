package project.leo.com.baidumapproject.location.location;

import com.baidu.location.BDLocation;

import io.reactivex.Observer;

/**
 * Describe : 处理定位回调
 * Created by Leo on 2018/3/31.
 */
public abstract class LocationSubscriber implements Observer<BDLocation> {

    @Override
    public void onNext(BDLocation bdLocation) {
        //成功
        onLocatedSuccess(bdLocation);
    }

    @Override
    public void onError(Throwable e) {
        onLocatedFail(null, e.getMessage().toString());
    }

    @Override
    public void onComplete() {
    }

    public void onLocatedSuccess(BDLocation location) {
    }

    public void onLocatedFail(BDLocation location, String msg) {
    }
}