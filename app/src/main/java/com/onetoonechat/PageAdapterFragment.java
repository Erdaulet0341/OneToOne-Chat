package com.onetoonechat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapterFragment extends FragmentPagerAdapter {

    int tabCount;

    public PageAdapterFragment(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        tabCount = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0:
                return new FragmentChat();

            case 1:
                return new FragmentStatus();

            case 2:
                return new FragmentCalls();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
