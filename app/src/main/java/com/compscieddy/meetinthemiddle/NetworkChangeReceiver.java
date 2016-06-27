package com.compscieddy.meetinthemiddle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ambar on 6/24/16.
 */
abstract public class NetworkChangeReceiver extends BroadcastReceiver {

  public Context mContext;

  public boolean isInternetAvailable(Context context) {
    ConnectivityManager connMgr = (ConnectivityManager)
        context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    return (networkInfo != null && networkInfo.isConnected());
  }
}
