package com.uit.carrental.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.uit.carrental.FragmentPages.Customer.ChatFragment;
import com.uit.carrental.FragmentPages.Customer.CustomerHomeFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new CustomerHomeFragment();
            case 1:
                return new ChatFragment();
            default:
                return new CustomerHomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Two tabs: Home and Chat
    }
}