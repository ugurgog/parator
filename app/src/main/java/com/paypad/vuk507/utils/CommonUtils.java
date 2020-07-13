package com.paypad.vuk507.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.paypad.vuk507.R;
import com.paypad.vuk507.enums.CurrencyEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
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

    public static int getPaddingInPixels(Context context, float paddingDp) {
        float density = Objects.requireNonNull(context).getResources().getDisplayMetrics().density;
        int paddingPixel = (int)(paddingDp * density);
        return paddingPixel;
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

    public static String getDefaultLanguageCode() {
        return Locale.getDefault().getLanguage();
    }

    public static String getDefaultCountryCode() {
        return Locale.getDefault().getCountry();
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
        String value = getDoubleStrValueForView(amount, inputType);
        editText.setText(value);
        editText.setSelection(editText.getText().length());
    }


    public static String getDoubleStrValueForView(double doubleVal, int inputType){
        DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
        Number x = null;

        /*try {
            x = decimalFormat.parse(String.valueOf(doubleVal));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(inputType == TYPE_RATE && doubleVal > 100d)
            doubleVal = 100d;

        String value;

        if(doubleVal != 0d)
            value = decimalFormat.format(x);
        else
            value = "0.00";*/


        if(inputType == TYPE_RATE && doubleVal > 100d)
            doubleVal = 100d;

        String value;

        if(doubleVal != 0d)
            value = decimalFormat.format(doubleVal);
        else
            value = "0.00";

        return value;
    }

    public static int getDarkRandomColor(Context context) {

        int[] colorList = getDarkColors();
        Random rand = new Random();
        return colorList[rand.nextInt(colorList.length)];
    }

    public static int[] getDarkColors(){
        int[] colorList = {
                R.color.style_color_primary,
                R.color.style_color_accent,
                R.color.fab_color_pressed,
                R.color.blue_color_picker,
                R.color.brown_color_picker,
                R.color.green_color_picker,
                R.color.orange_color_picker,
                R.color.red_color_picker,
                R.color.red_orange_color_picker,
                R.color.violet_color_picker,
                R.color.dot_dark_screen1,
                R.color.dot_dark_screen2,
                R.color.dot_dark_screen3,
                R.color.dot_dark_screen4,
                R.color.Fuchsia,
                R.color.DarkRed,
                R.color.Olive,
                R.color.Purple,
                R.color.gplus_color_1,
                R.color.gplus_color_2,
                R.color.gplus_color_3,
                R.color.gplus_color_4,
                R.color.MediumTurquoise,
                R.color.RoyalBlue,
                R.color.Green
        };
        return colorList;
    }

    public static int[] getItemColors(){
        int[] colorList = {
                R.color.Gray,
                R.color.style_color_accent,
                R.color.fab_color_pressed,
                R.color.blue_color_picker,
                R.color.brown_color_picker,
                R.color.green_color_picker,
                R.color.orange_color_picker,
                R.color.red_color_picker,
                R.color.red_orange_color_picker,
                R.color.violet_color_picker,
                R.color.dot_dark_screen1,
                R.color.dot_dark_screen2
        };
        return colorList;
    }

    public static int getNavigationBarHeight(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        ((Activity) context).getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight)
            return realHeight - usableHeight;
        else
            return 0;
    }

    //Navigation bar opened or closed
    public static boolean showNavigationBar(Resources resources)
    {
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }

    public static int getScreenWidth(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        return width;
    }

    public static int getScreenHeight(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        return height;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}