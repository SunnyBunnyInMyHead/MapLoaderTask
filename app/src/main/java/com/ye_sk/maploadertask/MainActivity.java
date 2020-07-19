package com.ye_sk.maploadertask;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ye_sk.maploadertask.regions.Region;
import com.ye_sk.maploadertask.regions.RegionAdapter;
import com.ye_sk.maploadertask.regions.RegionController;
import com.ye_sk.maploadertask.storageanalyzer.StorageFinder;
import com.ye_sk.maploadertask.storageanalyzer.StorageList;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        RegionController.INSTANCE.loadRegions();
        ListView listView = (ListView) findViewById(R.id.region_list);
        LinkedList<Region> regions = RegionController.INSTANCE.getRegions();
        if (regions != null && regions.getFirst().getSubRegions() != null && regions.getFirst().getSubRegions().size()>0) {
            //select europe
            listView.setAdapter(new RegionAdapter(getBaseContext(), regions.getFirst().getSubRegions()));
        }
        StorageList storageList = (StorageList) findViewById(R.id.storage);
        storageList.initList(new StorageFinder(getBaseContext()).getStorageList());
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.READ_LOGS,
                        Manifest.permission.INTERNET,
                }, 0);
            }
        }
    }

}
