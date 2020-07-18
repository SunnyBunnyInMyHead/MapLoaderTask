package com.ye_sk.maploadertask.download;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.ye_sk.maploadertask.BuildConfig;
import com.ye_sk.maploadertask.Const;
import com.ye_sk.maploadertask.MainActivity;
import com.ye_sk.maploadertask.R;
import com.ye_sk.maploadertask.regions.Region;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class DownloadService extends Service implements Const {

    public void onCreate() {
        super.onCreate();
        //makeServiceNotification();
        startLoading();
    }

    private void startLoading(){
        Region region = null;
        do {
            region = DownloadController.INSTANCE.getRegion();
            if(region==null){
                stopSelf();
                break;
            }
            downloadFile(region.getUrl(),region);
            sendFinishLoadRegionMessage(region);
        }while (true);

    }

    private void downloadFile(String url, Region region){
        File file = new File(region.getFilePath());
        try {
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        } catch (IOException e) {
            return;
        }

        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(region.getTitle());
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        final Uri uriFile = /*(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) ?
                FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file) :*/
                Uri.fromFile(file);

        request.setDestinationUri(uriFile);
        downloadmanager.enqueue(request);
    }

    private static void downloadUsingStream(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }

    protected void makeServiceNotification(String mapTitle) {
        makeServiceNotification(mapTitle,
                getString(R.string.app_name),
                null,
                NOTIFICATION, getString(R.string.app_name));
    }

    protected void makeServiceNotification(String ticker, String title, String text, int id, String tag) {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
        intent.putExtra("tag", tag);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_world_globe_dark)
                .setLargeIcon(BitmapFactory.decodeResource(this.getApplicationContext().getResources(), R.drawable.ic_action_import))
                .setTicker(ticker)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(text)
                .setOngoing(true)
                .setProgress(100, 0, false);

        Notification notification = builder.getNotification();

        startForeground(id, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendFinishLoadRegionMessage(Region region) {
        Intent intent = new Intent(LOAD_REGION_FILTER);
        intent.putExtra(FINISH_LOAD_FILE, region.getGuid());
        sendBroadcast(intent);
    }

    private void sendStopServiceMessage() {
        Intent intent = new Intent(LOAD_REGION_FILTER);
        intent.putExtra(SERVICE_STOPPED, true);
        sendBroadcast(intent);
    }

    public void onDestroy() {
        sendStopServiceMessage();
        super.onDestroy();
    }
}
