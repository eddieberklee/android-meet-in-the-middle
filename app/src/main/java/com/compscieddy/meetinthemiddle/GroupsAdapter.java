package com.compscieddy.meetinthemiddle;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.compscieddy.meetinthemiddle.model.Chat;
import com.compscieddy.meetinthemiddle.model.Group;
import com.compscieddy.meetinthemiddle.util.Lawg;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ambar on 6/7/16.
 */
public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupHolder> {

  private static final Lawg lawg = Lawg.newInstance(GroupsAdapter.class.getSimpleName());
  private static Context mContext;
  public List<Group> groups = new ArrayList<>();
  private FirebaseDatabase mFirebaseDatabase;

  public void addGroup(Group group) {
    groups.add(group);
    notifyDataSetChanged();
  }

  public void removeGroup(String deleteGroupKey) {
    for (Group group : groups) {
      if (TextUtils.equals(group.key, deleteGroupKey)) {
        groups.remove(group);
        notifyDataSetChanged();
      }
    }
  }

  public void updateGroup(Group updatedGroup) {
    for (Group group : groups) {
      if (TextUtils.equals(group.key, updatedGroup.key)) {
        group.updateWith(updatedGroup);
        notifyDataSetChanged();
        return;
      }
    }
    lawg.e("No matching group was found");
  }

  @Override
  public GroupsAdapter.GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    mContext = parent.getContext();
    LayoutInflater layoutInflater = LayoutInflater.from(mContext);
    View itemView = layoutInflater.inflate(R.layout.item_group, parent, false);

    final GroupHolder groupHolder = new GroupHolder(itemView);
    groupHolder.onClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(mContext, GroupActivity.class);
        // For testing purposes, gives NPE otherwise
        intent.putExtra(GroupActivity.ARG_GROUP_KEY, GroupsAdapter.this.groups.get(groupHolder.position).key);
        mContext.startActivity(intent);
      }
    };
    groupHolder.itemView.setOnClickListener(groupHolder.onClickListener);

    mFirebaseDatabase = FirebaseDatabase.getInstance();

    return groupHolder;
  }

  @Override
  public void onBindViewHolder(final GroupsAdapter.GroupHolder holder, final int position) {
    holder.position = position;
    final Group group = groups.get(position);
    String groupTitle = group.getGroupTitle();
    if (!TextUtils.isEmpty(groupTitle)) {
      holder.titleTextView.setText(groupTitle);
    }
    Query lastMessageQuery = mFirebaseDatabase.getReference("chats").child(group.getKey()).limitToLast(1);
    lastMessageQuery.addChildEventListener(new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Chat lastChat = dataSnapshot.getValue(Chat.class);
        String lastChatMessage = lastChat.getChatMessage();
        if (TextUtils.isEmpty(lastChatMessage)) {
          holder.lastMessageTextView.setText("Last message of group " + position);
        } else {
          holder.lastMessageTextView.setText(lastChat.getChatMessage());
        }
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {}

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

      @Override
      public void onCancelled(DatabaseError databaseError) {
        lawg.e("onCancelled() " + databaseError);
      }
    });
  }

  @Override
  public int getItemCount() {
    return groups.size();
  }

  public static final class GroupHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
    @Bind(R.id.group_title_text_view) TextView titleTextView;
    @Bind(R.id.group_last_message_text_view) TextView lastMessageTextView;
    @Bind(R.id.group_map_view) MapView groupMapView;
    public int position;
    public View.OnClickListener onClickListener;
    GoogleMap groupMap;

    public GroupHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

      groupMapView.onCreate(null);
      groupMapView.getMapAsync(this);
      groupMapView.setClickable(false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
      MapsInitializer.initialize(mContext);
      groupMap = googleMap;
    }
  }
}
