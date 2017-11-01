package com.mg.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;


class Compression {

    public File compressImage(final Activity activity, final ReadableMap options, final String originalImagePath) throws IOException {
        Integer maxWidth = options.hasKey("width") ? options.getInt("width") : null;
        Integer maxHeight = options.hasKey("height") ? options.getInt("height") : null;
        Integer quality = options.hasKey("compressQuality") ? options.getInt("compressQuality") : null;

        if (maxWidth == null || maxWidth<=0) {
            maxWidth = 300;
        }
        if (maxHeight == null || maxHeight<=0 ) {
            maxHeight = 300;
        }
        if (quality == null || quality >= 100 || quality <= 0) {
            return new File(originalImagePath);
        }
        Compressor compressor = new Compressor(activity)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).getAbsolutePath());

        compressor.setQuality(quality);

        if (maxWidth != null) {
            compressor.setMaxWidth(maxWidth);
        }

        if (maxHeight != null) {
            compressor.setMaxHeight(maxHeight);
        }

        File image = new File(originalImagePath);

        String[] paths = image.getName().split("\\.(?=[^\\.]+$)");
        String compressedFileName = paths[0] + "-compressed";

        if(paths.length > 1)
            compressedFileName += "." + paths[1];

        return compressor
                .compressToFile(image, compressedFileName);
    }

    synchronized void compressVideo(final Activity activity, final ReadableMap options,
                                    final String originalVideo, final String compressedVideo, final Promise promise) {
        // todo: video compression
        // failed attempt 1: ffmpeg => slow and licensing issues
        promise.resolve(originalVideo);
    }
}
