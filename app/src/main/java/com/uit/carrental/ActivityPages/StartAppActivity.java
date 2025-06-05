package com.uit.carrental.ActivityPages;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.uit.carrental.Adapter.SplashPagerAdapter;
import com.uit.carrental.FragmentPages.SplashFragment;
import com.uit.carrental.R;
import com.uit.carrental.Service.UserAuthentication.LoginActivity;

public class StartAppActivity extends AppCompatActivity implements SplashFragment.OnNextButtonClickListener {

    private ViewPager2 viewPager;
    private static final int TOTAL_SPLASHES = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        viewPager = findViewById(R.id.splash_viewpager);
        SplashPagerAdapter adapter = new SplashPagerAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.setPageTransformer((page, position) -> {
            page.setAlpha(1 - Math.abs(position));
            page.setTranslationX(-position * page.getWidth());
        });
    }

    @Override
    public void onNextButtonClicked(int position) {
        if (position < TOTAL_SPLASHES - 1) {
            viewPager.setCurrentItem(position + 1, true);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
            finish();
        }
    }
}