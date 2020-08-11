package com.paypad.vuk507.model.pojo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.paypad.vuk507.R;
import com.paypad.vuk507.utils.ExifUtil;
import com.paypad.vuk507.utils.UriAdapter;

import java.io.IOException;
import java.util.Objects;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.paypad.vuk507.constants.CustomConstants.CAMERA_TEXT;
import static com.paypad.vuk507.constants.CustomConstants.FROM_FILE_TEXT;
import static com.paypad.vuk507.constants.CustomConstants.GALLERY_TEXT;

public class PhotoSelectUtil {

    private Bitmap bitmap = null;
    private Uri mediaUri = null;
    private String imageRealPath = null;
    private Context context;
    private Intent data;
    private String type;
    private boolean portraitMode;

    public PhotoSelectUtil() {
    }

    public PhotoSelectUtil(Context context, Intent data, String type) {
        this.context = context;
        this.data = data;
        this.type = type;
        routeSelection();
        setPortraitMode();
    }

    public PhotoSelectUtil(Context context, Uri uri, String type) {
        this.context = context;
        this.type = type;
        this.mediaUri = uri;
        routeSelection();
        setPortraitMode();
    }

    private void routeSelection() {
        switch (type) {
            case CAMERA_TEXT:
                onSelectFromCameraResult();
                break;
            case GALLERY_TEXT:
                onSelectFromGalleryResult();
                break;
            case FROM_FILE_TEXT:
                onSelectFromFileResult();
                break;
            default:
                break;
        }
    }

    private void onSelectFromFileResult() {
        if (mediaUri == null) return;
        imageRealPath = UriAdapter.getRealPathFromURI(mediaUri, context);

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeFile(imageRealPath, options);
        } catch (Exception e) {
            return;
        }

        if (bitmap == null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), mediaUri);
            } catch (IOException e) {
                return;
            }
        }

        if (imageRealPath != null && !imageRealPath.isEmpty())
            bitmap = ExifUtil.rotateImageIfRequired(imageRealPath, bitmap);
        else {
            imageRealPath = UriAdapter.getFilePathFromURI(context, mediaUri, MEDIA_TYPE_IMAGE,
                    context.getResources().getString(R.string.app_name));
            bitmap = ExifUtil.rotateImageIfRequired(imageRealPath, bitmap);
        }
    }

    private void onSelectFromGalleryResult() {
        if (data == null) return;
        mediaUri = data.getData();

        if (mediaUri == null) return;
        imageRealPath = UriAdapter.getPathFromGalleryUri(context, mediaUri);

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeFile(imageRealPath, options);
        } catch (Exception e) {
            return;
        }

        if (bitmap == null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), mediaUri);
            } catch (IOException e) {
                return;
            }
        }

        bitmap = ExifUtil.rotateImageIfRequired(imageRealPath, bitmap);
    }

    private void onSelectFromCameraResult() {
        if (data == null) return;
        mediaUri = data.getData();

        if (mediaUri == null) return;
        imageRealPath = UriAdapter.getPathFromGalleryUri(context, mediaUri);

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeFile(imageRealPath, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bitmap == null)
            bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");

        bitmap = ExifUtil.rotateImageIfRequired(imageRealPath, bitmap);
    }

    private void setPortraitMode() {
        if (bitmap == null)
            return;

        int width = bitmap.getWidth();
        int heigth = bitmap.getHeight();

        portraitMode = heigth > width;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Uri getMediaUri() {
        return mediaUri;
    }

    public void setMediaUri(Uri mediaUri) {
        this.mediaUri = mediaUri;
    }

    public String getImageRealPath() {
        return imageRealPath;
    }

    public void setImageRealPath(String imageRealPath) {
        this.imageRealPath = imageRealPath;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Intent getData() {
        return data;
    }

    public void setData(Intent data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPortraitMode() {
        return portraitMode;
    }

    public void setPortraitMode(boolean portraitMode) {
        this.portraitMode = portraitMode;
    }
}
