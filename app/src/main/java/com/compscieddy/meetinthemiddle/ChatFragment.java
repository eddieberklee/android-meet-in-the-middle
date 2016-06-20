package com.compscieddy.meetinthemiddle;

import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.eddie_utils.Lawg;
import com.compscieddy.meetinthemiddle.model.Chat;
import com.compscieddy.meetinthemiddle.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

  private static final Lawg lawg = Lawg.newInstance(ChatFragment.class.getSimpleName());

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

    return view;
  }

  @Override
  public void onStart() {
    super.onStart();
    // Default Database rules do not allow unauthenticated reads, so we need to
    // sign in before attaching the RecyclerView adapter otherwise the Adapter will
    // not be able to read any data from the Database.
    if (!isSignedIn()) {
      signInAnonymously();
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

    mChatsFirebaseAdapter = new FirebaseRecyclerAdapter<Chat, ChatHolder>(
        Chat.class, R.layout.item_chat, ChatHolder.class, mChatReference) {

      @Override
      public void populateViewHolder(final ChatHolder chatView, final Chat chat, int position) {
        mFirebaseDatabase.getReference("users").child(chat.getUserKey()).addListenerForSingleValueEvent(new ValueEventListener() {

          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            User user = dataSnapshot.getValue(User.class);
            chatView.setName(user.name);
            chatView.setText(chat.getChatMessage());

            FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
            String currentUserKey = Etils.encodeEmail(currentUser.getEmail());
            if (currentUser != null && chat.getUserKey().equals(currentUserKey)) {
              lawg.d(" user.getKey(): " + user.getKey() + " chat.getUserKey(): " + chat.getUserKey());
              chatView.setIsSender(true);
            } else {
              chatView.setIsSender(false);
            }
             if (mChatsFirebaseAdapter.getItemCount() > 0) {
              mEmptyChatView.setVisibility(View.INVISIBLE);
              mChatRecyclerView.setVisibility(View.VISIBLE);
            }
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {
            lawg.e("onCancelled " + databaseError);
          }
        });
      }
    };

    if (mChatsFirebaseAdapter.getItemCount() <= 0) {
      mChatRecyclerView.setVisibility(View.INVISIBLE);
      mEmptyChatView.setVisibility(View.VISIBLE);
    }

    // Scroll to bottom on new messages
    mChatsFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
      @Override
      public void onItemRangeInserted(int positionStart, int itemCount) {
        mLayoutManager.smoothScrollToPosition(mChatRecyclerView, null, mChatsFirebaseAdapter.getItemCount());
      }
    });
    mChatRecyclerView.setAdapter(mChatsFirebaseAdapter);
  }

  private void signInAnonymously() {
    Toast.makeText(getContext(), "Signing in...", Toast.LENGTH_SHORT).show();
    mFirebaseAuth.signInAnonymously()
        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              Toast.makeText(getActivity(), "Signed In",
                  Toast.LENGTH_SHORT).show();
              attachRecyclerViewAdapter();
            } else {
              Toast.makeText(getActivity(), "Sign In Failed",
                  Toast.LENGTH_SHORT).show();
            }
          }
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

  public static class ChatHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.left_arrow) FrameLayout leftArrow;
    @Bind(R.id.right_arrow) FrameLayout rightArrow;
    @Bind(R.id.message_container) RelativeLayout messageContainer;
    @Bind(R.id.message_box) LinearLayout messageBox;
    View rootView;

    public ChatHolder(View itemView) {
      super(itemView);
      rootView = itemView;
      ButterKnife.bind(ChatHolder.this, rootView);
    }

    public void setIsSender(boolean isSender) {
      int color;
      if (isSender) {
        color = ContextCompat.getColor(rootView.getContext(), R.color.group_chat_background_color);
        leftArrow.setVisibility(View.GONE);
        rightArrow.setVisibility(View.VISIBLE);
        messageContainer.setGravity(Gravity.RIGHT);
      } else {
        color = ContextCompat.getColor(rootView.getContext(), R.color.user_chat_background_color);
        leftArrow.setVisibility(View.VISIBLE);
        rightArrow.setVisibility(View.GONE);
        messageContainer.setGravity(Gravity.LEFT);
      }

      ((GradientDrawable) messageBox.getBackground()).setColor(color);
      ((RotateDrawable) leftArrow.getBackground()).getDrawable()
          .setColorFilter(color, PorterDuff.Mode.SRC);
      ((RotateDrawable) rightArrow.getBackground()).getDrawable()
          .setColorFilter(color, PorterDuff.Mode.SRC);
    }

    public void setName(String name) {
      TextView field = (TextView) rootView.findViewById(R.id.name_text);
      field.setText(name);
    }

    public void setText(String text) {
      TextView field = (TextView) rootView.findViewById(R.id.message_text);
      field.setText(text);
    }
  }


}
