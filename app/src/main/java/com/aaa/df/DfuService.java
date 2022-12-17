package com.aaa.df;

import android.app.Activity;
import android.content.Intent;
import android.os.IBinder;

import com.aaa.df.dfu.DfuBaseService;


public class DfuService extends DfuBaseService {

    @Override
    protected Class<? extends Activity> getNotificationTarget() {
        return MainActivity.class;
    }

    @Override
    protected boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}