package com.compscieddy.meetinthemiddle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.compscieddy.eddie_utils.Lawg;

/**
 * Created by Darren on 11-Jun-16.
 */
public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusHolder> {

  private final static Lawg lawg = Lawg.newInstance(StatusAdapter.class.getSimpleName());
  private Context mContext;
  private static ClickListener mClickListener;

  public static final int ITEM_COUNT = 4;

  public static final int CASINO = 0;
  public static final int FITNESS = 1;
  public static final int MOVIES = 2;
  public static final int SLEEPING = 3;

  public interface ClickListener {
    void OnItemClick(View v);
  }

  public void setClickListener(ClickListener clickListener) {
    mClickListener = clickListener;
  }

  @Override
  public StatusAdapter.StatusHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    mContext = parent.getContext();
    LayoutInflater layoutInflater = LayoutInflater.from(mContext);
    View itemView = layoutInflater.inflate(R.layout.item_status, parent, false);
    return new StatusHolder(itemView);
  }

  @Override
  public void onBindViewHolder(StatusAdapter.StatusHolder holder, int position) {
    //Placeholder text for now
    int imageResourceId = -1;

    switch (position) {

      case CASINO:
        imageResourceId = R.drawable.ic_casino_white_24dp;
        break;
      case FITNESS:
        imageResourceId = R.drawable.ic_fitness_center_white_24dp;
        break;
      case MOVIES:
        imageResourceId = R.drawable.ic_local_movies_white_24dp;
        break;
      case SLEEPING:
        imageResourceId = R.drawable.ic_local_hotel_white_24dp;
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

  public static final class StatusHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ImageView statusImageView;

    public StatusHolder(View itemView) {
      super(itemView);
      statusImageView = (ImageView) itemView.findViewById(R.id.status_image_view);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      mClickListener.OnItemClick(v);
    }
  }

}
