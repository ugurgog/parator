package com.paypad.vuk507.keypad;

import android.view.View;

import com.example.numpad.numPadClickListener;

import java.util.ArrayList;

import static com.paypad.vuk507.keypad.KeyPadLogic.returnInteger;
import static com.paypad.vuk507.keypad.KeyPadLogic.returnList;

public class KeyPadClick implements View.OnClickListener {

    private numPadClickListener mListener;
    private ArrayList<Integer> numbers = new ArrayList<>();

    public KeyPadClick(numPadClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            int num = returnInteger(view);
            numbers = returnList(num, numbers);
            mListener.onNumpadClicked(numbers);

        } else {
            throw new NullPointerException("No listener attached to numpad");
        }
    }
}
