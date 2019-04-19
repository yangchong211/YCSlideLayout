package com.ycbjie.slide;

import android.util.Log;

public class LoggerUtils {

    private static boolean isLog = false;

    public static void setIsLog(boolean isLog) {
        LoggerUtils.isLog = isLog;
    }

    public static void i(String string){
        if (isLog){
            Log.i("LoggerUtils",string);
        }
    }


    public static void d(String string){
        if (isLog){
            Log.d("LoggerUtils",string);
        }
    }
}
