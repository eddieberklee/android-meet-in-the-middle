package com.compscieddy.meetinthemiddle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.compscieddy.meetinthemiddle.R;

/**
 * Created by Ant Henderson on 6/15/16.
 * anthenderson89@gmail.com
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, OnboardingActivity.class);
        startActivity(intent);
        finish();
    }
}
