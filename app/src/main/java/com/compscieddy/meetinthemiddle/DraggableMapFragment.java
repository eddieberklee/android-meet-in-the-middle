package com.compscieddy.meetinthemiddle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by ambar on 6/23/16.
 */
public class DraggableMapFragment extends SupportMapFragment {
  public View mOriginalContentView;
  public TouchableWrapper mTouchView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    mOriginalContentView = super.onCreateView(inflater, parent, savedInstanceState);
    mTouchView = new TouchableWrapper(getActivity());
    mTouchView.addView(mOriginalContentView);
    return mTouchView;
  }

  @Override
  public View getView() {
    return mOriginalContentView;
  }
}
