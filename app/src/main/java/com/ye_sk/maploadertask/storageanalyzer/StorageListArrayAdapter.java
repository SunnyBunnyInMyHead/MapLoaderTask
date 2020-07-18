package com.ye_sk.maploadertask.storageanalyzer;

/**
 * Created by ав on 21.12.2015
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ye_sk.maploadertask.R;
import com.ye_sk.maploadertask.utils.FileUtils;
import com.ye_sk.maploadertask.utils.RepresentationUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StorageListArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private ArrayList<String> values = new ArrayList<>();
    private ArrayList<Long> packageListSize = new ArrayList<>();
    private ArrayList<Long> freeListSize = new ArrayList<>();
    private ArrayList<LinearLayout> layoutsSelectedSize = new ArrayList<>();
    private ArrayList<LinearLayout> layoutsPackageSize = new ArrayList<>();
    private ArrayList<Integer> selectedListSize = new ArrayList<>();
    private ArrayList<String> pathList = new ArrayList<>();

    public StorageListArrayAdapter(Context context,
                                   List<Storage> storageList) {
        super(context, R.layout.rowlayout);
        this.context = context;
        for (Storage storage : storageList) {
            values.add(storage.getName());
            selectedListSize.add(0);
            String root = storage.getFolderPath();
            pathList.add(root);
            File directory = new File(root);
            packageListSize.add(storage.getTotalAmount());
            freeListSize.add(FileUtils.bytesAvailable(directory));
        }

        addAll(values);

    }

    public void updateFreeListSize() {
        for (int i = 0; i < pathList.size(); i++) {
            File directory = new File(pathList.get(i));
            freeListSize.set(i, FileUtils.bytesAvailable(directory));
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.list_item);
        TextView sizeView = (TextView) rowView.findViewById(R.id.size);
        sizeView.setText(context.getString(R.string.free_size, RepresentationUtils.getStringNodeSize(freeListSize.get(position),context),
                RepresentationUtils.getStringNodeSize(packageListSize.get(position),context)));
        textView.setText(values.get(position));
        setupColorIndicator(position, parent, rowView);
        return rowView;
    }

    private void setupColorIndicator(int position, ViewGroup parent, View rowView) {
        LinearLayout packageSpace = (LinearLayout) rowView.findViewById(R.id.package_space);
        LinearLayout freeSpace = (LinearLayout) rowView.findViewById(R.id.free_space);
        LinearLayout selectedSpace = (LinearLayout) rowView.findViewById(R.id.selected_space);

        parent.getWidth();
        int width = parent.getWidth();
        ViewGroup.LayoutParams params = packageSpace.getLayoutParams();

        int freeSize = Math.round(width * (((float) freeListSize.get(position)) / packageListSize.get(position)));
        if (freeSize < width / 10) {
            freeSize += width / 10;
        }
        int packageSize = width - freeSize;
        params.width = packageSize;
        params = freeSpace.getLayoutParams();
        params.width = freeSize;
        layoutsSelectedSize.add(selectedSpace);
        layoutsPackageSize.add(packageSpace);
        if (selectedListSize.get(position) != 0) {
            addSelectedSpace(pathList.get(position), selectedListSize.get(position));
        }
    }

    // to future task
    public void addSelectedSpace(String filePath, long size) {

        for (int i = 0; i < pathList.size(); i++) {
            if (filePath.contains(pathList.get(i)) || filePath.equals(pathList.get(i))) {
                layoutsSelectedSize.get(i).setVisibility(View.VISIBLE);
                int width = layoutsPackageSize.get(i).getLayoutParams().width;
                long packageSize = packageListSize.get(i) - freeListSize.get(i);
                int selectedSize = 15 * Math.round(width * (((float) size) / packageSize));
                selectedListSize.add(i, selectedSize);
                ViewGroup.LayoutParams params = layoutsSelectedSize.get(i).getLayoutParams();
//                if (selectedSize < width / 10) selectedSize += width / 10;
                params.width = selectedSize;
                params = layoutsPackageSize.get(i).getLayoutParams();
                params.width = width - selectedSize;
//                break;
            }
        }

    }

    // to future task
    public void removeSelectedSpace(String filePath, long size) {
        for (int i = 0; i < pathList.size(); i++) {
            if (filePath.contains(pathList.get(i)) || filePath.equals(pathList.get(i))) {
                layoutsSelectedSize.get(i).setVisibility(View.VISIBLE);
                int width = layoutsPackageSize.get(i).getLayoutParams().width;
                long packageSize = packageListSize.get(i) - freeListSize.get(i);
                int selectedSize = selectedListSize.get(i) - 15 * Math.round(width * (((float) size) / packageSize));
                selectedListSize.add(i, selectedSize);
                ViewGroup.LayoutParams params = layoutsSelectedSize.get(i).getLayoutParams();
//                if (selectedSize < width / 10) selectedSize += width / 10;
                params.width = selectedSize;
                params = layoutsPackageSize.get(i).getLayoutParams();
                params.width = width - selectedSize;
//                break;
            }
        }

    }

}