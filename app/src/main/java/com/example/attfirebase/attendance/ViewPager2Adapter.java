package com.example.attfirebase.attendance;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPager2Adapter extends FragmentStateAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitles = new ArrayList<>();

    public ViewPager2Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitles.add(title);
    }

    public CharSequence getPageTitle(Object position) {
        return fragmentTitles.get((Integer) position);
    }


}
