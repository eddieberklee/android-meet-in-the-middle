package com.compscieddy.meetinthemiddle;

import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ambar on 6/12/16.
 */
public class ChatFragment extends Fragment {


  @Bind(R.id.chats_recycler_view)
  RecyclerView mChatsRecyclerView;

  private FirebaseAuth mAuth;
  private DatabaseReference mRef;
  private Query mChatRef;
  private Button mSendButton;
  private EditText mMessageEdit;

  private RecyclerView mMessages;
  private LinearLayoutManager mManager;
  private FirebaseRecyclerAdapter<Chat, ChatHolder> mRecyclerViewAdapter;


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
    ButterKnife.bind(this, view);

    return view;
  }

  public static class Chat {

        String name;
        String text;
        String uid;

        public Chat() {
        }

        public Chat(String name, String uid, String message) {
            this.name = name;
            this.text = message;
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public String getUid() {
            return uid;
        }

        public String getText() {
            return text;
        }
    }


    public static class ChatHolder extends RecyclerView.ViewHolder {
        View mView;

        public ChatHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setIsSender(Boolean isSender) {
            FrameLayout left_arrow = (FrameLayout) mView.findViewById(R.id.left_arrow);
            FrameLayout right_arrow = (FrameLayout) mView.findViewById(R.id.right_arrow);
            RelativeLayout messageContainer = (RelativeLayout) mView.findViewById(R.id.message_container);
            LinearLayout message = (LinearLayout) mView.findViewById(R.id.message);

            int color;
            if (isSender) {
                color = ContextCompat.getColor(mView.getContext(), R.color.group_chat_background_color);
                left_arrow.setVisibility(View.GONE);
                right_arrow.setVisibility(View.VISIBLE);
                messageContainer.setGravity(Gravity.RIGHT);
            } else {
                color = ContextCompat.getColor(mView.getContext(), R.color.user_chat_background_color);
                left_arrow.setVisibility(View.VISIBLE);
                right_arrow.setVisibility(View.GONE);
                messageContainer.setGravity(Gravity.LEFT);
            }

            ((GradientDrawable) message.getBackground()).setColor(color);
            ((RotateDrawable) left_arrow.getBackground()).getDrawable()
                    .setColorFilter(color, PorterDuff.Mode.SRC);
            ((RotateDrawable) right_arrow.getBackground()).getDrawable()
                    .setColorFilter(color, PorterDuff.Mode.SRC);
        }

        public void setName(String name) {
            TextView field = (TextView) mView.findViewById(R.id.name_text);
            field.setText(name);
        }

        public void setText(String text) {
            TextView field = (TextView) mView.findViewById(R.id.message_text);
            field.setText(text);
        }
    }


}
