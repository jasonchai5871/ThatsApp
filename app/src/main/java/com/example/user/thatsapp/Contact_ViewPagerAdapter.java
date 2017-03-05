package com.example.user.thatsapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by User on 2/18/2017.
 */

public class Contact_ViewPagerAdapter extends FragmentPagerAdapter{


    ArrayList<Fragment> mFragments = new ArrayList<>();
    ArrayList<String> mTabTitles = new ArrayList<>();

    public void addFragments(Fragment mFragments, String mTabTitles){
        this.mFragments.add(mFragments);
        this.mTabTitles.add(mTabTitles);
    }

    public Contact_ViewPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles.get(position);
    }
}
