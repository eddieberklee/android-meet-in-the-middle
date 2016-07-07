package com.compscieddy.meetinthemiddle.holder;

import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.compscieddy.meetinthemiddle.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by elee on 7/6/16.
 */

public class ChatHolder extends RecyclerView.ViewHolder {

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
