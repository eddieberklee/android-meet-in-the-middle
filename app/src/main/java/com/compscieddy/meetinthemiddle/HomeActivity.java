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
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.eddie_utils.Lawg;
import com.compscieddy.meetinthemiddle.model.Group;
import com.compscieddy.meetinthemiddle.model.User;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.fondesa.recyclerviewdivider.RecyclerViewDivider;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import butterknife.Bind;
import butterknife.ButterKnife;


public class HomeActivity extends BaseActivity implements OnMapReadyCallback, LocationListener,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

  private static final Lawg lawg = Lawg.newInstance(HomeActivity.class.getSimpleName());
  int count = 0;
  int initialVerticalOffset = 0;
  int finalVerticalOffset;
  private GoogleMap mMap;
  private final int LOCATION_REQUEST_CODE = 1;
  private Handler mHandler;

  private Coordinate mLastKnownCoord = new Coordinate();
  private boolean mIsLocationPermissionEnabled = false;

  private final int ANIMATE_CAMERA_REPEAT = 1500;

  private LocationManager mLocationManager;
  private GoogleApiClient mGoogleApiClient;
  private Marker mCurrentMarker;

  @Bind(R.id.username) TextView mUsername;
  @Bind(R.id.group_recycler_view) RecyclerView mGroupRecyclerView;
  @Bind(R.id.app_bar_layout) AppBarLayout mAppBarLayout;
  @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
  @Bind(R.id.map_card_view) CardView mMapCardView;
  @Bind(R.id.toolbar_viewgroup) ViewGroup mToolbarLayout;
  @Bind(R.id.new_group_button) View mNewGroupButton;
  @Bind(R.id.logout_button) View mLogoutButton;

  private SupportMapFragment mMapFragment;
  private Location mLastLocation;
  private GroupsAdapter mGroupsAdapter;

  @Bind(R.id.status_recycler_view) RecyclerView mStatusRecyclerView;
  private StatusAdapter mStatusAdapter;

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
      if (latLng.latitude != -1 && latLng.longitude != -1 && zoom < 9) {
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
    MapsInitializer.initialize(this);
    mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mMapFragment.getMapAsync(this);

    String displayName = mFirebaseUser.getDisplayName();
    String email = mFirebaseUser.getEmail();
    mUsername.setText(displayName + "\n" + email);

    FacebookSdk.sdkInitialize(getApplicationContext());
    AppEventsLogger.activateApp(this);

    mHandler = new Handler(Looper.getMainLooper());
    mHandler.postDelayed(mAnimateCameraRunnable, ANIMATE_CAMERA_REPEAT);

    if (mGoogleApiClient == null) {
      mGoogleApiClient = new GoogleApiClient.Builder(HomeActivity.this)
          .addConnectionCallbacks(HomeActivity.this)
          .addOnConnectionFailedListener(HomeActivity.this)
          .addApi(LocationServices.API)
          .addApi(AppInvite.API)
          .enableAutoManage(this, this)
          .build();
    }

    // Check if this app was launched from a deep link. Setting autoLaunchDeepLink to true
    // would automatically launch the deep link if one is found.
    boolean autoLaunchDeepLink = false;
    AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
        .setResultCallback(
            new ResultCallback<AppInviteInvitationResult>() {
              @Override
              public void onResult(@NonNull AppInviteInvitationResult result) {
                if (result.getStatus().isSuccess()) {
                  // Extract deep link from Intent
                  Intent intent = result.getInvitationIntent();
                  String deepLink = AppInviteReferral.getDeepLink(intent);

                  // Handle the deep link, that is, open the
                  // group saved in the deep link, and start
                  // corresponding GroupActivity.

                }
              }
            });


    mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    int locationPermissionCheck = ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
    if (locationPermissionCheck == PackageManager.PERMISSION_GRANTED) {
      initLocationPermissionGranted();
    } else {
      requestLocationPermission();
    }

    setListeners();
    setupRecyclerView();
  }

  private void setListeners() {
    mAppBarLayout.addOnOffsetChangedListener(this);
    mNewGroupButton.setOnClickListener(this);
    mLogoutButton.setOnClickListener(this);
  }

  @Override
  public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
    finalVerticalOffset = verticalOffset;
    if (finalVerticalOffset < initialVerticalOffset){
      // we are scrolling down
      count++;
      if (count == 1) {
        Animation animation = new ScaleAnimation(1.0f, 0.5f, 1.0f, 0.5f, 0.5f, 0.5f);
        animation.setDuration(500);
        animation.setFillAfter(true);
        mToolbarLayout.startAnimation(animation);
      }

    } else if (finalVerticalOffset > initialVerticalOffset){
      // we are scrolling up
      count++;
      if (count == 1) {
        Animation animation = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, 0.5f, 0.5f);
        animation.setDuration(500);
        animation.setFillAfter(true);
        mToolbarLayout.startAnimation(animation);
      }
    }
    initialVerticalOffset = finalVerticalOffset;
    count = 0;
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
        Bitmap resizedIcon = Bitmap.createScaledBitmap(icon, icon.getWidth() * 2, icon.getHeight() * 2, true);
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
    mGroupRecyclerView.setAdapter(mGroupsAdapter);
    mGroupRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//    RecyclerViewDivider.with(this).addTo(mGroupRecyclerView).marginSize(Etils.dpToPx(5)).build().attach();

    mStatusAdapter = new StatusAdapter();
    mStatusAdapter.setClickListener(new StatusAdapter.ClickListener() {
      @Override
      public void OnItemClick(View v) {
        //set status
      }
    });

    mStatusRecyclerView.setAdapter(mStatusAdapter);
    mStatusRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    RecyclerViewDivider.with(this).addTo(mStatusRecyclerView).marginSize(Etils.dpToPx(5)).build().attach();
    mGroupRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
  }

  @Override
  public void userIsReady() {
    super.userIsReady();
    initFirebaseData();
  }

  private void initFirebaseData() {

    mFirebaseDatabase.getReference("users").child(mUser.getKey()).child("groups").addChildEventListener(new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        String groupKey = dataSnapshot.getKey();
        mFirebaseDatabase.getReference("groups").child(groupKey).addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            Group group = dataSnapshot.getValue(Group.class);
            mGroupsAdapter.addGroup(group);
          }

          @Override
          public void onCancelled(DatabaseError databaseError) { lawg.e("onCancelled " + databaseError); }
        });

        mFirebaseDatabase.getReference("groups").child(groupKey).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            Group updatedGroup = dataSnapshot.getValue(Group.class);
            mGroupsAdapter.updateGroup(updatedGroup);
          }

          @Override
          public void onCancelled(DatabaseError databaseError) { lawg.e("onCancelled() " + databaseError); }
        });
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        lawg.e(" dataSnapshot: " + dataSnapshot);
        Group group = dataSnapshot.getValue(Group.class);
        lawg.e(" group: " + group);
        mGroupsAdapter.removeGroup(group);
      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

      @Override
      public void onCancelled(DatabaseError databaseError) {
        lawg.e("onCancelled " + databaseError);
      }
    });

  }

  @Override
  public void onClick(View v) {
    int viewId = v.getId();
    switch (viewId) {
      case R.id.new_group_button: {
        Intent intent = new Intent(HomeActivity.this, GroupActivity.class);
        DatabaseReference newGroupReference = mFirebaseDatabase.getReference("groups").push();

        final String newGroupKey = newGroupReference.getKey();
        Group newGroup = new Group(newGroupKey, null, null);
        newGroupReference.setValue(newGroup);
        mUser.addGroup(newGroupKey);
        mUser.update();
        // Just while testing, add everyone to every group
        mFirebaseDatabase.getReference("users").addChildEventListener(new ChildEventListener() {
          @Override
          public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            User user = dataSnapshot.getValue(User.class);
            lawg.e("onChildAdded " + " user: " + user);
            if (user != null) {
              lawg.e("name: " + user.name);
            }
            if (!user.getKey().equals(mUser.getKey())) {
              user.addGroup(newGroupKey);
              user.update();
            }
          }

          @Override
          public void onChildChanged(DataSnapshot dataSnapshot, String s) {

          }

          @Override
          public void onChildRemoved(DataSnapshot dataSnapshot) {

          }

          @Override
          public void onChildMoved(DataSnapshot dataSnapshot, String s) {

          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
        });

        intent.putExtra(GroupActivity.ARG_GROUP_KEY, newGroupKey);
        startActivity(intent);
        break;
      }
      case R.id.logout_button: {
        mFirebaseAuth.signOut();
        Intent intent = new Intent(HomeActivity.this, AuthenticationActivity.class);
        startActivity(intent);
        break;
      }
    }
  }
}
