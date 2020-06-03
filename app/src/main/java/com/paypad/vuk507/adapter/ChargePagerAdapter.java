package com.paypad.vuk507.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.paypad.vuk507.charge.KeypadFragment;
import com.paypad.vuk507.charge.LibraryFragment;

public class ChargePagerAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    public ChargePagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new KeypadFragment();
            case 1:
                return new LibraryFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

}