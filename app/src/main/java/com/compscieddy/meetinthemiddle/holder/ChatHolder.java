package com.compscieddy.meetinthemiddle.holder;

import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.compscieddy.meetinthemiddle.R;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by elee on 7/6/16.
 */
public class ChatHolder extends RecyclerView.ViewHolder {

  @Bind(R.id.message_container) LinearLayout messageContainer;
  @Bind(R.id.message_box) LinearLayout messageBox;
  @Bind(R.id.name_text) TextView userName;
  @Bind(R.id.time_stamp_text) TextView timestampText;
  View rootView;
  TextView messageText;


  public ChatHolder(View itemView) {
    super(itemView);
    rootView = itemView;
    ButterKnife.bind(ChatHolder.this, rootView);
    messageText = (TextView) rootView.findViewById(R.id.message_text);
  }

  public void setIsSender(boolean isSender) {
    int bg_color;
    int text_color;
    if (isSender) {
      bg_color = ContextCompat.getColor(rootView.getContext(), R.color.chat_sender);
      text_color = ContextCompat.getColor(rootView.getContext(), R.color.chat_sender_text);
      messageContainer.setGravity(Gravity.RIGHT);
      userName.setVisibility(View.GONE);
    } else {
      bg_color = ContextCompat.getColor(rootView.getContext(), R.color.chat_sendee);
      text_color = ContextCompat.getColor(rootView.getContext(), R.color.chat_sendee_text);
      messageContainer.setGravity(Gravity.LEFT);
    }

    ((GradientDrawable) messageBox.getBackground()).setColor(bg_color);
    messageText.setTextColor(text_color);
  }

  public void setTimestamp(Date date){
    int hours = date.getHours();
    int minutes = date.getMinutes();
    timestampText.setText(hours + ":" + minutes);
  }

  public void setName(String name) {
    userName.setText(name);
  }

  public void setText(String text) {
    messageText.setText(text);
  }
}
