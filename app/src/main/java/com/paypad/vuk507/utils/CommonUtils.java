package com.paypad.vuk507.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.CurrencyEnum;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_EN;
import static com.paypad.vuk507.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.vuk507.constants.CustomConstants.TYPE_RATE;

public class CommonUtils {

    public static void showToastShort(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static int getPaddingInPixels(Context context, float dpSize) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int paddingInPx = (int) (dpSize * scale + 0.5f);
        return paddingInPx;
    }

    public static boolean isEmptyCheck(String text) {
        if(text != null && !text.isEmpty()){
            if(text.trim().equals("null"))
                return true;
            else
                return false;
        } else
            return true;
    }

    public static String getVersionName(Context context) {

        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(pInfo).versionName;

    }

    public static String getVersion(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            String version = packInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "0";
        }
    }

    public static boolean checkCameraHardware(Context context) {
        // this device has a camera
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static void hideKeyBoard(Context context) {
        Activity activity = (Activity) context;
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static Date fromISO8601UTC(String dateStr) {

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);

        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void snackbarDisplay(View view, Context context, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(context.getResources().getColor(R.color.Red, null));
        TextView tv = snackBarView.findViewById(R.id.snackbar_text);
        tv.setTextColor(context.getResources().getColor(R.color.White, null));
        snackbar.show();
    }

    public static String getLanguage() {
        String language = Locale.getDefault().getLanguage();

        if (language.equals(LANGUAGE_TR)) {
            return LANGUAGE_TR;
        }
        return LANGUAGE_EN;
    }

    public static void setSaveBtnEnability(boolean enability, Button saveBtn, Context context){
        if(enability){
            saveBtn.setBackgroundColor(context.getResources().getColor(R.color.DodgerBlue, null));
            saveBtn.setEnabled(true);
        }else {
            saveBtn.setBackgroundColor(context.getResources().getColor(R.color.Gray, null));
            saveBtn.setEnabled(false);
        }
    }

    public static void showKeyboard(Context context, boolean showKeyboard, EditText editText) {

        if (showKeyboard) {
            InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(context).getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(imm).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } else {
            InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(context).getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(imm).hideSoftInputFromWindow(editText.getWindowToken(), 0);
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(true);
        }
    }

    public static void setBtnFirstCondition(Context context, Button button, String btnText){
        //button.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.button_delete_back, null),
        //        context.getResources().getColor(R.color.button_delete_border, null), GradientDrawable.RECTANGLE, 20, 2));

        button.setBackground(context.getResources().getDrawable(R.drawable.custom_button_bg, null));

        button.setTextColor(context.getResources().getColor(R.color.button_delete_writing, null));
        button.setText(btnText);
    }

    public static void setBtnSecondCondition(Context context, Button button, String btnText){
        //button.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.button_delete_confirm_back, null),
        //        context.getResources().getColor(R.color.button_delete_confirm_border, null), GradientDrawable.RECTANGLE, 20, 2));


        button.setBackground(context.getResources().getDrawable(R.drawable.custom_button_delete_bg, null));


        button.setTextColor(context.getResources().getColor(R.color.button_delete_writing_confirm, null));
        button.setText(btnText);
    }

    public static CurrencyEnum getCurrency(){
        return CurrencyEnum.TL;
    }

    public static void setAmountToView(double amount, EditText editText, int inputType){
        DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
        Number x = null;
        try {
            x = decimalFormat.parse(String.valueOf(amount));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(inputType == TYPE_RATE && amount > 100)
            amount = 100;

        if(amount == 0)
            editText.setText("");
        else{
            editText.setText(decimalFormat.format(x));
        }
        editText.setSelection(editText.getText().length());
    }
}