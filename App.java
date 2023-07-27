package com.skypan.covid_19;

import android.app.Application;

public class App extends Application {

    private static boolean activityVisible;

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }


}