package com.uruhans.code;

import android.content.Context;

/**
 * Created by uruha on 06-03-2016.
 */
public interface ILauncher {
    void launchGoogle(Context context, String searchString);
    void launchDial(Context context, String number);
    void launchGoogleMaps(Context context, String coordinates, String name);
}
