package com.compscieddy.meetinthemiddle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by elee on 6/30/16.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

  private Context mContext;

  @Override
  public void onReceive(Context context, Intent intent) {
    mContext = context;
  }

  public boolean isInternetAvailable() {
    ConnectivityManager connMgr = (ConnectivityManager)
        mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    return (networkInfo != null && networkInfo.isConnected());
  }

}
