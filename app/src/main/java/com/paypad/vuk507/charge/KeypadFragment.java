package com.paypad.vuk507.charge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.keypad.KeyPad;
import com.paypad.vuk507.keypad.KeyPadClick;
import com.paypad.vuk507.keypad.keyPadClickListener;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class KeypadFragment extends BaseFragment {

    View mView;

    private KeyPad keypad;

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
        initListeners();
        return mView;
    }

    private void initVariables() {
        keypad = mView.findViewById(R.id.keypad);

        //Realm realm = Realm.getDefaultInstance();

        //final RealmResults<Dog> puppies = realm.where(Dog.class).lessThan("age", 2).findAll();
        //puppies.size();

    }

    private void initListeners() {
        keypad.setOnNumPadClickListener(new KeyPadClick(new keyPadClickListener() {
            @Override
            public void onKeypadClicked(ArrayList<Integer> nums) {

            }
        }));
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

}