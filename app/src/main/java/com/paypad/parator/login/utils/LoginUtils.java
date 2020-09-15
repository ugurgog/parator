package com.paypad.parator.login.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.paypad.parator.R;
import com.paypad.parator.enums.ToastEnum;
import com.paypad.parator.utils.CommonUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class LoginUtils {

    public static SharedPreferences getEncryptedSharedPreferences(Context context){
        String masterKeyAlias = null;
        SharedPreferences sharedPreferences = null;

        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            sharedPreferences = EncryptedSharedPreferences.create(
                    "secret_shared_prefs_file",
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        return sharedPreferences;
    }

    public static void applySharedPreferences(Context context, String email, String password, String id){
        try{
            getEncryptedSharedPreferences(context).edit()
                    .putString("email", email)
                    .putString("password", password)
                    .putString("id", id)
                    .apply();
        }catch (Exception e){
            CommonUtils.showCustomToast(context, context.getResources().getString(R.string.error_upper)
                    .concat(" : ")
                    .concat(e.getMessage() != null ? e.getMessage() : context.getResources().getString(R.string.unexpected_error)), ToastEnum.TOAST_ERROR);
        }
    }

    public static boolean deleteSharedPreferences(Context context){
        boolean commit = getEncryptedSharedPreferences(context).edit()
                .remove("email")
                .remove("password")
                .remove("id").commit();
        return commit;
    }

    public static String getEmailFromCache(Context context){
        try{
            return LoginUtils.getEncryptedSharedPreferences(context)
                    .getString("email", "No Email");
        }catch (Exception e){
            CommonUtils.showCustomToast(context, context.getResources().getString(R.string.error_upper)
                    .concat(" : ")
                    .concat(e.getMessage() != null ? e.getMessage() : context.getResources().getString(R.string.unexpected_error)), ToastEnum.TOAST_ERROR);
        }
        return "";
    }

    public static String getPasswordFromCache(Context context){
        try{
            return LoginUtils.getEncryptedSharedPreferences(context)
                    .getString("password", "No password");
        }catch (Exception e){
            CommonUtils.showCustomToast(context, context.getResources().getString(R.string.error_upper)
                .concat(" : ")
                .concat(e.getMessage() != null ? e.getMessage() : context.getResources().getString(R.string.unexpected_error)), ToastEnum.TOAST_ERROR);
        }
        return "";
    }

    public static String getUserIdFromCache(Context context){
        try{
            return LoginUtils.getEncryptedSharedPreferences(context)
                    .getString("id", "No ID");
        }catch (Exception e){
            CommonUtils.showCustomToast(context, context.getResources().getString(R.string.error_upper)
                    .concat(" : ")
                    .concat(e.getMessage() != null ? e.getMessage() : context.getResources().getString(R.string.unexpected_error)), ToastEnum.TOAST_ERROR);
        }
        return "";
    }
}
