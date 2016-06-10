package com.compscieddy.meetinthemiddle.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.compscieddy.meetinthemiddle.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Darren on 11-Jun-16.
 */
public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusHolder> {

  private Context mContext;
  private static ClickListener mClickListener;

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
    holder.casinoImageView.setImageResource(R.drawable.ic_casino_white_24dp);
    holder.fitnessImageView.setImageResource(R.drawable.ic_fitness_center_white_24dp);
    holder.moviesImageView.setImageResource(R.drawable.ic_local_movies_white_24dp);
    holder.sleepingImageView.setImageResource(R.drawable.ic_local_hotel_white_24dp);

  }

  @Override
  public int getItemCount() {
    return 1;
  }

  public static final class StatusHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    CircleImageView casinoImageView;
    CircleImageView fitnessImageView;
    CircleImageView moviesImageView;
    CircleImageView sleepingImageView;


    public StatusHolder(View itemView) {
      super(itemView);
      casinoImageView = (CircleImageView) itemView.findViewById(R.id.status_casino_image_view);
      fitnessImageView = (CircleImageView) itemView.findViewById(R.id.status_fitness_image_view);
      moviesImageView = (CircleImageView) itemView.findViewById(R.id.status_movies_image_view);
      sleepingImageView = (CircleImageView) itemView.findViewById(R.id.status_sleeping_image_view);

      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      mClickListener.OnItemClick(v);
    }
  }


  }
}
