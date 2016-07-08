package com.compscieddy.meetinthemiddle.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.compscieddy.meetinthemiddle.R;
import com.compscieddy.meetinthemiddle.activity.GroupActivity;
import com.compscieddy.meetinthemiddle.holder.GroupHolder;
import com.compscieddy.meetinthemiddle.model.Chat;
import com.compscieddy.meetinthemiddle.model.Group;
import com.compscieddy.meetinthemiddle.util.Lawg;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ambar on 6/7/16.
 */
public class GroupsAdapter extends RecyclerView.Adapter<GroupHolder> {

  private static final Lawg L = Lawg.newInstance(GroupsAdapter.class.getSimpleName());
  private static Context mContext;
  public List<Group> groups = new ArrayList<>();
  private FirebaseDatabase mFirebaseDatabase;

  public void addGroup(Group group) {
    groups.add(group);
    notifyDataSetChanged();
  }

  public void removeGroup(String deleteGroupKey) {
    //TODO: java.util.ConcurrentModificationException
    //at java.util.ArrayList$ArrayListIterator.next(ArrayList.java:573)
    //at com.compscieddy.meetinthemiddle.adapter.GroupsAdapter.removeGroup(GroupsAdapter.java:47)
    //at com.compscieddy.meetinthemiddle.activity.HomeActivity$4.onChildRemoved(HomeActivity.java:411)

    for (Iterator<Group> it = groups.iterator(); it.hasNext();){
      Group group = it.next();
      if (TextUtils.equals(group.key, deleteGroupKey)){
        it.remove();
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
    L.e("No matching group was found");
  }

  @Override
  public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    mContext = parent.getContext();
    LayoutInflater layoutInflater = LayoutInflater.from(mContext);
    View itemView = layoutInflater.inflate(R.layout.item_group, parent, false);
    final GroupHolder groupHolder = new GroupHolder(mContext, itemView);
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
  public void onBindViewHolder(final GroupHolder holder, final int position) {
    holder.position = position;
    final Group group = groups.get(position);
    String groupTitle = group.getGroupTitle();
    if (!TextUtils.isEmpty(groupTitle)) {
      holder.titleView.setText(groupTitle);
    }
    holder.messageContainer.setVisibility(View.GONE);
    Query lastMessageQuery = mFirebaseDatabase.getReference("chats").child(group.getKey()).limitToLast(1);
    lastMessageQuery.addChildEventListener(new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        holder.messageContainer.setVisibility(View.VISIBLE);
        Chat lastChat = dataSnapshot.getValue(Chat.class);
        String lastChatMessage = lastChat.getChatMessage();
        if (TextUtils.isEmpty(lastChatMessage)) {
          holder.lastMessageView.setText("Last message of group " + position);
        } else {
          holder.lastMessageView.setText(lastChat.getChatMessage());
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
        L.e("onCancelled() " + databaseError);
      }
    });
  }

  @Override
  public int getItemCount() {
    return groups.size();
  }
}
