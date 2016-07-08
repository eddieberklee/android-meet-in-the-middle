package com.compscieddy.meetinthemiddle.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.compscieddy.meetinthemiddle.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by elee on 7/6/16.
 */

public final class GroupHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

  public @Bind(R.id.group_title) TextView titleView;
  public @Bind(R.id.group_last_message_text_view) TextView lastMessageView;
  public @Bind(R.id.group_map_view) MapView mapView;
  public @Bind(R.id.message_container) ViewGroup messageContainer;
  public int position;
  public View.OnClickListener onClickListener;
  GoogleMap groupMap;
  private final Context mContext;

  public GroupHolder(Context context, View itemView) {
    super(itemView);
    mContext = context;
    ButterKnife.bind(this, itemView);

    mapView.onCreate(null);
    mapView.getMapAsync(this);
    mapView.setClickable(false);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    MapsInitializer.initialize(mContext);
    groupMap = googleMap;
  }
}
