package com.ye_sk.maploadertask.utils;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ResourceFormatUtils {
    public static boolean compare(File folder1, File folder2) {

        if (folder1.getAbsolutePath().startsWith(folder2.getAbsolutePath()) || folder2.getAbsolutePath().startsWith(folder1.getAbsolutePath()))
            return true;

        Log.d("Storage", "compare " + folder1.getAbsolutePath() + ":" + folder2.getAbsolutePath());
        if (folder1.getAbsolutePath().equals(folder2.getAbsolutePath())) return true;
        try {
            if (folder1.getCanonicalPath().equals(folder2.getCanonicalPath())) return true;
        } catch (IOException e) {
            return false;
        }
        if (folder1.getTotalSpace() != folder2.getTotalSpace()) return false;
        if (folder1.getFreeSpace() != folder2.getFreeSpace()) return false;
        return checkFoldersInsideLists(folder1, folder2);
    }

    private static boolean checkFoldersInsideLists(File folder1, File folder2) {
        File[] list1 = folder1.listFiles(), list2 = folder2.listFiles();
        if (list1 == null || list2 == null) return false;
         if (list1.length != list2.length) return false;

        Map<String, File> map = new HashMap<>();
        for (File file : list1) {
            map.put(file.getName(), file);
        }
        for (File file2 : list2) {
            File file1 = map.get(file2.getName());
            if (file1 == null) return false;
            if (file1.isHidden() != file2.isHidden()) return false;
            if (file1.isFile()) {
                if (file2.isFile()) {
                    if (file1.length() != file2.length()) return false;
                } else {
                    return false;
                }
            } else if (file1.isDirectory()) {
                return file2.isDirectory() && compare(file1, file2);
            } else if (file2.isFile() || file2.isDirectory()) {
                return false;
            }
        }
        return true;
    }
}
