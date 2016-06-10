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

  private String[] mDataSet;
  private int[] mDataSetTypes;

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

  public static class StatusHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public StatusHolder(View v) {
      super(v);
    }

    @Override
    public void onClick(View v) {
      mClickListener.OnItemClick(v);
    }

  }

  public class CasinoHolder extends StatusAdapter.StatusHolder {
    CircleImageView casinoImageView;

    public CasinoHolder(View itemView) {
      super(itemView);
      this.casinoImageView = (CircleImageView) itemView.findViewById(R.id.status_casino_image_view);
    }

  }

  public class FitnessHolder extends StatusAdapter.StatusHolder {
    CircleImageView fitnessImageView;

    public FitnessHolder(View itemView) {
      super(itemView);
      this.fitnessImageView = (CircleImageView) itemView.findViewById(R.id.status_fitness_image_view);
    }

  }

  public class MoviesHolder extends StatusAdapter.StatusHolder {
    CircleImageView moviesImageView;

    public MoviesHolder(View itemView) {
      super(itemView);
      this.moviesImageView = (CircleImageView) itemView.findViewById(R.id.status_movies_item_view);
    }

  }

  public class SleepingHolder extends StatusAdapter.StatusHolder {
    CircleImageView sleepingImageView;

    public SleepingHolder(View itemView) {
      super(itemView);
      this.sleepingImageView = (CircleImageView) itemView.findViewById(R.id.status_sleeping_item_view);
    }

  }

  public StatusAdapter(String[] dataSet, int[] dataSetTypes) {
    mDataSet = dataSet;
    mDataSetTypes = dataSetTypes;
  }

/*  @Override
  public StatusAdapter.StatusHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    mContext = parent.getContext();
    LayoutInflater layoutInflater = LayoutInflater.from(mContext);
    View itemView = layoutInflater.inflate(R.layout.item_casino, parent, false);
    return new StatusHolder(itemView);
  }*/

  @Override
  public StatusHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View view;

    if (viewType == CASINO) {
      view = LayoutInflater.from(viewGroup.getContext())
          .inflate(R.layout.item_casino, viewGroup, false);
      return new CasinoHolder(view);

    } else if (viewType == FITNESS) {
      view = LayoutInflater.from(viewGroup.getContext())
          .inflate(R.layout.item_fitness, viewGroup, false);
      return new FitnessHolder(view);
    } else if (viewType == MOVIES){
      view = LayoutInflater.from(viewGroup.getContext())
          .inflate(R.layout.item_movies, viewGroup, false);
      return new MoviesHolder(view);
    } else {
      view = LayoutInflater.from(viewGroup.getContext())
          .inflate(R.layout.item_sleeping, viewGroup, false);
      return new SleepingHolder(view);
    }
  }

  @Override
  public void onBindViewHolder(StatusAdapter.StatusHolder viewHolder, final int position) {
    if (viewHolder.getItemViewType() == CASINO) {
      CasinoHolder holder = (CasinoHolder) viewHolder;
      holder.casinoImageView.setImageResource(R.drawable.ic_casino_white_24dp);
    } else if (viewHolder.getItemViewType() == FITNESS) {
      FitnessHolder holder = (FitnessHolder) viewHolder;
      holder.fitnessImageView.setImageResource(R.drawable.ic_fitness_center_white_24dp);
    } else if (viewHolder.getItemViewType() == MOVIES) {
      MoviesHolder holder = (MoviesHolder) viewHolder;
      holder.moviesImageView.setImageResource(R.drawable.ic_local_movies_white_24dp);
    } else {
      SleepingHolder holder = (SleepingHolder) viewHolder;
      holder.sleepingImageView.setImageResource(R.drawable.ic_local_hotel_white_24dp);
    }
  }

  @Override
  public int getItemCount() {
    return mDataSet.length;
  }

  @Override
  public int getItemViewType(int position) {
    return mDataSetTypes[position];
  }
}



