package com.ycbjie.slide;

import android.util.Log;

/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211/YCShopDetailLayout
 *     time  : 2018/6/6
 *     desc  : 日志
 *     revise:
 * </pre>
 */
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
