package com.reactnative.mg.imagepicker.models;

import android.util.Log;

import com.reactnative.mg.imagepicker.SelectorSettings;

public class ImageItem {
    private static final String TAG = "ImageItem";
    public static final String CAMERA_PATH = "Camera";

    public String path;
    public String name;
    public long time;

    public ImageItem(String name, String path, long time){
        this.name = name;
        this.path = path;
        this.time = time;
    }


    public boolean isCamera() {
        return this.path.equals(SelectorSettings.CAMERA_ITEM_PATH);
    }

    @Override
    public boolean equals(Object o) {
        try {
            ImageItem other = (ImageItem) o;
            return this.path.equalsIgnoreCase(other.path);
        }catch (ClassCastException e){
            Log.e(TAG, "equals: " + Log.getStackTraceString(e));
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        return "ImageItem{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", time=" + time +
                '}';
    }
}