package com.compscieddy.meetinthemiddle.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.compscieddy.meetinthemiddle.R;

import butterknife.ButterKnife;

/**
 * Created by elee on 7/6/16.
 */

public class StatusHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
  public ImageView statusImageView;
  View.OnClickListener mOnClickListener;

  public StatusHolder(View itemView, View.OnClickListener onClickListener) {
    super(itemView);
    mOnClickListener = onClickListener;
    statusImageView = ButterKnife.findById(itemView, R.id.status_image_view);
    itemView.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    mOnClickListener.onClick(v);
  }
}
