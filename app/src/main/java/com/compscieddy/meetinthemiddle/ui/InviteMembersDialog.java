package com.compscieddy.meetinthemiddle.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.compscieddy.meetinthemiddle.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by SEELE on 2016/7/4.
 */
public class InviteMembersDialog extends DialogFragment {

    @Bind(R.id.rv_invite_members)
    RecyclerView mRvInviteMembers;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = inflater.inflate(R.layout.dialog_invite_members, container);
        ButterKnife.bind(this, rootView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
