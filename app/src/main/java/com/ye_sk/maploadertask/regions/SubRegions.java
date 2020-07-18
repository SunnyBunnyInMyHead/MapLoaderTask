package com.ye_sk.maploadertask.regions;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ye_sk.maploadertask.R;

import java.util.LinkedList;

public class SubRegions extends Activity {
    private LinkedList<Region> regions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subregions);
        regions =  new Gson().fromJson(getIntent().getStringExtra("regions"), new TypeToken<LinkedList<Region>>() {}.getType());
        ListView listView = (ListView) findViewById(R.id.region_list);
        listView.setAdapter(new RegionAdapter(getBaseContext(),regions));

    }
}
