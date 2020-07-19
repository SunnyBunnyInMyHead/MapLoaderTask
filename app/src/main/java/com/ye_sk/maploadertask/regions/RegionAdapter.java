package com.ye_sk.maploadertask.regions;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ye_sk.maploadertask.R;
import com.ye_sk.maploadertask.download.DownloadController;

import java.util.LinkedList;
import java.util.List;

public class RegionAdapter extends BaseAdapter {
    private LayoutInflater lInflater;
    private List<Region> regions;
    private Context context;

    public RegionAdapter(Context context) {
        this.regions = RegionController.INSTANCE.getRegions().getFirst().getSubRegions();
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public RegionAdapter(Context context, List<Region> regions) {
        this.context = context;
        this.regions = regions;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return regions.size();
    }

    @Override
    public Object getItem(int position) {
        return regions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Region region = regions.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = lInflater.inflate(R.layout.region_item, null);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.delete = (ImageView) convertView.findViewById(R.id.delete);
            holder.map = (ImageView) convertView.findViewById(R.id.map);
            holder.download = (ImageView) convertView.findViewById(R.id.download);

            holder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DownloadController.INSTANCE.addToQueue(region);
                }
            });
/*            if (region.isLoaded()) {
                holder.map.setBackgroundColor(Color.GREEN);
            }*/
            region.addListener(new OnLoadStatusChangeListener() {
                @Override
                public void onStatusChanged(boolean newStatus) {
                    if (newStatus && holder != null && holder.map != null && region.isLoaded())
                        holder.map.setBackgroundColor(Color.GREEN);
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        /*if(!region.getMap()) {
            holder.download.setVisibility(View.INVISIBLE);
        }*/



        holder.title.setText(region.getTitle());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //notifyDataSetChanged();
            }
        });
        if (region.getSubRegions().size() != 0) {
            holder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SubRegions.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("regions", new Gson().toJson(region.getSubRegions(), new TypeToken<LinkedList<Region>>() {
                    }.getType()));
                    context.startActivity(intent);
                }
            });
        }
        holder.ref = position;


        return convertView;
    }


    private class ViewHolder {
        TextView title;
        ImageView download;
        ImageView delete;
        ImageView map;
        int ref;
    }

}
