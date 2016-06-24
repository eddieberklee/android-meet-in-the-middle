package com.compscieddy.meetinthemiddle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Darren on 24-Jun-16.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

  private Context mContext;

  @Override
  public void onReceive(Context context, Intent intent) {
    mContext = context;
    if (isInternetAvailable(mContext)) {

    }
  }
  public boolean isInternetAvailable(Context context) {
    ConnectivityManager connMgr = (ConnectivityManager)
        context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    return (networkInfo != null && networkInfo.isConnected());
  }
}