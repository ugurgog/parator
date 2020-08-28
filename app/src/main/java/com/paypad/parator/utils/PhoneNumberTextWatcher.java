package com.paypad.parator.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Objects;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class PhoneNumberTextWatcher implements TextWatcher {
    private EditText et;
    private PhoneNumberUtil util;

    // Pass the EditText instance to TextWatcher by constructor
    public PhoneNumberTextWatcher(EditText et, PhoneNumberUtil util) {
        this.et = et;
        this.util = util;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        // Unregister self before update
        et.removeTextChangedListener(this);

        if(s != null && !s.toString().isEmpty()){
            Phonenumber.PhoneNumber phoneNumber = null;
            try {
                phoneNumber = util.parse(s.toString(), CommonUtils.getDefaultCountryCode());
                String formattedPhoneNum = util.format(Objects.requireNonNull(phoneNumber), PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                et.setText(formattedPhoneNum);
                et.setSelection(et.getText().length());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Re-register self after update
        et.addTextChangedListener(this);
    }
}