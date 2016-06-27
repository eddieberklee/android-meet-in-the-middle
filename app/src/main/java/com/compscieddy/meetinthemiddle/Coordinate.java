package com.compscieddy.meetinthemiddle;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by elee on 6/6/16.
 */
public class Coordinate {

  private static final Lawg lawg = Lawg.newInstance(Coordinate.class.getSimpleName());

  public double lat;
  public double lon;

  public Coordinate() {
    lat = -1;
    lon = -1;
  }

  public Coordinate(double lat, double lon) {
    set(lat, lon);
  }

  public void set(double lat, double lon) {
    this.lat = lat;
    this.lon = lon;
  }

  public LatLng getLatLng() {
    return new LatLng(lat, lon);
  }

}
