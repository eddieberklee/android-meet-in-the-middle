package com.compscieddy.meetinthemiddle.model;

import com.compscieddy.meetinthemiddle.Lawg;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by elee on 6/7/16.
 */
@IgnoreExtraProperties
public class UserMarker {

  private static final Lawg lawg = Lawg.newInstance(UserMarker.class.getSimpleName());

  public String userUUID;
  public double latitude;
  public double longitude;

  public UserMarker() {}

  public UserMarker(String userUUID, double latitude, double longitude) {
    this.userUUID = userUUID;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public LatLng getLatLng() {
    return new LatLng(latitude, longitude);
  }

}
