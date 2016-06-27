package com.compscieddy.meetinthemiddle;

import android.app.Application;
import android.content.Context;


/**
 * Created by elee on 6/18/16.
 * http://stackoverflow.com/a/9445685/4326052
 */
public class MitmApplication extends Application {

  private static final Lawg lawg = Lawg.newInstance(MitmApplication.class.getSimpleName());

  private static MitmApplication instance;

  public static MitmApplication getInstance() {
    return instance;
  }

  public static Context getContext() {
    return instance;
  }

  @Override
  public void onCreate() {
    instance = this;
    super.onCreate();
  }

}
