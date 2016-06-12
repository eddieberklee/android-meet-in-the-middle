package com.compscieddy.meetinthemiddle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.meetinthemiddle.adapter.ChatsAdapter;
import com.fondesa.recyclerviewdivider.RecyclerViewDivider;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ambar on 6/12/16.
 */
public class ChatFragment extends Fragment {


  @Bind(R.id.chats_recycler_view) RecyclerView mChatsRecyclerView;

  private ChatsAdapter mChatsAdapter;

  public static ChatFragment newInstance() {
    
    Bundle args = new Bundle();
    //For future arguments, add here
    ChatFragment fragment = new ChatFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_chat, container, false);
    ButterKnife.bind(this, view);

    setupRecyclerView();

    return view;
  }

  private void setupRecyclerView() {
    mChatsAdapter = new ChatsAdapter();
    mChatsRecyclerView.setAdapter(mChatsAdapter);
    mChatsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    RecyclerViewDivider.with(getActivity()).addTo(mChatsRecyclerView).marginSize(Etils.dpToPx(5)).build();
  }
}
