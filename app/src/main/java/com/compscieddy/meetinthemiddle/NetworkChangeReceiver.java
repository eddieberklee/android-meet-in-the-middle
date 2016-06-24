//http://stackoverflow.com/questions/8412714/broadcastreceiver-receives-multiple-identical-messages-for-one-event
//possible solution for the double onRecieve call

package com.compscieddy.meetinthemiddle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Darren on 24-Jun-16.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

  private Context mContext;

  @Override
  public void onReceive(Context context, Intent intent) {
    mContext = context;
    if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
      Log.d("Internet connected = ", "" + isInternetAvailable(mContext));
    }
    isInternetAvailable(context);

  }

  public boolean isInternetAvailable(Context context) {
    ConnectivityManager connMgr = (ConnectivityManager)
        context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    return (networkInfo != null && networkInfo.isConnected());
  }
}