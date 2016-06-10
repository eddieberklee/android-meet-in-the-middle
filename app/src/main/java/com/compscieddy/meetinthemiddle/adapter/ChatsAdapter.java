package com.compscieddy.meetinthemiddle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.compscieddy.eddie_utils.Lawg;
import com.compscieddy.meetinthemiddle.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by elee on 6/9/16.
 */
public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatHolder> {

  private final static Lawg lawg = Lawg.newInstance(ChatsAdapter.class.getSimpleName());
  private Context mContext;

  @Override
  public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    mContext = parent.getContext();
    LayoutInflater layoutInflater = LayoutInflater.from(mContext);
    View itemView = layoutInflater.inflate(R.layout.item_chat, parent, false);
    lawg.e("BOOOOOOO " + mContext.getString(R.string.sample_chat_message));
    return new ChatHolder(itemView);
  }

  @Override
  public void onBindViewHolder(ChatsAdapter.ChatHolder holder, int position) {
//    holder.chatIcon.setImageResource();
    lawg.e("YOOOOOOO " + mContext.getString(R.string.sample_chat_message));
    holder.chatMessage.setText(mContext.getString(R.string.sample_chat_message) + " " + position);
  }

  @Override
  public int getItemCount() {
    return 10;
  }

  public static final class ChatHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.chat_icon) ImageView chatIcon;
    @Bind(R.id.chat_message) TextView chatMessage;

    public ChatHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

}
