package com.paypad.parator.utils;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;

import static com.paypad.parator.constants.CustomConstants.TYPE_PRICE;
import static com.paypad.parator.constants.CustomConstants.TYPE_RATE;

public class NumberFormatWatcher implements TextWatcher {

    private DecimalFormat decimalFormat;
    private EditText editText;
    private int inputType;
    private double mMaxValue = 0d;

    private static final int PRICE_MAX_LEN = 10;
    private static final int RATE_MAX_LEN = 6;
    private ReturnEtTextCallback returnEtTextCallback;

    public interface ReturnEtTextCallback{
        void OnReturnEtValue(String text);
    }

    public void setReturnEtTextCallback(ReturnEtTextCallback returnEtTextCallback) {
        this.returnEtTextCallback = returnEtTextCallback;
    }

    public NumberFormatWatcher(EditText editText, int inputType, double maxValue) {
        decimalFormat = new DecimalFormat("###,##0.00");
        this.editText = editText;
        this.inputType = inputType;
        this.mMaxValue = maxValue;
    }

    public void afterTextChanged(Editable s) {
        editText.removeTextChangedListener(this);

        try {
            if(inputType == TYPE_RATE){
                editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(RATE_MAX_LEN)});
                editText.setHint("0 %");
            }
            else if(inputType == TYPE_PRICE){
                editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(PRICE_MAX_LEN)});
                editText.setHint("0.00 ".concat(CommonUtils.getCurrency().getSymbol()));
            }

            double num = DataUtils.getDoubleValueFromFormattedString(s.toString());

            if(num > mMaxValue && mMaxValue != 0)
                num = mMaxValue;

            if(num == 0)
                editText.setText("");
            else{
                editText.setText(decimalFormat.format(num));
            }

            editText.setSelection(editText.getText().length());

            if(returnEtTextCallback != null)
                returnEtTextCallback.OnReturnEtValue(editText.getText().toString());

        } catch (Exception e) {
            //TODO - hata loglamasi yapilacak mi?
        }

        editText.addTextChangedListener(this);
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

}