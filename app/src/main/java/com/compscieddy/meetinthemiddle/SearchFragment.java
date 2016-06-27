package com.compscieddy.meetinthemiddle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ambar on 6/12/16.
 */
public class SearchFragment extends Fragment {

  private static final Lawg lawg = Lawg.newInstance(SearchFragment.class.getSimpleName());

  public static SearchFragment newInstance() {

    Bundle args = new Bundle();
    //For future arguments, add here
    SearchFragment fragment = new SearchFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_search, container, false);

    return view;
  }
}
