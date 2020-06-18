package com.example.picturesharedemo1.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.picturesharedemo1.Fragment.AlbumFragment;
import com.example.picturesharedemo1.Fragment.CollectFragment;
import com.example.picturesharedemo1.Fragment.LikeFragment;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private String[] mTitles = new String[]{"相册", "点赞", "收藏"};

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return new LikeFragment();
        } else if (position == 2) {
            return new CollectFragment();
        }
        return new AlbumFragment();//position=0
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
