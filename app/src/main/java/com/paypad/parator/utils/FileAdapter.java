package com.paypad.parator.utils;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class FileAdapter {

    public static File getOutputMediaFile(int type, String appName) {

        String directoryChild = null;

        switch (type) {
            case MEDIA_TYPE_IMAGE:
                directoryChild = Environment.DIRECTORY_PICTURES;
                break;

            case MEDIA_TYPE_VIDEO:
                directoryChild = Environment.DIRECTORY_MOVIES;
                break;
            case MEDIA_TYPE_AUDIO:
                directoryChild = Environment.DIRECTORY_DOCUMENTS;
                break;
            default:
                break;
        }

        if (directoryChild != null && !directoryChild.isEmpty()) {

            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(appName), directoryChild);

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(appName, "failed to create directory");
                    return null;
                }
            }

            @SuppressLint("SimpleDateFormat")
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            File mediaFile;
            if (type == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "IMG_" + timeStamp + ".jpg");
                return mediaFile;
            } else if (type == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "VID_" + timeStamp + ".mp4");
                return mediaFile;
            } else {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "AUD_" + timeStamp + ".mp3");
                return mediaFile;
            }
        }

        return null;
    }

    public static File getCropMediaFile(String appName) {
        File mediaFile;
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(appName), Environment.DIRECTORY_PICTURES);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(appName, "failed to create directory");
                return null;
            }
        }
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_CROP.jpg");
        return mediaFile;
    }
}
