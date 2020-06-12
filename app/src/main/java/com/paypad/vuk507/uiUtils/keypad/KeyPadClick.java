package com.paypad.vuk507.uiUtils.keypad;

import android.view.View;

import java.util.ArrayList;

public class KeyPadClick implements View.OnClickListener {

    private keyPadClickListener mListener;
    private ArrayList<Integer> numbers = new ArrayList<>();

    public KeyPadClick(keyPadClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            int num = KeyPadLogic.returnInteger(view);
            numbers = KeyPadLogic.returnList(num, numbers);
            mListener.onKeypadClicked(numbers);

        } else {
            throw new NullPointerException("No listener attached to numpad");
        }
    }
}
