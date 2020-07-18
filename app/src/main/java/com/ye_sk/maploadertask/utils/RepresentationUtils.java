package com.ye_sk.maploadertask.utils;

import android.content.Context;

import com.ye_sk.maploadertask.R;

public class RepresentationUtils {
    private static long SIZE_DIVIDER = 1024;
    private static long GB_DIVIDER = SIZE_DIVIDER * SIZE_DIVIDER * SIZE_DIVIDER;
    private static long MB_DIVIDER = SIZE_DIVIDER * SIZE_DIVIDER;
    private static long KB_DIVIDER = SIZE_DIVIDER;

    public static String getStringNodeSize(long size, Context context) {
        if (size > GB_DIVIDER) {
            return getMeasuredSize(size, GB_DIVIDER) + context.getString(R.string.gygabyte);
        } else if (size > MB_DIVIDER) {
            return getMeasuredSize(size, MB_DIVIDER) + context.getString(R.string.megabyte);
        } else if (size > KB_DIVIDER) {
            return getMeasuredSize(size, KB_DIVIDER) + context.getString(R.string.kilobyte);
        }
        return size + context.getString(R.string.unit_byte);
    }


    private static String getMeasuredSize(long size, long divider) {
        double measuredSize = size / (double) divider;
        double ratioSizePart = measuredSize % 1;
        return (ratioSizePart >= 0.05 && ratioSizePart <= 0.95) ?
                String.format("%.1f ", measuredSize) : String.format("%d ", Math.round(measuredSize));
    }

}
