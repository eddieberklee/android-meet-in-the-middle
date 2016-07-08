package com.compscieddy.meetinthemiddle.adapter;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.meetinthemiddle.holder.ChatHolder;
import com.compscieddy.meetinthemiddle.model.Chat;
import com.compscieddy.meetinthemiddle.model.User;
import com.compscieddy.meetinthemiddle.util.Lawg;
import com.compscieddy.meetinthemiddle.util.Util;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by elee on 7/6/16.
 */

public class ChatsFirebaseAdapter extends FirebaseRecyclerAdapter<Chat, ChatHolder> {

  private static final Lawg L = Lawg.newInstance(ChatsFirebaseAdapter.class.getSimpleName());

  public ChatsFirebaseAdapter(Class<Chat> modelClass, int modelLayout, Class<ChatHolder> viewHolderClass, Query ref) {
    super(modelClass, modelLayout, viewHolderClass, ref);
  }

  @Override
  protected void populateViewHolder(final ChatHolder chatHolder, final Chat chat, int position) {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    firebaseDatabase.getReference("users").child(chat.getUserKey()).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        chatHolder.setTimestampText(Util.getLocalTimeFromUTC(chat.getCurrentUTCTime()));
        chatHolder.setName(user.name);
        chatHolder.setText(chat.getChatMessage());

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String currentUserKey = Etils.encodeEmail(currentUser.getEmail());
        if (currentUser != null && chat.getUserKey().equals(currentUserKey)) {
          L.d(" user.getKey(): " + user.getKey() + " chat.getUserKey(): " + chat.getUserKey());
          chatHolder.setIsSender(true);
        } else {
          chatHolder.setIsSender(false);
        }
      }
      @Override
      public void onCancelled(DatabaseError databaseError) {
        L.e("onCancelled " + databaseError);
      }
    });
  }
}
