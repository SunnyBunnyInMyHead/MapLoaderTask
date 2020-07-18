package com.ye_sk.maploadertask.storageanalyzer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class StorageList extends ListView {
    private Context context;

    public StorageList(Context context) {
        super(context);
        this.context = context;
    }

    public StorageList(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void initList(List<Storage> storage) {
        StorageListArrayAdapter storageAdapter = new StorageListArrayAdapter(context, storage);
        setAdapter(storageAdapter);

        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                view.setSelected(true);
            }
        });
        setHeight();
    }

    public void setHeight() {
        ListView listView = this;
        ListAdapter storageAdapter = listView.getAdapter();
        int count = storageAdapter.getCount();
        if (count > 1) {
            float height = getDisplayedHeight(count);
            listView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, Math.round(height)));
            listView.requestLayout();
        }

    }

    private int getDisplayedHeight(int count) {
        int size = count;
        if (count > 3) {
            size = 3;
        }
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.UNSPECIFIED);
        ListAdapter listAdapter = getAdapter();
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < size; i++) {
            view = listAdapter.getView(i, view, this);
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        return totalHeight + (getDividerHeight() * size) + getPaddingBottom() + getPaddingTop();
    }

}

