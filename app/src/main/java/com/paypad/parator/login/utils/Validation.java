package com.paypad.parator.login.utils;

import android.content.Context;
import android.text.TextUtils;

import com.paypad.parator.R;

public class Validation {

    private static Validation instance = null;
    private String errorMessage;

    private final static int PASSWORD_MAX_LENGTH = 6;

    public static Validation getInstance() {
        if (instance == null) {
            instance = new Validation();
        }
        return(instance);
    }

    private Validation() {
    }

    public boolean isValidUserName(Context context, String userName){

        if (TextUtils.isEmpty(userName)) {
            errorMessage = context.getString(R.string.username_empty_error);
            return false;
        }

        return true;
    }

    public boolean isValidEmail(Context context, String email){

        if (TextUtils.isEmpty(email)) {
            errorMessage =context.getString(R.string.email_empty_error);
            return false;
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            errorMessage = context.getString(R.string.email_invalid_error);
            return false;
        }

        return true;
    }

    public boolean isValidPassword(Context context, String password){

        if (TextUtils.isEmpty(password)) {
            errorMessage = context.getString(R.string.password_empty_error);
            return false;
        }

        if (password.length() < PASSWORD_MAX_LENGTH) {
            errorMessage = context.getString(R.string.password_length_error);
            return false;
        }

        return true;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
