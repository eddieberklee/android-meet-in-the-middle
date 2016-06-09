package com.compscieddy.meetinthemiddle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ambar on 6/7/16.
 */
public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupHolder> {

  private Context mContext;
  private static ClickListener mClickListener;

  public interface ClickListener{
    void OnItemClick(View v);
  }

  public void setClickListener(ClickListener clickListener){
    mClickListener = clickListener;
  }

  @Override
  public GroupsAdapter.GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    mContext = parent.getContext();
    LayoutInflater layoutInflater = LayoutInflater.from(mContext);
    View itemView = layoutInflater.inflate(R.layout.item_group, parent, false);
    return new GroupHolder(itemView);
  }

  @Override
  public void onBindViewHolder(GroupsAdapter.GroupHolder holder, int position) {
    //Placeholder text for now
    holder.mAvatarImageView.setImageResource(R.drawable.ic_account_circle_grey600_48dp);
    holder.mTitleTextView.setText("Group " + position);
    holder.mLastMessageTextView.setText("Last message of group " + position);
  }

  @Override
  public int getItemCount() {
    //Arbitrary for now
    return 10;
  }

  public static final class GroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    CircleImageView mAvatarImageView;
    TextView mTitleTextView;
    TextView mLastMessageTextView;

    public GroupHolder(View itemView) {
      super(itemView);
      mAvatarImageView = (CircleImageView) itemView.findViewById(R.id.group_avatar_image_view);
      mTitleTextView = (TextView) itemView.findViewById(R.id.group_title_text_view);
      mLastMessageTextView = (TextView) itemView.findViewById(R.id.group_last_message_text_view);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      mClickListener.OnItemClick(v);
    }
  }
}
