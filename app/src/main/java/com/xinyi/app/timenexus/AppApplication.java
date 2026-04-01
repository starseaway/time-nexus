package com.xinyi.app.timenexus;

import android.app.Application;
import android.util.Log;

import com.xinyi.timenexus.DateTimeNexus;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("DateTimeNexus", "getWeekDayName = " + DateTimeNexus.getWeekDayName());
        Log.d("DateTimeNexus", "getWeekDayShortName = " + DateTimeNexus.getWeekDayShortName());
    }
}
