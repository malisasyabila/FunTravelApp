package com.example.funtravelapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> imagePaths;
    private int position;

    public ImageAdapter(Context context, List<String> imagePaths) {
        this.mContext = context;
        this.imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return imagePaths.size();
    }

    @Override
    public Object getItem(int position) {
        return imagePaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(300, 300));
        } else {
            imageView = (ImageView) convertView;
        }

        Glide.with(mContext)
                .load(imagePaths.get(position))
                .into(imageView);

        return imageView;
    }

    public void remove(int position) {
        if (position >= 0 && position < imagePaths.size()) {
            imagePaths.remove(position); // actually remove from the list
            notifyDataSetChanged();      // update the GridView
        }
    }
}