package com.example.login;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.widget.Toast;

/**
 * The toast class is used to avoid repeated creation, which may result in display time too long
 * Originally developed by lioilwin
 * Below is the information of the original source code
 * Title: <Bluetooth>
 * Author: <lioilwin>
 * Date: <11 Jan 2019>
 * Availability: <https://github.com/lioilwin/Bluetooth>
 */
public class APP extends Application {
    private static final Handler sHandler = new Handler();
    private static Toast sToast;

    @SuppressLint("ShowToast")
    @Override
    public void onCreate() {
        super.onCreate();
        sToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    public static void toast(String txt, int duration) {
        sToast.setText(txt);
        sToast.setDuration(duration);
        sToast.show();
    }

    public static void runUi(Runnable runnable) {
        sHandler.post(runnable);
    }
}
