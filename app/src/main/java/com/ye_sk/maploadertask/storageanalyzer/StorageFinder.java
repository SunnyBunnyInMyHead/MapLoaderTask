package com.ye_sk.maploadertask.storageanalyzer;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.ye_sk.maploadertask.R;
import com.ye_sk.maploadertask.utils.FileUtils;
import com.ye_sk.maploadertask.utils.ResourceFormatUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import ua.kmd.storageutils.StorageUtils;

public class StorageFinder {

    private final String[] ENV_STORAGE_NAME = new String[]{
            "EXTERNAL_STORAGE",
            "SECONDARY_STORAGE",
            "EXTERNAL_SD_STORAGE",
            "EXTERNAL_SDCARD_STORAGE"
    };

    Context context;

    private final String partPathAdp = "/Android/data";
    private final String pathStorages = "/storage";


    public StorageFinder(Context context) {
        this.context = context;
    }

    public ArrayList<Storage> getStorageList() {
        ArrayList<Storage> storages = new ArrayList<>();

        File mainStorage = StorageUtils.getInternalSDCard();
        if (FileUtils.isWritable(mainStorage)) {
            storages.add(new Storage(mainStorage.getAbsolutePath(), mainStorage.getTotalSpace()));
        }
        File standard = Environment.getExternalStorageDirectory();
        if (!mainStorage.equals(standard) && FileUtils.isWritable(standard)) {
            storages.add(new Storage(standard.getAbsolutePath(), standard.getTotalSpace()));
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            fillStorageBeforeKitKat(storages);
        } else {
            fillStorageForAndAfterKitKat(storages);
        }

        applyStorageNames(storages);

        return storages;
    }

    private void applyStorageNames(ArrayList<Storage> storages) {
        int countInternalStorage = 0;
        int countExternalStorage = 0;
        for (Storage storage : storages) {
            if (storage.getFolderPath().contains("emulated")) {
                storage.setName(context.getString(R.string.internal_sd_card, countInternalStorage == 0 ? "" : countInternalStorage));
                countInternalStorage++;
            } else {
                storage.setName(context.getString(R.string.external_sd_card, countExternalStorage == 0 ? "" : countExternalStorage));
                countExternalStorage++;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void fillStorageForAndAfterKitKat(ArrayList<Storage> storages) {
        File[] extDirs = context.getExternalFilesDirs(null);

        for (File fileExtDir : extDirs) {
            if(fileExtDir==null) continue;
            File checkedFile = getFileByExtDirs(fileExtDir);
            if (checkedFile != null) {
                addToStorageListIfNotExist(storages, checkedFile.getAbsolutePath());
            }
        }
        checkByStoragesPath(storages);

    }

    private void checkByStoragesPath(final ArrayList<Storage> storages) {
        File file = new File(pathStorages);
        if(!file.exists())
            return;

        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (FileUtils.isWritable(pathname)) {
                    addToStorageListIfNotExist(storages, pathname.getAbsolutePath());
                } else {
                    File adpPath = new File(pathname, partPathAdp + "/" + context.getPackageName() + "/files");
                    if (adpPath.exists() && FileUtils.isWritable(adpPath)) {
                        addToStorageListIfNotExist(storages, adpPath.getAbsolutePath());
                    }
                }
                return false;
            }
        });
    }

    private File getFileByExtDirs(File fileExtDir) {
        if (fileExtDir.getAbsolutePath().contains(partPathAdp)) {
            File rootStoragePath = new File(fileExtDir.getAbsolutePath().substring(0, fileExtDir.getAbsolutePath().indexOf(partPathAdp) - 1));
            if (rootStoragePath != null && FileUtils.isWritable(rootStoragePath))
                return rootStoragePath;
        }
        boolean bool = FileUtils.isWritable(fileExtDir);
        return  bool ? fileExtDir : null;
    }

    public void fillStorageBeforeKitKat(ArrayList<Storage> storages) {
        ArrayList<String> storagePaths = new ArrayList<>();
        fillStorageListByEnv(storagePaths);

        for (String storagePath : storagePaths) {
            addToStorageListIfNotExist(storages, storagePath);
        }
    }

    /**
     * fill path storage list and exclude duplicate path
     *
     * @param storagePaths
     */
    private void fillStorageListByEnv(ArrayList<String> storagePaths) {
        for (String pathStorageEnv : ENV_STORAGE_NAME) {
            String pathStorage = System.getenv(pathStorageEnv);
            if(pathStorage == null)
                continue;
            if (pathStorage.contains(";")) {
                String[] pathsByEnv = pathStorage.split(";");
                for (String pathByEnv : pathsByEnv) {
                    addIfNotExist(storagePaths, pathByEnv);
                }
            } else {
                addIfNotExist(storagePaths, pathStorage);
            }
        }
    }

    private void addToStorageListIfNotExist(ArrayList<Storage> storages, String storagePath) {
        File storageFileToAdd = new File(storagePath);

        if (!FileUtils.isWritable(storageFileToAdd))
            return;

        for (Storage storage : storages) {
            File storageFile = new File(storage.getFolderPath());
            if (ResourceFormatUtils.compare(storageFileToAdd, storageFile))
                return;
        }
        storages.add(new Storage(storageFileToAdd.getAbsolutePath(), storageFileToAdd.getTotalSpace()));
    }

    private void addIfNotExist(ArrayList<String> storagePaths, String pathByEnv) {
        if (!storagePaths.contains(pathByEnv)) {
            storagePaths.add(pathByEnv);
        }
    }


}
