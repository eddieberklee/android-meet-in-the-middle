package com.compscieddy.meetinthemiddle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.compscieddy.meetinthemiddle.R;
import com.compscieddy.meetinthemiddle.holder.StatusHolder;
import com.compscieddy.meetinthemiddle.util.Lawg;

/**
 * Created by Darren on 11-Jun-16.
 */
public class StatusAdapter extends RecyclerView.Adapter<StatusHolder> {

  private final static Lawg L = Lawg.newInstance(StatusAdapter.class.getSimpleName());
  private Context mContext;
  private View.OnClickListener mOnClickListener;

  public static final int ITEM_COUNT = 4;

  public static final int CASINO = 0;
  public static final int FITNESS = 1;
  public static final int MOVIES = 2;
  public static final int SLEEPING = 3;

  public void setOnClickListener(View.OnClickListener onClickListener) {
    mOnClickListener = onClickListener;
  }

  @Override
  public StatusHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    mContext = parent.getContext();
    LayoutInflater layoutInflater = LayoutInflater.from(mContext);
    View itemView = layoutInflater.inflate(R.layout.item_status, parent, false);
    return new StatusHolder(itemView, mOnClickListener);
  }

  @Override
  public void onBindViewHolder(StatusHolder holder, int position) {
    //Placeholder text for now
    int imageResourceId = -1;

    switch (position) {

      case CASINO:
        imageResourceId = R.drawable.ic_casino_white_48dp;
        break;
      case FITNESS:
        imageResourceId = R.drawable.ic_fitness_center_white_48dp;
        break;
      case MOVIES:
        imageResourceId = R.drawable.ic_local_movies_white_48dp;
        break;
      case SLEEPING:
        imageResourceId = R.drawable.ic_local_hotel_white_48dp;
        break;
    }
    if (imageResourceId != -1) {
      holder.statusImageView.setImageResource(imageResourceId);
    }
  }

  @Override
  public int getItemCount() {
    return ITEM_COUNT;
  }

}
