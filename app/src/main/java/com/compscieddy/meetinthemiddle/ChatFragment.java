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

import com.compscieddy.meetinthemiddle.model.Chat;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ambar on 6/12/16.
 */
public class ChatFragment extends Fragment {

  private FirebaseAuth mAuth;
  private DatabaseReference mRef;
  @Bind(R.id.message_send_button) ImageView mSendButton;
  @Bind(R.id.message_edit_text) EditText mMessageEdit;

  @Bind(R.id.chats_recycler_view) RecyclerView mChatRecyclerView;
  private LinearLayoutManager mLayoutManager;
  private FirebaseRecyclerAdapter<Chat, ChatHolder> mChatsFirebaseAdapter;


  public static ChatFragment newInstance() {

    Bundle args = new Bundle();
    //For future arguments, add here
    ChatFragment fragment = new ChatFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_chat, container, false);
    ButterKnife.bind(ChatFragment.this, view);
    mAuth = FirebaseAuth.getInstance();
    mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        updateUI();
      }
    });

    mRef = FirebaseDatabase.getInstance().getReference();

    mSendButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String uid = mAuth.getCurrentUser().getUid(); // todo: use the user's id
        String name = "User " + uid.substring(0, 6);

        Chat chat = new Chat(name, uid, mMessageEdit.getText().toString());
        mRef.push().setValue(chat, new DatabaseReference.CompletionListener() {
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
        Chat.class, R.layout.item_chat, ChatHolder.class, mRef) {

      @Override
      public void populateViewHolder(ChatHolder chatView, Chat chat, int position) {
        chatView.setName(chat.getName());
        chatView.setText(chat.getText());

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && chat.getUid() != null && chat.getUid().equals(currentUser.getUid())) {
          chatView.setIsSender(true);
        } else {
          chatView.setIsSender(false);
        }
      }
    };

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
    mAuth.signInAnonymously()
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
    return (mAuth.getCurrentUser() != null);
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
