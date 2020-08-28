package com.paypad.parator.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.paypad.parator.charge.KeypadFragment;
import com.paypad.parator.charge.LibraryFragment;

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

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "First Tab";
            case 1:
            default:
                return "Second Tab";
        }
    }
}