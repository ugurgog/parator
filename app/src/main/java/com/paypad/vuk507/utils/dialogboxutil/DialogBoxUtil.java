package com.paypad.vuk507.utils.dialogboxutil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.widget.ArrayAdapter;

import com.paypad.vuk507.R;
import com.paypad.vuk507.interfaces.PhotoChosenCallback;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.dialogboxutil.interfaces.InfoDialogBoxCallback;
import com.paypad.vuk507.utils.dialogboxutil.interfaces.YesNoDialogBoxCallback;

import static com.paypad.vuk507.constants.CustomConstants.CODE_CAMERA_POSITION;
import static com.paypad.vuk507.constants.CustomConstants.CODE_GALLERY_POSITION;
import static com.paypad.vuk507.constants.CustomConstants.CODE_PHOTO_REMOVE;

public class DialogBoxUtil {

    public static void photoChosenDialogBox(Context context, String title, boolean photoExist, final PhotoChosenCallback photoChosenCallback) {
        CommonUtils.hideKeyBoard(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        adapter.add("  " + context.getResources().getString(R.string.open_gallery));
        adapter.add("  " + context.getResources().getString(R.string.open_camera));

        if (photoExist)
            adapter.add("  " + context.getResources().getString(R.string.remove_photo));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null && !title.isEmpty())
            builder.setTitle(title);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == CODE_GALLERY_POSITION)
                    photoChosenCallback.onGallerySelected();
                else if (item == CODE_CAMERA_POSITION)
                    photoChosenCallback.onCameraSelected();
                else if (item == CODE_PHOTO_REMOVE) {
                    photoChosenCallback.onPhotoRemoved();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showErrorDialog(Context context, String errMessage, final InfoDialogBoxCallback infoDialogBoxCallback) {
        CommonUtils.hideKeyBoard(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.error_upper));
        builder.setIcon(context.getResources().getDrawable(R.drawable.ic_error_white_24dp, null));
        builder.setMessage(errMessage);

        builder.setNeutralButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                infoDialogBoxCallback.okClick();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showInfoDialogBox(Context context, String message, String title, final InfoDialogBoxCallback infoDialogBoxCallback) {
        CommonUtils.hideKeyBoard(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(context.getResources().getDrawable(R.drawable.ic_error_white_24dp, null));
        builder.setMessage(message);

        if (title != null && !title.trim().isEmpty())
            builder.setTitle(title);

        builder.setNeutralButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                infoDialogBoxCallback.okClick();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showSuccessDialogBox(Context context, String message, String title, final InfoDialogBoxCallback infoDialogBoxCallback) {
        CommonUtils.hideKeyBoard(context);
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setIcon(R.drawable.ic_check_white_24dp);

        if (title != null && !title.isEmpty())
            alertDialog.setTitle(title);

        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        infoDialogBoxCallback.okClick();
                    }
                });
        alertDialog.show();
    }

    public static void showYesNoDialog(Context context, String title, String message, final YesNoDialogBoxCallback yesNoDialogBoxCallback) {
        CommonUtils.hideKeyBoard(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.ic_warning_white_24dp);
        builder.setMessage(message);
        builder.setCancelable(false);

        if (title != null && !title.isEmpty())
            builder.setTitle(title);

        builder.setPositiveButton(context.getResources().getString(R.string.yes_upper), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                yesNoDialogBoxCallback.yesClick();
            }
        });

        builder.setNegativeButton(context.getResources().getString(R.string.no_upper), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                yesNoDialogBoxCallback.noClick();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    public static void showInfoDialogWithLimitedTime(Context context, String title, String message, long timeInMs, final InfoDialogBoxCallback infoDialogBoxCallback) {
        CommonUtils.hideKeyBoard(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (title != null && !title.isEmpty())
            builder.setTitle(title);

        builder.setIcon(R.drawable.ic_check_white_24dp);
        builder.setMessage(message);
        builder.setCancelable(false);
        final AlertDialog alert = builder.create();
        alert.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alert.dismiss();
                infoDialogBoxCallback.okClick();
            }
        }, timeInMs);
    }


    public static void showDialogWithJustPositiveButton(Context context, String title,
                                                        String message, String buttonDesc, final InfoDialogBoxCallback infoDialogBoxCallback) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(buttonDesc, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                infoDialogBoxCallback.okClick();
            }
        });
        alertDialog.show();
    }

}
