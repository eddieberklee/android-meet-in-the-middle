package com.compscieddy.meetinthemiddle;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ambar on 6/22/16.
 */
public class InternetErrorFragment extends DialogFragment implements View.OnClickListener {

  IntentFilter intentFilter;
  NetworkChangeReceiver networkChangeReceiver;

  @Bind(R.id.goto_wifi_settings_button) View mGotoWifiSettingsButton;

  public static InternetErrorFragment newInstance() {
    Bundle args = new Bundle();
    InternetErrorFragment fragment = new InternetErrorFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_internet_error, container);
    ButterKnife.bind(InternetErrorFragment.this, rootView);
    setListeners();
    return rootView;
  }

  private void setListeners() {
    mGotoWifiSettingsButton.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch(v.getId()) {
      case R.id.goto_wifi_settings_button:
        startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
        break;
    }
  }

  @Override
  public void onResume() {
    // Get existing layout params for the window
    ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
    // Assign window properties to fill the parent
    params.width = WindowManager.LayoutParams.MATCH_PARENT;
    params.height = WindowManager.LayoutParams.MATCH_PARENT;
    getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

    intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    networkChangeReceiver = new NetworkChangeReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (this.isInternetAvailable()) {
          getDialog().dismiss();
        }
      }
    };
    getActivity().registerReceiver(networkChangeReceiver, intentFilter);

    super.onResume();
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    Activity activity = getActivity();
    if (activity instanceof DialogInterface.OnDismissListener){
      ((DialogInterface.OnDismissListener) activity).onDismiss(getDialog());
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    Activity activity = getActivity();
    if (activity != null) activity.unregisterReceiver(networkChangeReceiver);
  }

}
