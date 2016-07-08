package com.compscieddy.meetinthemiddle.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.compscieddy.meetinthemiddle.R;
import com.compscieddy.meetinthemiddle.ui.CustomEmoticonView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by elee on 7/6/16.
 */

public class InvitePeopleHolder extends RecyclerView.ViewHolder {

  public @Bind(R.id.avatar) CustomEmoticonView mAvatar;
  public @Bind(R.id.username) TextView mUsername;

  public InvitePeopleHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
  }

}
