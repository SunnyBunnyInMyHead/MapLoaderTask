package com.ye_sk.maploadertask.download;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.ye_sk.maploadertask.regions.Region;

import java.io.File;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ua.kmd.storageutils.StorageUtils;

import static com.ye_sk.maploadertask.Const.LOAD_REGION_FILTER;
import static com.ye_sk.maploadertask.Const.SERVICE_STOPPED;
import static com.ye_sk.maploadertask.Const.WORK_FOLDER;

public enum DownloadController {
    INSTANCE;
    private Context context;

    private final File WORKING_FOLDER = new File(StorageUtils.getInternalSDCard(), WORK_FOLDER);

    private boolean downloadServiceEnable = false;
    private BroadcastReceiver stopDownloadServiceListener;
    private Queue<Region> queue = new ConcurrentLinkedQueue<>();

    public void addToQueue(Region region){
        queue.offer(region);
        if(!downloadServiceEnable){
            startDownloadService();
        }
    }

    public Region getRegion(){
        return queue.poll();
    }

    private void startDownloadService(){
        if(isServiceRunning(DownloadService.class, context)){
            return;
        }
        context.startService(new Intent(context, DownloadService.class));
        downloadServiceEnable = false;
    }

    //check service status in a system
    public static boolean isServiceRunning(Class<?> serviceClass, Context activity) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        List<ActivityManager.RunningServiceInfo> services = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : services) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void initialize(Context context) {
        this.context = context;
        initStopDownloadServiceListener();
    }

    //subscribe for stop service event
    private void initStopDownloadServiceListener(){
        stopDownloadServiceListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean stopped = (Boolean) intent.getBooleanExtra(SERVICE_STOPPED,false);
                if(stopped){
                    downloadServiceEnable = false;
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(LOAD_REGION_FILTER);
        context = context.getApplicationContext();
        context.registerReceiver(stopDownloadServiceListener, intentFilter);

    }

    public File getWORKING_FOLDER() {
        if(!WORKING_FOLDER.exists())
            WORKING_FOLDER.mkdirs();
        return WORKING_FOLDER;
    }

}
