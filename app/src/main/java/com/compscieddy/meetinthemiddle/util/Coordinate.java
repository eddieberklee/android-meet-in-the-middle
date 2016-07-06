package com.compscieddy.meetinthemiddle.util;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by elee on 6/6/16.
 */
public class Coordinate implements Serializable {

  private static final Lawg L = Lawg.newInstance(Coordinate.class.getSimpleName());

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
