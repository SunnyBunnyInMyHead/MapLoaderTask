package com.ye_sk.maploadertask.storageanalyzer;

import java.io.File;

/**
 * Created by xenonraite on 23.02.16.
 */
public class Storage {
    private String name;
    private String folderPath;
    private long totalAmount;

    public Storage(){

    }

    public Storage(String folderPath, long totalSpace){
        this.folderPath = folderPath;
        this.totalAmount = totalSpace;
    }

    public Storage(String folderPath){
        File file = new File(folderPath);
        this.folderPath = file.getAbsolutePath();
        this.totalAmount = file.getTotalSpace();
        this.name = file.getAbsolutePath();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }
}
