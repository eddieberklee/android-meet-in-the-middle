package com.compscieddy.meetinthemiddle;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by ambar on 6/22/16.
 */
public class InternetErrorFragment extends DialogFragment {

  IntentFilter intentFilter;
  NetworkChangeReceiver netWorkChangeReceiver;

  public static InternetErrorFragment newInstance() {

    Bundle args = new Bundle();

    InternetErrorFragment fragment = new InternetErrorFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_internet_error, container);
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
    netWorkChangeReceiver = new NetworkChangeReceiver();
    getActivity().registerReceiver(netWorkChangeReceiver, intentFilter);

    super.onResume();
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    final Activity activity = getActivity();
    if (activity instanceof DialogInterface.OnDismissListener){
      ((DialogInterface.OnDismissListener) activity).onDismiss(getDialog());
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    getActivity().unregisterReceiver(netWorkChangeReceiver);
  }

  public class NetworkChangeReceiver extends BroadcastReceiver {

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
      mContext = context;
      if (isInternetAvailable()) {
        getDialog().dismiss();
      }
    }

    private boolean isInternetAvailable() {
      ConnectivityManager connMgr = (ConnectivityManager)
          mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
      return (networkInfo != null && networkInfo.isConnected());
    }
  }
}
