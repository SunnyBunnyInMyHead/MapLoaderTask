package com.ye_sk.maploadertask.utils;

import android.os.Build;
import android.os.StatFs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

    public static long bytesAvailable(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
//        long bytesAvailable;
//        StatFs stat = new StatFs(file.getPath());
//
//        if (Build.VERSION.SDK_INT >= 18) {
//            bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
//        } else {
//            bytesAvailable = (long) stat.getBlockSize() * stat.getAvailableBlocks();
//        }
//        return bytesAvailable;
        return file.getFreeSpace();
    }

    public static long totalMemory(File file) {
        StatFs stat = new StatFs(file.getAbsolutePath());
        long bytesTotal;
        if (Build.VERSION.SDK_INT >= 18) {
            bytesTotal = stat.getBlockCountLong() * stat.getBlockSizeLong();
        } else {
            bytesTotal = (long) stat.getBlockCount() * stat.getBlockSize();
        }
        return bytesTotal;
    }

    public static boolean isWritable(File destination) {
        try {
            File file = File.createTempFile("tmp", ".tmp", destination);

            if (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
                return true;
            }
        } catch (IOException e) {
            try {
                File file = new File(destination, "tmp.tmp");
                file.createNewFile();
                if (file.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    file.delete();
                    return true;
                }
            } catch (IOException ex){
                if (destination.getPath().contains("Android/data") && destination.canWrite()) {
                    return true;
                }
            }
        }
        return false;
    }
}
