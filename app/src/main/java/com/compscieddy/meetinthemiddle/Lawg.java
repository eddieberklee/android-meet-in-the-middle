package com.compscieddy.meetinthemiddle;

import android.util.Log;

import java.util.Map;
import java.util.Set;

/**
 * Created by elee on 2/10/16.
 * Simple wrapper around `Log` but it appends an app-specific code to the TAG because even
 * if you filter by "show only selected application" in the Android Monitor logcat, it will show you
 * other system classes' debug statements (theory: it filters by process id but not only your code
 * is running on the process).
 */
public class Lawg {

  private String TAG;
  /** Depending on your regex or your logcat filter settings, it may not be enough to just use
   *  your application or package name.
   */
  private static final String UNIQUE_STRING = "mitm";

  public static Lawg newInstance(String className) {
    return new Lawg(UNIQUE_STRING + className);
  }

  // private to force use of newInstance()
  private Lawg(String TAG) {
    this.TAG = TAG;
  }

  private StringBuilder setToStringBuilder(Set<String> set) {
    StringBuilder builder = new StringBuilder();
    for (String s : set) {
      builder.append(s);
      builder.append(", ");
    }
    builder.delete(builder.length() - 2, builder.length());
    return builder;
  }
  private StringBuilder floatArrayToStringBuilder(float[] array) {
    StringBuilder builder = new StringBuilder();
    for (float f : array) {
      builder.append(f);
      builder.append(", ");
    }
    builder.delete(builder.length() - ", ".length(), builder.length());
    return builder;
  }
  private StringBuilder mapToStringBuilder(Map<String, Boolean> map) {
    StringBuilder builder = new StringBuilder();
    for (String key : map.keySet()) {
      builder.append("(" + key + ", " + map.get(key) + ")");
    }
    return builder;
  }

  public void d(String message) {
    Log.d(TAG, message);
  }
  public void d(float[] array) {
    StringBuilder builder = floatArrayToStringBuilder(array);
    Log.d(TAG, builder.toString());
  }
  public void d(Set<String> set) {
    StringBuilder builder = setToStringBuilder(set);
    Log.d(TAG, builder.toString());
  }
  public void d(Map<String, Boolean> map) {
    StringBuilder builder = mapToStringBuilder(map);
    Log.d(TAG, builder.toString());
  }


  public void e(String message) {
    Log.e(TAG, message);
  }
  public void e(float[] array) {
    StringBuilder builder = floatArrayToStringBuilder(array);
    Log.e(TAG, builder.toString());
  }
  public void e(Set<String> set) {
    StringBuilder builder = setToStringBuilder(set);
    Log.e(TAG, builder.toString());
  }
  public void e(Map<String, Boolean> map) {
    StringBuilder builder = mapToStringBuilder(map);
    Log.e(TAG, builder.toString());
  }

}