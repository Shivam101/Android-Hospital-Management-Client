package com.example.shivam.openmrs;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Shivam on 13/05/15 at 12:17 PM.
 */
public class OpenMRSApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "QzCDH87MvFwPnpsSQnt25uu09tjwa9QuCtmmwL1a", "80Btpyns4XVgKr72IznodYct12HNeZU0cBvLCU7D");
    }
}
