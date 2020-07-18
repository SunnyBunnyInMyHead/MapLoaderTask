package com.ye_sk.maploadertask;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.ye_sk.maploadertask.regions.RegionAdapter;
import com.ye_sk.maploadertask.regions.RegionController;
import com.ye_sk.maploadertask.storageanalyzer.StorageFinder;
import com.ye_sk.maploadertask.storageanalyzer.StorageList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RegionController.INSTANCE.loadRegions();
        ListView listView = (ListView) findViewById(R.id.region_list);
        listView.setAdapter(new RegionAdapter(getBaseContext(),RegionController.INSTANCE.getRegions().getFirst().getSubRegions()));
        StorageList storageList = (StorageList) findViewById(R.id.storage);
        storageList.initList(new StorageFinder(getBaseContext()).getStorageList());
    }



}
