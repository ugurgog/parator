package com.paypad.vuk507.charge;

import android.graphics.Matrix;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KeypadFragment extends BaseFragment {

    View mView;

    public KeypadFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_keypad, container, false);
        ButterKnife.bind(this, mView);
        initVariables();
        return mView;
    }



    private void initVariables() {

        System.out.println();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

}