package com.uruhans.code;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by uruha on 06-03-2016.
 */
public class Launcher implements ILauncher {

    @Override
    public void launchGoogle(Context context, String searchString) {
        Uri uri = Uri.parse("http://www.google.com/#q=" + searchString);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    @Override
    public void launchDial(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        context.startActivity(intent);
    }

    @Override
    public void launchGoogleMaps(Context context, String coordinates, String name) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + coordinates + " (" + name + ")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }
    }
}
