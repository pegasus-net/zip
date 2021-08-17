package com.icarus.unzip.util;

import android.view.Menu;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;


public class BottomNavigationHelper {

    public static void bind(ViewPager pager, BottomNavigationView navigation) {
        final PagerAdapter adapter = pager.getAdapter();
        final Menu menu = navigation.getMenu();
        if (adapter == null || adapter.getCount() <= 0) {
            return;
        }
        if (menu == null || menu.size() <= 0) {
            return;
        }
        if (pager.getAdapter().getCount() != navigation.getMenu().size()) {
            return;
        }
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                navigation.setSelectedItemId(navigation.getMenu().getItem(position).getItemId());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        navigation.setOnNavigationItemSelectedListener(item -> {
            for (int i = 0; i < menu.size(); i++) {
                if (Objects.equals(menu.getItem(i), item)) {
                    pager.setCurrentItem(i, false);
                }
            }
            return true;
        });
    }
}
