package com.uit.carrental.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.uit.carrental.FragmentPages.SplashFragment;
import com.uit.carrental.R;

public class SplashPagerAdapter extends FragmentStateAdapter {

    private final int[] splashLayouts = {
            R.layout.sflash_1,
            R.layout.sflash_2,
            R.layout.sflash_3,
            R.layout.sflash_4
    };

    public SplashPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return SplashFragment.newInstance(splashLayouts[position], position);
    }

    @Override
    public int getItemCount() {
        return splashLayouts.length;
    }
}