package com.example.gasbill;

import android.app.Application;

public class MyApplication extends Application {
    private static boolean appInForeground = false;

    public static boolean isAppInForeground() {
        return appInForeground;
    }

    public static void setAppInForeground(boolean isInForeground) {
        appInForeground = isInForeground;
    }
}
