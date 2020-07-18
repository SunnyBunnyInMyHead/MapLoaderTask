package com.ye_sk.maploadertask.regions;

import com.ye_sk.maploadertask.download.DownloadController;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Region {
    private final String prefixURL = "http://download.osmand.net/download.php?standard=yes&file=", sufixUrl = "_2.obf.zip";

    private String name;
    private String innerDownloadPrefix;
    private Boolean srtm;
    private String lang;
    private String polyExtract;
    private Boolean joinMapFiles;
    private String type;
    private String translate;
    private Boolean boundary;
    private Boolean map;
    private Boolean wiki;
    private Boolean roads;
    private Boolean hillshade;
    private String downloadSuffix;
    private String downloadPrefix;
    private String innerDownloadSuffix;
    private String entity;
    private String title;
    private String url;
    private String filePath;
    private Boolean loaded;
    private final String guid;
    private LinkedList<Region> subRegions = new LinkedList<>();
    private List<OnLoadStatusChangeListener> listeners = new ArrayList<>();

    public Region() {
        guid = UUID.randomUUID().toString();
    }

    public void setMeaning(String title, Object meaning) {
        switch (title) {
            case "name":
                name = (String) meaning;
                break;
            case "srtm":
                srtm = (meaning).equals("yes");
                break;
            case "lang":
                lang = (String) meaning;
                break;
            case "poly_extract":
                polyExtract = (String) meaning;
                break;
            case "join_map_files":
                joinMapFiles = (meaning).equals("yes");
                break;
            case "type":
                type = (String) meaning;
                break;
            case "translate":
                translate = (String) meaning;
                setEntityAndTitle(translate);
                break;
            case "boundary":
                boundary = (meaning).equals("yes");
                break;
            case "inner_download_prefix":
                innerDownloadPrefix = (String) meaning;
                break;
            case "download_suffix":
                downloadSuffix = (String) meaning;
                break;
            case "download_prefix":
                downloadPrefix = (String) meaning;
                break;
            case "inner_download_suffix":
                innerDownloadSuffix = (String) meaning;
                break;
            case "map":
                map = (meaning).equals("yes");
                break;
            case "wiki":
                wiki = (meaning).equals("yes");
                break;
            case "roads":
                roads = (meaning).equals("yes");
                break;
            case "hillshade":
                hillshade = (meaning).equals("yes");
                break;
        }
    }

    private void setEntityAndTitle(String translate) {
        String[] arr = translate.split(";");
        if (arr.length == 0)
            return;
        if (arr.length == 2) {
            title = (arr[0].replace("name=", "")).replace("name:en=", "");
            entity = arr[1].replace("entity=", "");
        } else if (translate.contains("entity=")) {
            entity = translate.replace("entity=", "");
        } else {

            title = (translate.replace("name=", "")).replace("=", "");
        }

    }

    public LinkedList<Region> getSubRegions() {
        return subRegions;
    }

    public String getTitle() {
        return title;
    }

    public void createMissingData() {
        if (title == null)
            title = Character.toUpperCase(name.charAt(0)) + name.substring(1);

        if(innerDownloadPrefix!=null&&innerDownloadPrefix.equals("$name")){
            innerDownloadPrefix = name;
        }

        Boolean mapTemp, wikiTemp, roadsTemp, srtmTemp = false, hillshadeTemp = false;

        mapTemp = true;
        srtmTemp = true;
        hillshadeTemp = true;

        if (type != null) {
            mapTemp = false;
            srtmTemp = false;
            hillshadeTemp = false;
            wikiTemp = false;
            roadsTemp = false;
        }

        wikiTemp = map != null && wiki == null ? map : mapTemp;
        roadsTemp = map != null && roads == null ? map : mapTemp;


        if (map == null) {
            map = mapTemp;
        }
        if (wiki == null) {
            wiki = wikiTemp;
        }
        if (roads == null) {
            roads = roadsTemp;
        }
        if (srtm == null) {
            srtm = srtmTemp;
        }
        if (hillshade == null) {
            hillshade = hillshadeTemp;
        }
    }

    public void complementLink(Region parent) {
        if (downloadSuffix == null) {
            downloadSuffix = parent.getInnerDownloadSuffix() != null ? parent.getInnerDownloadSuffix() : parent.downloadSuffix;
        }

        if (downloadPrefix == null) {
            downloadPrefix = parent.getInnerDownloadPrefix() != null ? parent.getInnerDownloadPrefix() : parent.downloadPrefix;
        }

        if (downloadSuffix != null && downloadPrefix != null) {
            url = prefixURL + firstSymbolToUpperCase(downloadPrefix + "_" + name + "_" + downloadSuffix )+ sufixUrl;
            return;
        } else if (downloadSuffix == null && downloadPrefix != null) {
            url = prefixURL + firstSymbolToUpperCase(downloadPrefix + "_" + name) + sufixUrl;
        } else if (downloadSuffix != null) {
            url = prefixURL + firstSymbolToUpperCase(name + "_" + downloadSuffix)+ sufixUrl;
        }
    }

    public String getUrl() {
        return url;
    }

    public String getFilePath() {
        //todo normal folder hierarchy
        //return filePath;
        return url==null?null:DownloadController.INSTANCE.getWORKING_FOLDER().getPath()+ File.separator +url.replace(prefixURL,"");
    }

    public String getInnerDownloadPrefix() {
        return innerDownloadPrefix;
    }

    public String getDownloadSuffix() {
        return downloadSuffix;
    }

    public String getDownloadPrefix() {
        return downloadPrefix;
    }

    public String getInnerDownloadSuffix() {
        return innerDownloadSuffix;
    }

    public String getGuid() {
        return guid;
    }

    public boolean isLoaded() {
        return loaded != null ? loaded : false;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
        for (OnLoadStatusChangeListener onLoadStatusChangeListener : listeners) {
            onLoadStatusChangeListener.onStatusChanged(loaded);
        }
    }

    public void addListener(OnLoadStatusChangeListener listener) {
        listeners.add(listener);
    }

    public void deleteListener(OnLoadStatusChangeListener listener) {
        listeners.remove(listener);
    }

    public Boolean getMap() {
        return map;
    }

    private String firstSymbolToUpperCase(String string){
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }
}
