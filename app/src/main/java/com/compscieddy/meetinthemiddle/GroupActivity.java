package com.compscieddy.meetinthemiddle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.eddie_utils.Lawg;
import com.compscieddy.meetinthemiddle.model.Group;
import com.compscieddy.meetinthemiddle.model.UserMarker;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;

public class GroupActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, GoogleMap.OnMapClickListener {

  private static final Lawg lawg = Lawg.newInstance(GroupActivity.class.getSimpleName());

  private GoogleMap mMap;
  private final int LOCATION_REQUEST_CODE = 1;
  private Handler mHandler;

  private Coordinate mLastKnownCoord = new Coordinate();
  private boolean mIsLocationPermissionEnabled = false;

  private final int ANIMATE_CAMERA_REPEAT = 2000;

  public static final String ARG_GROUP_KEY = "group_id_key";
  private String mGroupKey;
  private Group mGroup;

  private LocationManager mLocationManager;
  private GoogleApiClient mGoogleApiClient;
  private Map<String, Marker> mMarkers = new HashMap<>();

  private final String UUID_KEY = "UUID_KEY"; // Temporary way to identify different users or different installations
  private Location mLastLocation;
  private String mUUID;

  @Bind(R.id.group_edit_text) EditText mGroupEditText;
  @Bind(R.id.group_text_view) TextView mGroupTextView;
  @Bind(R.id.group_set_button) TextView mSetButton;
  @Bind(R.id.invite_button) TextView mInviteButton;
  @Bind(R.id.invite_button_two) TextView mInviteButtonTwo;
  @Bind(R.id.expand_chat_fab) ImageView mExpandButton;
  @Bind(R.id.bottom_section) RelativeLayout mBottomSection;
  @Bind(R.id.location_marker) ImageView mLocationArrow;
  @Bind(R.id.viewpager) ViewPager mViewPager;
  @Bind(R.id.sliding_tabs) TabLayout mTabLayout;

  boolean expanded = false;
  boolean voteLocationActive = false;

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
      if (latLng.latitude != -1 && latLng.longitude != -1 && zoom < 13) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
      }

      mHandler.postDelayed(mAnimateCameraRunnable, ANIMATE_CAMERA_REPEAT);
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group);
    ButterKnife.bind(GroupActivity.this);

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    SharedPreferences sharedPreferences = GroupActivity.this.getSharedPreferences(
        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    mUUID = sharedPreferences.getString(UUID_KEY, null);
    if (mUUID == null) {
      SharedPreferences.Editor editor = sharedPreferences.edit();
      mUUID = UUID.randomUUID().toString();
      editor.putString(UUID_KEY, mUUID);
      editor.apply();
    }

    Bundle args = getIntent().getExtras();
    if (args != null) {
      mGroupKey = args.getString(ARG_GROUP_KEY);
      FirebaseDatabase database = FirebaseDatabase.getInstance();
      DatabaseReference groupReference = database.getReference("groups").child(mGroupKey);
      groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          mGroup = dataSnapshot.getValue(Group.class);
          if (mGroup == null) {
            Etils.logAndToast(GroupActivity.this, lawg, "Group is null - shit is so wrong");
            return;
          }
          mGroupTextView.setText(mGroup.groupTitle);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
          lawg.e("onCancelled() " + databaseError);
        }
      });
    }

    mGroupEditText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 1) {
          mSetButton.setText(getString(R.string.set_group_title_editing));
        } else {
          mSetButton.setText(getString(R.string.cancel_group_title_editing));
        }
      }

      @Override
      public void afterTextChanged(Editable s) {}
    });

    mHandler = new Handler(Looper.getMainLooper());
    // TODO: let's turn off this zooming animation for now
    // mHandler.postDelayed(mAnimateCameraRunnable, ANIMATE_CAMERA_REPEAT);

    if (mGoogleApiClient == null) {
      mGoogleApiClient = new GoogleApiClient.Builder(GroupActivity.this)
          .addConnectionCallbacks(GroupActivity.this)
          .addOnConnectionFailedListener(GroupActivity.this)
          .addApi(LocationServices.API)
          .build();
    }

    mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    int locationPermissionCheck = ContextCompat.checkSelfPermission(GroupActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
    if (locationPermissionCheck == PackageManager.PERMISSION_GRANTED) {
      initLocationPermissionGranted();
    } else {
      requestLocationPermission();
    }

    mExpandButton.setImageResource(R.drawable.ic_expand_less_black_48dp);

    mViewPager.setAdapter(new GroupFragmentPagerAdapter(getSupportFragmentManager(), GroupActivity.this));

    setupTabLayout();
    setListeners();

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
      mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 10, GroupActivity.this);
    } catch (SecurityException se) {
      lawg.e("se: " + se);
    }
  }

  private void requestLocationPermission() {
    ActivityCompat.requestPermissions(GroupActivity.this, new String[]{
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

    LatLng sydney = new LatLng(-34, 151);
    Marker sydneyMarker = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
    mMarkers.put(UUID.randomUUID().toString(), sydneyMarker);
    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    LatLng queenstown = new LatLng(-45, 168);
    Marker queenstownMarker = mMap.addMarker(new MarkerOptions().position(queenstown).title("Marker in Queenstown"));
    mMarkers.put(UUID.randomUUID().toString(), queenstownMarker);
    mMap.moveCamera(CameraUpdateFactory.newLatLng(queenstown));

    initMarkers();
  }

  private void initMarkers() {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference markerReference = database.getReference("markers");
    markerReference.addChildEventListener(new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        UserMarker userMarker = dataSnapshot.getValue(UserMarker.class);
        lawg.d("initMarkers() onChildAdded() " + " dataSnapshot: " + dataSnapshot + " userMarker: " + userMarker);
        String userUUID = userMarker.userUUID;
        LatLng latLng = userMarker.getLatLng();
        if (mMarkers.containsKey(userUUID)) {
          Marker existingMarker = mMarkers.get(userUUID);
          existingMarker.setPosition(latLng);
        } else {
          Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(userUUID));
          mMarkers.put(userUUID, marker);
        }

        // http://stackoverflow.com/a/14828739/4326052
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker m : mMarkers.values()) {
          builder.include(m.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = Etils.dpToPx(50);
        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
          @Override
          public void onMapLoaded() {
            mMap.animateCamera(cameraUpdate);
          }
        });

        // TODO: Google maps bounds need to be extended here
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        // Implement so that if marker location is changed, the appropriate marker gets updated
      }

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        lawg.e("Firebase Error onCancelled() [" + databaseError.getCode() + "] " + databaseError.getMessage() + databaseError.getDetails());
      }
    });

    mMap.setOnMapClickListener(this);

  }

  private void setListeners() {
    mGroupTextView.setOnClickListener(this);
    mSetButton.setOnClickListener(this);
    mInviteButton.setOnClickListener(this);
    mInviteButtonTwo.setOnClickListener(this);
    mExpandButton.setOnClickListener(this);
    mLocationArrow.setOnClickListener(this);
  }

  private void setupTabLayout() {
    mTabLayout.setupWithViewPager(mViewPager);
    mTabLayout.getTabAt(0).setIcon(R.drawable.ic_message_text_grey600_48dp);
    mTabLayout.getTabAt(1).setIcon(R.drawable.ic_magnify_grey600_48dp);
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    lawg.d("GoogleApiClient onConnected()");
    try {
      mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
      if (mLastLocation != null) {
        double latitude = mLastLocation.getLatitude();
        double longitude = mLastLocation.getLongitude();
        mLastKnownCoord.set(latitude, longitude);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference markerReference = database.getReference("markers");
        UserMarker userMarker = new UserMarker(mUUID, latitude, longitude);
        markerReference.child(mUUID).setValue(userMarker);

//        if (mCurrentMarker != null) mCurrentMarker.remove();

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
            R.drawable.ic_darren);
        Bitmap croppedIcon = Util.getCroppedBitmap(GroupActivity.this, icon);

        // TODO: Don't add current marker, just update Firebase to make it do it for you
        // mCurrentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location").icon(BitmapDescriptorFactory.fromBitmap(croppedIcon)));

/*        //This adds the outer lines only
        mMap.addPolyline(new PolylineOptions()
            .add(new LatLng(-34, 151), new LatLng(-45, 168), new LatLng(mLastKnownCoord.lat, mLastKnownCoord.lon), new LatLng(-34, 151))
            .width(5)
            .color(Color.RED));*/

        PolygonOptions rectOptions = new PolygonOptions()
            .add(new LatLng(-34, 151),
                new LatLng(-45, 168),
                new LatLng(mLastKnownCoord.lat, mLastKnownCoord.lon),
                new LatLng(-34, 151))
            .strokeColor(Color.RED)
            .strokeWidth(4)
            .fillColor(getResources().getColor(R.color.flatui_red_1_transp_50));

        // Get back the mutable Polygon
        mMap.addPolygon(rectOptions);
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

  //Toasts the tapped points coords
  @Override
  public void onMapClick(LatLng point) {
    Etils.showToast(this, "Tapped point is: " + point);

    VisibleRegion vRegion = mMap.getProjection().getVisibleRegion();
    LatLng upperLeft = vRegion.farLeft;
    LatLng lowerRight = vRegion.nearRight;
    //Logs the visible area of the map
    lawg.d("Top left = " + upperLeft + " and Bottom right = " + lowerRight);

  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.group_text_view:
        mGroupEditText.setVisibility(View.VISIBLE);
        mGroupTextView.setVisibility(View.INVISIBLE);
        mSetButton.setVisibility(View.VISIBLE);
        mGroupEditText.requestFocus();
        mGroupEditText.setText("");
        mSetButton.setText(getString(R.string.cancel_group_title_editing));
        break;

      case R.id.group_set_button:
        mGroupEditText.setVisibility(View.INVISIBLE);
        mGroupTextView.setVisibility(View.VISIBLE);
        mSetButton.setVisibility(View.INVISIBLE);

        //name will need to be saved as a shared preference or in database
        mGroupTextView.setText(mGroupEditText.getText());
        break;

      case R.id.invite_button:
        PopupMenu popupMenu = new PopupMenu(this, mInviteButton);
        popupMenu.getMenuInflater().inflate(R.menu.menu_popup_invite, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
          @Override
          public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
              case R.id.invite_members:
                return true;
              case R.id.share_facebook:
               // TODO HANDLE SHARING GROUP URL VIA FACEBOOK
                //Invites through Facebook Messenger
/*        ShareLinkContent content = new ShareLinkContent.Builder()
            .setContentUrl(Uri.parse("https://developers.facebook.com"))
            .build();
        MessageDialog.show(this, content);*/


                String appLinkUrl, previewImageUrl;

                appLinkUrl = "https://www.facebook.com/";
                previewImageUrl = "https://scontent-syd1-1.xx.fbcdn.net/v/t1.0-9/1479360_10151708109576856_405696712_n.jpg?oh=c6bd6367b90cf0f5f52a25217bc753d2&oe=57CDED70";

                if (AppInviteDialog.canShow()) {
                  AppInviteContent content = new AppInviteContent.Builder()
                      .setApplinkUrl(appLinkUrl)
                      .setPreviewImageUrl(previewImageUrl)
                      .build();
                  AppInviteDialog.show(GroupActivity.this, content);
                }
                return true;
              case R.id.share_link:
                shareLinkClicked();
                return true;
              default:
                return false;
            }
          }
        });
        popupMenu.show();
        break;

      case R.id.expand_chat_fab:
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;

        ResizeAnimation resizeAnimation;

        // todo: use Etils.getScreenHeight() instead

        if (!expanded) {
          Util.rotateFabForward(mExpandButton);
          resizeAnimation = new ResizeAnimation(
              mBottomSection,
              (int) (height * 0.75),
              getResources().getDimensionPixelSize(R.dimen.group_bottom_section_starting_height)
          );
        } else {
          Util.rotateFabBackward(mExpandButton);

          resizeAnimation = new ResizeAnimation(
              mBottomSection,
              getResources().getDimensionPixelSize(R.dimen.group_bottom_section_starting_height),
              (int) (height * 0.75)
          );
        }
        expanded = !expanded;
        resizeAnimation.setDuration(400);
        mBottomSection.startAnimation(resizeAnimation);
        break;

      case R.id.invite_button_two:
      case R.id.location_marker:
        if (!voteLocationActive) {
          Util.rotateLocationActive(mLocationArrow);

          // TODO: PARSE THE DATA INTO MORE USEFUL FORMAT IN ANOTHER FRAME (POSSIBLY RECYCLEVIEW)

          Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
              YelpAPIFactory apiFactory = new YelpAPIFactory(
                  getString(R.string.yelp_consumer_key),
                  getString(R.string.yelp_consumer_secret),
                  getString(R.string.yelp_token),
                  getString(R.string.yelp_token_secret));
              YelpAPI yelpAPI = apiFactory.createAPI();

              Map<String, String> params = new HashMap<>();

              // general params
              params.put("term", "food");
              params.put("limit", "3");

              // locale params
              params.put("lang", "en");

              //Call<SearchResponse> call = yelpAPI.search("San Francisco", params);

/*              // bounding box
              BoundingBoxOptions bounds = BoundingBoxOptions.builder()
                  .swLatitude(37.7577)
                  .swLongitude(-122.4376)
                  .neLatitude(37.785381)
                  .neLongitude(-122.391681).build();
              Call<SearchResponse> call = yelpAPI.search(bounds, params);*/

              // coordinates
              CoordinateOptions coordinate = CoordinateOptions.builder()
                  .latitude(mLastKnownCoord.lat)
                  .longitude(mLastKnownCoord.lon).build();
              Call<SearchResponse> call = yelpAPI.search(coordinate, params);

              try {
                //Response<SearchResponse> response = call.execute();
                //lawg.d("Yelp Response: " + response.body());

                SearchResponse searchResponse = call.execute().body();

                //int totalNumberOfResult = searchResponse.total();  // 13297 for some reason

                for (int i = 0; i < params.size(); i++) { //or can use Integer.parseInt(params.get("limit")
                  ArrayList<Business> businesses = searchResponse.businesses();
                  String businessName = businesses.get(i).name();  // "JapaCurry Truck"
                  Double rating = businesses.get(i).rating();  // 4.0
                  lawg.d("Yelp Business: " + businessName);
                  lawg.d("Yelp Rating: " + rating);
                }


              } catch (IOException e) {
                lawg.e(e.toString());
              }
            }
          });

          thread.start();

        } else {
          Util.rotateLocationInactive(mLocationArrow);
        }
        voteLocationActive = !voteLocationActive;
        break;

    }

  }

  private void shareLinkClicked(){
    Intent shareLinkIntent = new Intent(Intent.ACTION_SEND);
    shareLinkIntent.setType("text/plain");
    shareLinkIntent.putExtra(Intent.EXTRA_TEXT, "Testing");
    // TODO CREATE UNIQUE URL FOR GROUP
    startActivity(Intent.createChooser(shareLinkIntent, "Share link using"));
  }

  public class GroupFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    final int CHAT_FRAGMENT = 0;
    final int SEARCH_FRAGMENT = 1;
    private Context context;

    public GroupFragmentPagerAdapter(FragmentManager fm, Context context) {
      super(fm);
      this.context = context;
    }

    @Override
    public int getCount() {
      return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
      switch (position) {
        case CHAT_FRAGMENT:
          return ChatFragment.newInstance(mGroupKey);
        case SEARCH_FRAGMENT:
          return SearchFragment.newInstance();
      }
      return null;
    }
  }
}
