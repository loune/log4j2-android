package net.loune.log4jexampleapp;

import android.app.Application;

import net.loune.log4j2android.AndroidLog4jHelper;

/**
 * Created by loune on 7/05/2016.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            AndroidLog4jHelper.initialise(this.getApplicationContext(), R.raw.log4j_debug);
        } else {
            AndroidLog4jHelper.initialise(this.getApplicationContext(), R.raw.log4j_release);
        }
    }
}
