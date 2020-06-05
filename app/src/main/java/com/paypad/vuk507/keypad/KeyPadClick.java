package com.paypad.vuk507.keypad;

import android.view.View;

import java.util.ArrayList;

import static com.paypad.vuk507.keypad.KeyPadLogic.returnInteger;
import static com.paypad.vuk507.keypad.KeyPadLogic.returnList;

public class KeyPadClick implements View.OnClickListener {

    private keyPadClickListener mListener;
    private ArrayList<Integer> numbers = new ArrayList<>();

    public KeyPadClick(keyPadClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            int num = returnInteger(view);
            numbers = returnList(num, numbers);
            mListener.onKeypadClicked(numbers);

        } else {
            throw new NullPointerException("No listener attached to numpad");
        }
    }
}
