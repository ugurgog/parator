package com.paypad.parator.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.paypad.parator.R;
import com.paypad.parator.enums.CurrencyEnum;
import com.paypad.parator.enums.ToastEnum;
import com.paypad.parator.interfaces.CustomDialogListener;
import com.paypad.parator.interfaces.TutorialPopupCallback;

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

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_EN;
import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;
import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.parator.constants.CustomConstants.TYPE_RATE;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_CONTINUE;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_END;

public class CommonUtils {

    /*public static void showToastShort(Context context, String message) {
        showCustomToast(context, message, ToastEnum.TOAST_NORMAL);
    }*/

    public static void displayPopupWindow(View anchorView, Context mContext, String message, TutorialPopupCallback tutorialPopupCallback) {
        PopupWindow popup = new PopupWindow(mContext);
        View layout = ((Activity)mContext).getLayoutInflater().inflate(R.layout.popup_content, null);

        TextView tutorialMsgTv = layout.findViewById(R.id.tutorialMsgTv);
        ImageView closeTutorialImgv = layout.findViewById(R.id.closeTutorialImgv);
        LinearLayout tutorialRl = layout.findViewById(R.id.tutorialRl);

        tutorialMsgTv.setText(message);

        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        //popup.setOutsideTouchable(true);
        //popup.setFocusable(true);
        // Show anchored to button

        popup.setOutsideTouchable(false);
        popup.setFocusable(false);


        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAsDropDown(anchorView);

        tutorialPopupCallback.OnGetPopup(popup);

        closeTutorialImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CustomDialogBoxVert.Builder((Activity) mContext)
                        .setTitle(mContext.getResources().getString(R.string.dont_need_help))
                        .setMessage(mContext.getResources().getString(R.string.dont_need_help_message))
                        .setNegativeBtnVisibility(View.VISIBLE)
                        .setPositiveBtnVisibility(View.VISIBLE)
                        .setPositiveBtnText(mContext.getResources().getString(R.string.continue_walkthrough))
                        .setNegativeBtnText(mContext.getResources().getString(R.string.end_walkthrough))
                        .setPositiveBtnBackground(mContext.getResources().getColor(R.color.Green, null))
                        .setNegativeBtnBackground(mContext.getResources().getColor(R.color.custom_btn_bg_color, null))
                        .setDurationTime(0)
                        .isCancellable(false)
                        .setEdittextVisibility(View.GONE)
                        .setpBtnTextColor(mContext.getResources().getColor(R.color.White, null))
                        .setnBtnTextColor(mContext.getResources().getColor(R.color.Green, null))
                        .OnPositiveClicked(new CustomDialogListener() {
                            @Override
                            public void OnClick() {
                            }
                        }).OnNegativeClicked(new CustomDialogListener() {
                            @Override
                            public void OnClick() {
                                tutorialPopupCallback.OnClosed();
                                popup.dismiss();
                            }
                        }).build();
            }
        });
    }

    /*public static void showCustomToast(Context context, String message) {
            if (context == null) return;
            if (message == null || message.isEmpty()) return;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.toast_layout, null);
            View layout =  view.findViewById(R.id.toast_layout_root);
            layout.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                    context.getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 15, 0));
            TextView text =  layout.findViewById(R.id.text);
            ImageView iconImgv = layout.findViewById(R.id.iconImgv);
            iconImgv.setVisibility(View.GONE);

            text.setText(message);
            text.setTextColor(context.getResources().getColor(R.color.White, null));
            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
    }*/

    public static void showCustomToast(Context context, String message, ToastEnum toastType) {
        if (context == null) return;
        if (message == null || message.isEmpty()) return;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.toast_layout, null);
        View layout =  view.findViewById(R.id.toast_layout_root);

        TextView text =  layout.findViewById(R.id.text);
        ImageView iconImgv = layout.findViewById(R.id.iconImgv);
        iconImgv.setVisibility(View.VISIBLE);

        if(toastType == ToastEnum.TOAST_SUCCESS){
            layout.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.MediumSeaGreen, null),
                    context.getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 15, 0));
            Glide.with(context)
                    .load(R.drawable.ic_check_white_24dp)
                    .into(iconImgv);
        }else if(toastType == ToastEnum.TOAST_ERROR){
            layout.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.red_orange_color_picker, null),
                    context.getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 15, 0));
            Glide.with(context)
                    .load(R.drawable.ic_error_white_24dp)
                    .into(iconImgv);
        }else if(toastType == ToastEnum.TOAST_INFO){
            layout.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue, null),
                    context.getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 15, 0));
            Glide.with(context)
                    .load(R.drawable.ic_error_white_24dp)
                    .into(iconImgv);
        }else if(toastType == ToastEnum.TOAST_WARNING){
            layout.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.Orange, null),
                    context.getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 15, 0));
            Glide.with(context)
                    .load(R.drawable.ic_warning_white_24dp)
                    .into(iconImgv);
        }else if(toastType == ToastEnum.TOAST_NORMAL){
            layout.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.Orange, null),
                    context.getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 15, 0));
            iconImgv.setVisibility(View.GONE);
        }

        text.setText(message);
        text.setTextColor(context.getResources().getColor(R.color.White, null));
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static int getPaddingInPixels(Context context, float paddingDp) {
        float density = Objects.requireNonNull(context).getResources().getDisplayMetrics().density;
        int paddingPixel = (int)(paddingDp * density);
        return paddingPixel;
    }

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%" + n + "s", s);
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

    public static void openAppInGooglePlay(Context context) {
        try {
            String mAddress = "market://details?id=" + context.getPackageName();
            Intent marketIntent = new Intent("android.intent.action.VIEW");
            marketIntent.setData(Uri.parse(mAddress));
            context.startActivity(marketIntent);
        } catch (Exception e) {
            showCustomToast(context, context.getString(R.string.error_upper).concat(" : ").concat(context.getString(R.string.unexpected_error)), ToastEnum.TOAST_ERROR);
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

    public static void hideNavigationBar(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
    }

    public static void showNavigationBar(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE );
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

        if(inputType == TYPE_RATE && doubleVal > 100d)
            doubleVal = 100d;

        String value;

        if(doubleVal != 0d)
            value = decimalFormat.format(doubleVal);
        else
            value = "0.00";

        return value.replaceAll("\\.", " ")
                .replaceAll(",", "\\.")
                .replaceAll(" ", ",");
    }

    public static String getAmountTextWithCurrency(double amount){
        return getDoubleStrValueForView(amount, TYPE_PRICE).concat(" ").concat(CommonUtils.getCurrency().getSymbol());
    }

    public static String getAmountText(double amount){
        return getDoubleStrValueForView(amount, TYPE_PRICE);
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

    public static SpannableStringBuilder setTextViewHTML(Spannable sequence, Context context){
        Typeface face = Typeface.create("sans-serif", Typeface.NORMAL);
        CustomTypefaceSpan customTypefaceSpan = new CustomTypefaceSpan("",face);
        sequence.setSpan(customTypefaceSpan, 0, sequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] spans = spannableStringBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for(URLSpan span : spans){

        }

        return spannableStringBuilder;


        //textView.setText(spannableStringBuilder);
        //textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /*public void makeLinkClickable(SpannableStringBuilder spannableStringBuilder, URLSpan urlSpan, Context context){
        int start = spannableStringBuilder.getSpanStart(urlSpan);
        int end = spannableStringBuilder.getSpanEnd(urlSpan);
        int flags = spannableStringBuilder.getSpanFlags(urlSpan);

        if(urlSpan.getURL().trim().equals("office")){
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {

                }
            };
        }
    }*/
}