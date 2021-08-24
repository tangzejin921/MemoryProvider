package com.tzj.providers;

import android.app.Application;
import android.util.Log;

public class App extends Application {
    public static final String TAG = "MemoryKV";
    public static final Boolean DEBUG = true;

    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG) {
            Log.d(TAG, "onCreate: ");
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (DEBUG) {
            Log.d(TAG, "onTrimMemory: ");
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (DEBUG) {
            Log.d(TAG, "onLowMemory: ");
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (DEBUG) {
            Log.d(TAG, "onTerminate: ");
        }
    }
}
