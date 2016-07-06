package com.compscieddy.meetinthemiddle.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.compscieddy.meetinthemiddle.R;

/**
 * Created by Ant Henderson on 6/15/16.
 * anthenderson89@gmail.com
 */
public class OnboardingFragment1 extends Fragment {


    public OnboardingFragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.onboarding_screen1, container, false);
    }

}
