package com.uruhans.code;

import android.content.Context;
import android.content.Intent;

/**
 * Created by uruha on 06-03-2016.
 */
public class LocationInteractionImpl implements ILocationInteractor {

    private Context mContext;
    LocationInteractionImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public void startService() {
        mContext.startService(new Intent(mContext, LocationService.class));
    }

    @Override
    public void stopService() {
        mContext.stopService(new Intent(mContext, LocationService.class));
    }
}
