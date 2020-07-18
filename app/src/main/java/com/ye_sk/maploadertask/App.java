package com.ye_sk.maploadertask;

import android.app.Application;

import com.ye_sk.maploadertask.download.DownloadController;
import com.ye_sk.maploadertask.regions.RegionController;

public class App extends Application {

    public App() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RegionController.INSTANCE.initialize(this);
        DownloadController.INSTANCE.initialize(this);
    }

}
