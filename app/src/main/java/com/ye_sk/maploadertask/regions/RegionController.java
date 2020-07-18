package com.ye_sk.maploadertask.regions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.text.TextUtils;

import com.ye_sk.maploadertask.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.ye_sk.maploadertask.Const.FINISH_LOAD_FILE;
import static com.ye_sk.maploadertask.Const.LOAD_REGION_FILTER;
import static com.ye_sk.maploadertask.Const.SERVICE_STOPPED;

public enum RegionController {
    INSTANCE;

    private LinkedList<Region> regions = new LinkedList<>();
    private Map<String, Region> regionCatalog = new HashMap<>();
    private BroadcastReceiver downloadRegionsListener;
    private Context context;

    public void initialize(Context context) {
        this.context = context;
        initDownloadRegionsListener();
    }

    public void loadRegions() {
        StringBuilder tmp;
        Region region = null;
        try {
            XmlPullParser xpp = context.getResources().getXml(R.xml.regions);
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_DOCUMENT:
                        System.out.println("START_DOCUMENT");
                        break;
                    case XmlPullParser.START_TAG:
                        System.out.println("START_TAG: name = " + xpp.getName()
                                + ", depth = " + xpp.getDepth() + ", attrCount = "
                                + xpp.getAttributeCount());
                        tmp = new StringBuilder();
                        for (int i = 0; i < xpp.getAttributeCount(); i++) {
                            tmp.append(xpp.getAttributeName(i)).append(" = ").append(xpp.getAttributeValue(i)).append("& ");
                        }
                        if (!TextUtils.isEmpty(tmp.toString())) {
                            System.out.println("Attributes: " + tmp.toString());
                            if (xpp.getDepth() > 0)
                                region = parseRegion(tmp.toString());
                            if(region!=null) {
                                regionCatalog.put(region.getGuid(), region);
                                getLastElementByDepth(xpp.getDepth() - 2, regions).add(region);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        System.out.println("END_TAG: name = " + xpp.getName());
                        break;
/*                    case XmlPullParser.TEXT:
                        //System.out.println( "text = " + xpp.getText());
                        break;*/

                    default:
                        break;
                }
                xpp.next();
            }
            System.out.println("END_DOCUMENT");

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sortRegions(regions);
        loadUrls(regions);
        checkLoadedFiles(regions);
    }

    private void loadUrls(List<Region> regions){
        for (Region region : regions){
            if(region.getSubRegions().size()>0){
                for (Region subregion:region.getSubRegions()) {
                    subregion.complementLink(region);
                }
                loadUrls(region.getSubRegions());
            }
        }
    }

    private void checkLoadedFiles(List<Region> regions){
        String path;
        for (Region region : regions){
            path = region.getFilePath();
            if(path!=null)
                region.setLoaded(new File(path).exists());
            if(region.getSubRegions().size()>0){
                checkLoadedFiles(region.getSubRegions());
            }
        }
    }

    public Region parseRegion(String attributes) {
        Region region = new Region();
        String[] arr = attributes.split("& ");
        String[] couple;
        for (String atr : arr) {
            couple = atr.split("= ");
            if (couple.length < 2)
                continue;
            region.setMeaning(couple[0].replace(" ", ""), couple[1]);
        }
        region.createMissingData();
        return region;
    }

    private LinkedList<Region> getLastElementByDepth(int depth, LinkedList<Region> list) {
        return depth == 0 ? list : getLastElementByDepth(depth - 1, list.getLast().getSubRegions());
    }

    public LinkedList<Region> getRegions() {
        return regions;
    }

    public void sortRegions(LinkedList<Region> regions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            regions.sort(new Comparator<Region>() {
                @Override
                public int compare(Region r1, Region r2) {
                    return  r1.getTitle().compareTo(r2.getTitle());
                }
            });
        }
        for (Region region: regions) {
            if(region.getSubRegions().size()>0){
                sortRegions(region.getSubRegions());
            }
        }
    }

    private void initDownloadRegionsListener(){
        downloadRegionsListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String guid = (String) intent.getStringExtra(FINISH_LOAD_FILE);
                if(guid!=null)
                    regionCatalog.get(guid).setLoaded(true);
            }
        };
        IntentFilter intentFilter = new IntentFilter(LOAD_REGION_FILTER);
        context = context.getApplicationContext();
        context.registerReceiver(downloadRegionsListener, intentFilter);

    }
}
