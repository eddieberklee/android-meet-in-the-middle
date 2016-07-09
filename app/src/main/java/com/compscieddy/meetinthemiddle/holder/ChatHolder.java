package com.compscieddy.meetinthemiddle.holder;

import android.content.Context;
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
  @Bind(R.id.name_text) TextView userNameText;
  @Bind(R.id.time_stamp_text) TextView timestampText;
  View rootView;
  TextView messageText;
  Context context;


  public ChatHolder(View itemView) {
    super(itemView);
    rootView = itemView;
    context = rootView.getContext();
    ButterKnife.bind(ChatHolder.this, rootView);
    messageText = ButterKnife.findById(rootView, R.id.message_text);
  }

  public void setIsSender(boolean isSender) {
    int chatSenderColor = ContextCompat.getColor(context, R.color.chat_sender);
    int chatSendeeColor = ContextCompat.getColor(context, R.color.chat_sendee);
    int chatSenderTextColor = ContextCompat.getColor(context, R.color.chat_sender_text);
    int chatSendeeTextColor = ContextCompat.getColor(context, R.color.chat_sendee_text);

    if (isSender) {
      ((GradientDrawable) messageBox.getBackground()).setColor(chatSenderColor);
      messageText.setTextColor(chatSenderTextColor);
      messageContainer.setGravity(Gravity.RIGHT);
      userNameText.setVisibility(View.GONE);
    } else {
      ((GradientDrawable) messageBox.getBackground()).setColor(chatSendeeColor);
      messageText.setTextColor(chatSendeeTextColor);
      messageContainer.setGravity(Gravity.LEFT);
    }
  }

  public void setTimestampText(Date date){
    int hours = date.getHours();
    int minutes = date.getMinutes();
    timestampText.setText(hours + ":" + minutes);
  }

  public void setName(String name) {
    userNameText.setText(name);
  }

  public void setText(String text) {
    messageText.setText(text);
  }
}
