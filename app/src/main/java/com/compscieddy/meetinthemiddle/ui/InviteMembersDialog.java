package com.compscieddy.meetinthemiddle.ui;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.compscieddy.meetinthemiddle.R;
import com.compscieddy.meetinthemiddle.adapter.InviteRvAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by SEELE on 2016/7/4.
 */
public class InviteMembersDialog extends DialogFragment {

    @Bind(R.id.rv_invite_members) RecyclerView mRvInviteMembers;
    @Bind(R.id.btn_close) ImageButton mBtnClose;
    @Bind(R.id.invite_url) TextView mInviteUrl;


    private InviteRvAdapter mAdapter;


    public static InviteMembersDialog newInstance() {

        Bundle args = new Bundle();

        InviteMembersDialog fragment = new InviteMembersDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_invite_members, null);
        ButterKnife.bind(this, v);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        mAdapter = new InviteRvAdapter();
        mRvInviteMembers.hasFixedSize();
        mRvInviteMembers.setLayoutManager(gridLayoutManager);
        mRvInviteMembers.setAdapter(mAdapter);

        builder.setView(v);
        return builder.create();
    }

    @OnClick(R.id.btn_close)
    void onCloseBtnClick(){
        InviteMembersDialog.this.dismiss();
    }

    @OnClick(R.id.btn_copy)
    void onCopyBtnClick(){
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("", mInviteUrl.getText());
        clipboard.setPrimaryClip(data);
        Toast.makeText(getActivity(), "Share Link copied!", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_add)
    void onAddBtnClick(){

    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = inflater.inflate(R.layout.dialog_invite_members, container);
        ButterKnife.bind(this, rootView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        mAdapter = new InviteRvAdapter();
        mRvInviteMembers.hasFixedSize();
        mRvInviteMembers.setLayoutManager(gridLayoutManager);
        mRvInviteMembers.setAdapter(mAdapter);

        return rootView;
    }
*/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
