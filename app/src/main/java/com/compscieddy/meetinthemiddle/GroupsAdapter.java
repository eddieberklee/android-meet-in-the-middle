package com.compscieddy.meetinthemiddle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;

import java.util.List;

/**
 * Created by ambar on 6/7/16.
 */
public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupHolder> {

  private Context mContext;
  private static ClickListener mClickListener;
  private List<MapView> mMapViewList;

  public GroupsAdapter(List<MapView> mapViewList){
    mMapViewList = mapViewList;
  }

  public interface ClickListener{
    void OnItemClick(View v);
  }

  public void setClickListener(ClickListener clickListener){
    mClickListener = clickListener;
  }

  @Override
  public GroupsAdapter.GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    mContext = parent.getContext();
    LayoutInflater layoutInflater = LayoutInflater.from(mContext);
    View itemView = layoutInflater.inflate(R.layout.item_group, parent, false);
    GroupHolder groupHolder = new GroupHolder(itemView);
    groupHolder.groupMapView.onCreate(null);
    mMapViewList.add(groupHolder.groupMapView);
    return groupHolder;
  }

  @Override
  public void onBindViewHolder(GroupsAdapter.GroupHolder holder, int position) {
    //Placeholder text for now
    holder.titleTextView.setText("Group " + position);
    holder.lastMessageTextView.setText("Last message of group " + position);
    holder.groupMapView.onResume();
  }

  @Override
  public int getItemCount() {
    //Arbitrary for now
    return 10;
  }

  public static final class GroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView titleTextView;
    TextView lastMessageTextView;
    MapView groupMapView;

    public GroupHolder(View itemView) {
      super(itemView);
      titleTextView = (TextView) itemView.findViewById(R.id.group_title_text_view);
      lastMessageTextView = (TextView) itemView.findViewById(R.id.group_last_message_text_view);
      groupMapView = (MapView) itemView.findViewById(R.id.group_map_view);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      mClickListener.OnItemClick(v);
    }
  }
}
