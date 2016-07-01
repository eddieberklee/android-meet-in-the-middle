package com.compscieddy.meetinthemiddle.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.compscieddy.meetinthemiddle.R;
import com.compscieddy.meetinthemiddle.activity.GroupActivity;
import com.compscieddy.meetinthemiddle.util.Lawg;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by ambar on 6/12/16.
 */
public class DiscoverFragment extends Fragment {

  private static final Lawg lawg = Lawg.newInstance(DiscoverFragment.class.getSimpleName());

  private static final String ARG_LAST_KNOWN_COORD = "arg_last_known_coord";
  public WeakReference<GroupActivity> mGroupActivityWeakReference;

  public static DiscoverFragment newInstance(GroupActivity groupActivity) {
    Bundle args = new Bundle();
    DiscoverFragment fragment = new DiscoverFragment();
    fragment.mGroupActivityWeakReference = new WeakReference<GroupActivity>(groupActivity);
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_discover, container, false);

    initYelp();

    return view;
  }

  private void initYelp() {
    // todo: should be AsyncTask instead
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
        params.put("limit", "5");

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
        if (mGroupActivityWeakReference.get() == null) {
          lawg.e("GroupActivity WeakReference.get() is null");
          return;
        }
        CoordinateOptions coordinate = CoordinateOptions.builder()
            .latitude(mGroupActivityWeakReference.get().getLastKnownCoord().lat)
            .longitude(mGroupActivityWeakReference.get().getLastKnownCoord().lon).build();
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
  }

}
