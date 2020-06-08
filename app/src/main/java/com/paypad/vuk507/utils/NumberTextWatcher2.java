package com.paypad.vuk507.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.ParseException;

public class NumberTextWatcher2 implements TextWatcher {

    private DecimalFormat dfnd;
    private boolean hasFractionalPart;

    private EditText et;

    public NumberTextWatcher2(EditText et) {
        dfnd = new DecimalFormat("###,###.##");
        this.et = et;
        hasFractionalPart = false;
    }

    @SuppressWarnings("unused")
    private static final String TAG = "NumberTextWatcher2";

    public void afterTextChanged(Editable s) {
        et.removeTextChangedListener(this);

        try {
            int inilen, endlen;
            inilen = et.getText().length();

            String v = s.toString().replace(String.valueOf(dfnd.getDecimalFormatSymbols().getGroupingSeparator()), "");
            Number n = dfnd.parse(v);
           // int cp = et.getSelectionStart();


            Log.i("Info", "dfnd.format(n):" + dfnd.format(n));

            et.setText(dfnd.format(n));

            et.setSelection(et.getText().length() );

            /*endlen = et.getText().length();

            int sel = (cp + (endlen - inilen));
            if (sel > 0 && sel <= et.getText().length()) {
                et.setSelection(sel);
            } else {
                // place cursor at the end?
                et.setSelection(et.getText().length() - 1);
            }*/
        } catch (NumberFormatException nfe) {
            // do nothing?
        } catch (ParseException e) {
            // do nothing?
        }

        et.addTextChangedListener(this);
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.i("Info", "s.toString():" + s.toString());
        Log.i("Info", "String.valueOf(dfnd.getDecimalFormatSymbols().getDecimalSeparator())):"
                + String.valueOf(dfnd.getDecimalFormatSymbols().getDecimalSeparator()));

        if (s.toString().contains(String.valueOf(dfnd.getDecimalFormatSymbols().getDecimalSeparator()))) {
            hasFractionalPart = true;
        } else {
            hasFractionalPart = false;
        }
    }

}