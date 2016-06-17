package com.compscieddy.meetinthemiddle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.compscieddy.meetinthemiddle.model.Group;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ambar on 6/7/16.
 */
public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupHolder> {

  private static Context mContext;
  private static ClickListener mClickListener;
  private List<Group> groups = new ArrayList<>();

  public interface ClickListener {
    void OnItemClick(View v);
  }

  public void setClickListener(ClickListener clickListener) {
    mClickListener = clickListener;
  }

  public void addGroup(Group group) {
    groups.add(group);
  }

  public void removeGroup(Group deleteGroup) {
    for (Group group : groups) {
      if (group.groupKey == deleteGroup.groupKey) {
        groups.remove(group);
      }
    }
  }

  @Override
  public GroupsAdapter.GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    mContext = parent.getContext();
    LayoutInflater layoutInflater = LayoutInflater.from(mContext);
    View itemView = layoutInflater.inflate(R.layout.item_group, parent, false);
    return new GroupHolder(itemView);
  }

  @Override
  public void onBindViewHolder(GroupsAdapter.GroupHolder holder, int position) {
    //Placeholder text for now
    holder.titleTextView.setText("Group " + position);
    holder.lastMessageTextView.setText("Last message of group " + position);
  }

  @Override
  public int getItemCount() {
    return groups.size();
  }

  public static final class GroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnMapReadyCallback {
    @Bind(R.id.group_title_text_view) TextView titleTextView;
    @Bind(R.id.group_last_message_text_view) TextView lastMessageTextView;
    @Bind(R.id.group_map_view) MapView groupMapView;
    GoogleMap groupMap;

    public GroupHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

      groupMapView.onCreate(null);
      groupMapView.getMapAsync(this);
      groupMapView.setClickable(false);

      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      mClickListener.OnItemClick(v);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
      MapsInitializer.initialize(mContext);
      groupMap = googleMap;
    }
  }
}
