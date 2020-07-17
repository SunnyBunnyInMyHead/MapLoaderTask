package com.ye_sk.maploadertask;

import java.util.LinkedList;

public class Region {
    private String name;
    private String innerDownloadPrefix;
    private Boolean srtm;
    private String lang;
    private String polyExtract;
    private Boolean joinMapFiles;
    private String type;
    private String translate;
    private boolean boundary;
    private String downloadSuffix;
    private String downloadPrefix;
    private String innerDownloadSuffix;
    private String entity;
    private String title;
    private LinkedList<Region> subRegions = new LinkedList<>();

    public void setMeaning(String title, Object meaning){
        switch (title){
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

        }
    }

    private void setEntityAndTitle(String translate){
        String[] arr = translate.split(";");
        if(arr.length==0)
            return;
        if(arr.length==2){
            title = arr[0].replace("name:en=","");
            entity = arr[1].replace("entity=","");
        }else if (translate.contains("entity=")){
            entity = translate.replace("entity=","");
        }else {
            title = translate.replace("=","");
        }

    }


    public LinkedList<Region> getSubRegions() {
        return subRegions;
    }
}
