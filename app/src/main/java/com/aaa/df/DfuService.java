package com.aaa.df;

import android.app.Activity;
import android.content.Intent;
import android.os.IBinder;


import no.nordicsemi.android.dfu.BuildConfig;
import no.nordicsemi.android.dfu.DfuBaseService;

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