package com.paypad.vuk507.login.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

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
        getEncryptedSharedPreferences(context).edit()
                .putString("email", email)
                .putString("password", password)
                .putString("id", id)
                .apply();
    }

    public static boolean deleteSharedPreferences(Context context){
        boolean commit = getEncryptedSharedPreferences(context).edit()
                .remove("email")
                .remove("password")
                .remove("id").commit();
        return commit;
    }

    public static String getEmailFromCache(Context context){
        return LoginUtils.getEncryptedSharedPreferences(context)
                .getString("email", "No Email");
    }

    public static String getPasswordFromCache(Context context){
        return LoginUtils.getEncryptedSharedPreferences(context)
                .getString("password", "No password");
    }

    public static String getUserIdFromCache(Context context){
        return LoginUtils.getEncryptedSharedPreferences(context)
                .getString("id", "No ID");
    }
}
