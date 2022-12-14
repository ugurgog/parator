package com.paypad.parator.utils;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.paypad.parator.R;

import java.util.Objects;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class IntentSelectUtil {

    public static Intent getCameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        return intent;
    }

    public static Intent getGalleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }

    public static Intent getGalleryIntentForVideo(Context context){
        Uri videoUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider",
                Objects.requireNonNull(FileAdapter.getOutputMediaFile(MEDIA_TYPE_VIDEO, context.getResources().getString(R.string.app_name))));
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setTypeAndNormalize("video/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        return intent;
    }
}