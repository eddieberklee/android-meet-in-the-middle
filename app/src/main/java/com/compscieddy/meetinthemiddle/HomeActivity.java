package com.compscieddy.meetinthemiddle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.compscieddy.eddie_utils.Lawg;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.Bind;
import butterknife.ButterKnife;


public class HomeActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

  private static final Lawg lawg = Lawg.newInstance(GroupActivity.class.getSimpleName());

  private GoogleMap mMap;
  private final int LOCATION_REQUEST_CODE = 1;
  private Handler mHandler;


  private Coordinate mLastKnownCoord = new Coordinate();
  private boolean mIsLocationPermissionEnabled = false;

  private final int ANIMATE_CAMERA_REPEAT = 1500;

  private LocationManager mLocationManager;
  private GoogleApiClient mGoogleApiClient;
  private Marker mCurrentMarker;

  @Bind(R.id.map) MapView mMapView;

  private Location mLastLocation;
  @Bind(R.id.group_recycler_view) RecyclerView mGroupRecyclerView;
  private GroupsAdapter mGroupsAdapter;

  private Runnable mAnimateCameraRunnable = new Runnable() {
    @Override
    public void run() {
      if (false) lawg.d("mAnimateCameraRunnable");

      if (!mIsLocationPermissionEnabled) {
        return;
      }

      float zoom = mMap.getCameraPosition().zoom;
      if (false) lawg.d(" zoom: " + zoom);

      LatLng latLng = mLastKnownCoord.getLatLng();
      if (latLng.latitude != -1 && latLng.longitude != -1 && zoom < 16) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
      }

      mHandler.postDelayed(mAnimateCameraRunnable, ANIMATE_CAMERA_REPEAT);
    }
  };

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    ButterKnife.bind(HomeActivity.this);
    mMapView.getMapAsync(this);
    MapsInitializer.initialize(this);
    mMapView.onCreate(savedInstanceState);

    mHandler = new Handler(Looper.getMainLooper());
    mHandler.postDelayed(mAnimateCameraRunnable, ANIMATE_CAMERA_REPEAT);

    if (mGoogleApiClient == null) {
      mGoogleApiClient = new GoogleApiClient.Builder(HomeActivity.this)
          .addConnectionCallbacks(HomeActivity.this)
          .addOnConnectionFailedListener(HomeActivity.this)
          .addApi(LocationServices.API)
          .build();
    }

    mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    int locationPermissionCheck = ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
    if (locationPermissionCheck == PackageManager.PERMISSION_GRANTED) {
      initLocationPermissionGranted();
    } else {
      requestLocationPermission();
    }

    setupRecyclerView();
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mMapView != null) {
      mMapView.onResume();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (mMapView != null) {
      mMapView.onPause();
    }
  }

  @Override
  public void onDestroy() {
    if (mMapView != null) {
      try {
        mMapView.onDestroy();
      } catch (NullPointerException e) {
        lawg.e("NPE:" + e);
      }
    }
    super.onDestroy();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    if (mMapView != null) {
      mMapView.onLowMemory();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (mMapView != null) {
      mMapView.onSaveInstanceState(outState);
    }
  }

  @Override
  protected void onStart() {
    mGoogleApiClient.connect();
    super.onStart();
  }

  @Override
  protected void onStop() {
    mGoogleApiClient.disconnect();
    super.onStop();
  }

  private void initLocationPermissionGranted() {
    try {
      mIsLocationPermissionEnabled = true;
      mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 10, HomeActivity.this);
    } catch (SecurityException se) {
      lawg.e("se: " + se);
    }
  }

  private void requestLocationPermission() {
    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{
        Manifest.permission.ACCESS_FINE_LOCATION
    }, LOCATION_REQUEST_CODE);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == LOCATION_REQUEST_CODE) {
      if (permissions.length == 1 && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION
          && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        initLocationPermissionGranted();
      }
    }
  }

  @Override
  public void onLocationChanged(Location location) {
    lawg.e("onLocationChanged");
    /*
    double latitude = location.getLatitude();
    double longitude = location.getLongitude();
    mLastKnownCoord.set(latitude, longitude);
    LatLng currentLatLng = new LatLng(latitude, longitude);
    mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
    mMap.animateCamera(CameraUpdateFactory.zoomIn());
    */
  }

  @Override
  public void onProviderEnabled(String provider) {

  }

  @Override
  public void onProviderDisabled(String provider) {

  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {

  }

  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera. In this case,
   * we just add a marker near Sydney, Australia.
   * If Google Play services is not installed on the device, the user will be prompted to install
   * it inside the SupportMapFragment. This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   */
  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    try {
      // "MyLocation" is the "blue dot" feature for showing the current location and jumping to the location
//      mMap.setMyLocationEnabled(true);
    } catch (SecurityException se) {
      lawg.e("se: " + se);
    }
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    try {
      mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
      if (mLastLocation != null) {
        double latitude = mLastLocation.getLatitude();
        double longitude = mLastLocation.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        mLastKnownCoord.set(latitude, longitude);
        if (mCurrentMarker != null) mCurrentMarker.remove();

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_darren);
        Bitmap resizedIcon = Bitmap.createScaledBitmap(icon, icon.getWidth()*2, icon.getHeight()*2, true);
        Bitmap croppedIcon = Util.getCroppedBitmap(HomeActivity.this, resizedIcon);

        mCurrentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location").icon(BitmapDescriptorFactory.fromBitmap(croppedIcon)));
      }
    } catch (SecurityException se) {
      lawg.e("se: " + se);
    }
  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }

  private void setupRecyclerView() {
    mGroupsAdapter = new GroupsAdapter();
    mGroupsAdapter.setClickListener(new GroupsAdapter.ClickListener() {
      @Override
      public void OnItemClick(View v) {
        Intent intent = new Intent(HomeActivity.this, GroupActivity.class);
        startActivity(intent);
      }
    });

    mGroupRecyclerView.setAdapter(mGroupsAdapter);
    mGroupRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
  }

  @Override
  public void onClick(View v) {


  }
}
