package com.example.picturesharedemo1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.picturesharedemo1.Adapter.SectionsPagerAdapter;
import com.example.picturesharedemo1.Fragment.FragmentHome;
import com.example.picturesharedemo1.Fragment.FragmentMy;
import com.example.picturesharedemo1.Fragment.FragmentSearch;
import com.example.picturesharedemo.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener, ViewPager.OnPageChangeListener {
    private ViewPager viewPager;
    private BottomNavigationBar bottomNavigationBar;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewpager);
        bottomNavigationBar = findViewById(R.id.bottom);
        initView();
    }


    private void initView() {
        initViewPager();
        initBottomNavigationBar();
    }
    //配置底部导航栏
    private void initBottomNavigationBar() {
        bottomNavigationBar.setTabSelectedListener(this);
        bottomNavigationBar.clearAll();
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);//自适应大小
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_DEFAULT);
        bottomNavigationBar.setBarBackgroundColor(R.color.white).setActiveColor(R.color.black).setInActiveColor(R.color.blue);
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.home_file,"首页").setInactiveIconResource(R.drawable.home))
                .addItem(new BottomNavigationItem(R.drawable.search_file,"发现").setInactiveIconResource(R.drawable.search))
                .addItem(new BottomNavigationItem(R.drawable.ic_person_black_24dp,"我的").setInactiveIconResource(R.drawable.ic_perm_identity_black_24dp))
                .setFirstSelectedPosition(0)
                .initialise();
    }
    //配置viewpager
    private void initViewPager() {
        viewPager.setOffscreenPageLimit(3);

        fragments = new ArrayList<>();
        fragments.add(new FragmentHome());
        fragments.add(new FragmentSearch());
        fragments.add(new FragmentMy());

        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager(),fragments));
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
    }

    @Override
    public void onTabSelected(int position) {
        viewPager.setCurrentItem(position,true);
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        bottomNavigationBar.selectTab(i);
    }
    @Override
    public void onPageScrollStateChanged(int i) {
    }
}

