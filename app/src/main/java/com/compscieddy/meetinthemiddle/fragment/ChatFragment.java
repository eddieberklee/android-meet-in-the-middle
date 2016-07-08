package com.compscieddy.meetinthemiddle.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.meetinthemiddle.R;
import com.compscieddy.meetinthemiddle.activity.AuthenticationActivity;
import com.compscieddy.meetinthemiddle.activity.GroupActivity;
import com.compscieddy.meetinthemiddle.adapter.ChatsFirebaseAdapter;
import com.compscieddy.meetinthemiddle.holder.ChatHolder;
import com.compscieddy.meetinthemiddle.model.Chat;
import com.compscieddy.meetinthemiddle.util.Lawg;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ambar on 6/12/16.
 */
public class ChatFragment extends Fragment {

  private static final Lawg L = Lawg.newInstance(ChatFragment.class.getSimpleName());

  @Bind(R.id.message_send_button) ImageView mSendButton;
  @Bind(R.id.message_edit_text) EditText mMessageEdit;

  @Bind(R.id.chats_recycler_view) RecyclerView mChatRecyclerView;
  @Bind(R.id.empty_chat_view) ViewGroup mEmptyChatView;

  private LinearLayoutManager mLayoutManager;
  private FirebaseRecyclerAdapter<Chat, ChatHolder> mChatsFirebaseAdapter;

  public static final String ARG_GROUP_KEY = "arg_group_key";
  private String mGroupKey;

  private DatabaseReference mChatReference;
  private FirebaseDatabase mFirebaseDatabase;
  private FirebaseAuth mFirebaseAuth;

  public static ChatFragment newInstance(String groupKey) {
    //For future arguments, add here
    ChatFragment fragment = new ChatFragment();
    Bundle args = new Bundle();
    args.putString(ARG_GROUP_KEY, groupKey);
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_chat, container, false);
    ButterKnife.bind(ChatFragment.this, view);

    Bundle args = getArguments();
    mGroupKey = args.getString(ARG_GROUP_KEY);

    mFirebaseAuth = FirebaseAuth.getInstance();
    mFirebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        updateUI();
      }
    });
    mFirebaseDatabase = FirebaseDatabase.getInstance();
    mChatReference = mFirebaseDatabase.getReference("chats").child(mGroupKey);

    mSendButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String uid = mFirebaseAuth.getCurrentUser().getUid(); // todo: use the user's id
        String name = "User " + uid.substring(0, 6);
        String userKey = Etils.encodeEmail(mFirebaseAuth.getCurrentUser().getEmail());

        DatabaseReference newChatReference = mChatReference.push();
        String chatKey = newChatReference.getKey();
        Chat chat = new Chat(chatKey, mGroupKey, userKey, mMessageEdit.getText().toString());
        newChatReference.setValue(chat, new DatabaseReference.CompletionListener() {
          @Override
          public void onComplete(DatabaseError databaseError, DatabaseReference reference) {
            if (databaseError != null) {
              Log.e("Failed to write message", databaseError.toException().toString());
            }
          }
        });
        mMessageEdit.setText("");
      }
    });

    mLayoutManager = new LinearLayoutManager(getActivity());
    mLayoutManager.setReverseLayout(false);

    mChatRecyclerView.setHasFixedSize(false);
    mChatRecyclerView.setLayoutManager(mLayoutManager);

    final RelativeLayout mBottomSection = (RelativeLayout) getActivity().findViewById(R.id.bottom_section);

    mChatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        //TODO here is where we expand the group activity bottom_view


        //Can use this if we want to keep state
        /*SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor spe = sp.edit();

        boolean isChatExpanded = sp.getBoolean("isChatExpanded", true);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        ResizeAnimation resizeAnimation;
        L.e("Chat expanded = " + isChatExpanded);

        if (!isChatExpanded) {
          resizeAnimation = new ResizeAnimation(
              mBottomSection,
              (int) (height * 0.75),
              (int) (height * 0.3));

          resizeAnimation.setDuration(400);
          mBottomSection.startAnimation(resizeAnimation);

          L.e("Chat view scrolled");
          spe.putBoolean("isChatExpanded", true);
          spe.commit();
        }*/

        GroupActivity activity = (GroupActivity) getActivity();
        activity.resizeViewPager(true);

      }
    });

    return view;

  }

  @Override
  public void onStart() {
    super.onStart();
    // Default Database rules do not allow unauthenticated reads, so we need to
    // sign in before attaching the RecyclerView adapter otherwise the Adapter will
    // not be able to read any data from the Database.
    if (!isSignedIn()) {
      // eject the user from current flow if they're not signed in
      Activity activity = getActivity();
      Intent intent = new Intent(activity, AuthenticationActivity.class);
      startActivity(intent);
      activity.finish();
    } else {
      attachRecyclerViewAdapter();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    if (mChatsFirebaseAdapter != null) {
      mChatsFirebaseAdapter.cleanup();
    }
  }

  private void attachRecyclerViewAdapter() {

    mChatsFirebaseAdapter = new ChatsFirebaseAdapter(Chat.class, R.layout.item_chat, ChatHolder.class, mChatReference);

    // Scroll to bottom on new messages
    mChatsFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
      @Override
      public void onItemRangeInserted(int positionStart, int itemCount) {
        mLayoutManager.smoothScrollToPosition(mChatRecyclerView, null, mChatsFirebaseAdapter.getItemCount());
      }
    });

    mChatRecyclerView.setAdapter(mChatsFirebaseAdapter);

    mFirebaseDatabase.getReference("chats").child(mGroupKey).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        long numChats = dataSnapshot.getChildrenCount();
        if (numChats > 0) {
          mEmptyChatView.setVisibility(View.INVISIBLE);
          mChatRecyclerView.setVisibility(View.VISIBLE);
        } else {
          mChatRecyclerView.setVisibility(View.INVISIBLE);
          mEmptyChatView.setVisibility(View.VISIBLE);
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) { L.e("onCancelled() " + databaseError); }
    });
  }

  public boolean isSignedIn() {
    return (mFirebaseAuth.getCurrentUser() != null);
  }

  public void updateUI() {
    // Sending only allowed when signed in
    mSendButton.setEnabled(isSignedIn());
    mMessageEdit.setEnabled(isSignedIn());
  }


}
