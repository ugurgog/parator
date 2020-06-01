package com.paypad.vuk507.charge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;

import butterknife.ButterKnife;

public class LibraryFragment extends BaseFragment {

    View mView;

    public LibraryFragment() {

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
        mView = inflater.inflate(R.layout.fragment_library, container, false);
        ButterKnife.bind(this, mView);
        initVariables();
        return mView;
    }

    private void initVariables() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

}