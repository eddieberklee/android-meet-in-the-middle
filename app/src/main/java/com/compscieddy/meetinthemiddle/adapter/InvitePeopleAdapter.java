package com.compscieddy.meetinthemiddle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.dragselectrecyclerview.DragSelectRecyclerViewAdapter;
import com.compscieddy.meetinthemiddle.R;
import com.compscieddy.meetinthemiddle.ui.CustomEmoticonView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jet Wang on 2016/7/4.
 */
public class InvitePeopleAdapter extends DragSelectRecyclerViewAdapter<InvitePeopleAdapter.ViewHolder> {

    public interface ClickListener{
        void onClick(int index);
    }

    private final ClickListener mCallback;
    private Context mContext;

    public InvitePeopleAdapter(ClickListener callback){
        super();
        mCallback = callback;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_invite_members, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (isIndexSelected(position)) {
            // Item is selected, change it somehow
            holder.mAvatar.setBackgroundResource(R.drawable.item_invite_members_selected);
        } else {
            // Item is not selected, reset it to a non-selected state
            holder.mAvatar.setBackgroundResource(R.drawable.bg_item_members_circle);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null){
                    mCallback.onClick(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 20;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.avatar) CustomEmoticonView mAvatar;
        @Bind(R.id.username) TextView mUsername;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
