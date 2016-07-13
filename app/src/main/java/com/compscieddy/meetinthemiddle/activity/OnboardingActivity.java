package com.compscieddy.meetinthemiddle.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.compscieddy.meetinthemiddle.R;
import com.compscieddy.meetinthemiddle.fragment.OnboardingFragment1;
import com.compscieddy.meetinthemiddle.fragment.OnboardingFragment2;
import com.compscieddy.meetinthemiddle.fragment.OnboardingFragment3;

/**
 * Created by Ant Henderson on 6/15/16.
 * anthenderson89@gmail.com
 */
public class OnboardingActivity extends FragmentActivity {
    private ViewPager pager;
    private FloatingActionButton onboardingFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        pager = (ViewPager)findViewById(R.id.onboardingPager);
        onboardingFab = (FloatingActionButton)findViewById(R.id.onboardingFab);

        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0 : return new OnboardingFragment1();
                    case 1 : return new OnboardingFragment2();
                    case 2 : return new OnboardingFragment3();
                    default: return null;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
        pager.setAdapter(adapter);
        onboardingFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pager.getCurrentItem() == 2) { // The last screen
                    finishOnboarding();
                } else {
                    pager.setCurrentItem(
                            pager.getCurrentItem() + 1,
                            true
                    );
                }
            }
        });

    }

    private void finishOnboarding() {
        // Get the shared preferences

        // Launch the main Activity, called MainActivity
        Intent main = new Intent(this, AuthenticationActivity.class);
        startActivity(main);

        // Close the OnboardingActivity
        finish();
    }

}
