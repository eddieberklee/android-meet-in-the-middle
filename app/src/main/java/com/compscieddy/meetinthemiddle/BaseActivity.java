package com.compscieddy.meetinthemiddle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.eddie_utils.Lawg;
import com.compscieddy.meetinthemiddle.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by elee on 6/16/16.
 */
public class BaseActivity extends FragmentActivity {

  private static final Lawg lawg = Lawg.newInstance(BaseActivity.class.getSimpleName());

  FirebaseUser mFirebaseUser;
  FirebaseDatabase mFirebaseDatabase;
  FirebaseAuth mFirebaseAuth;
  User mUser;

  /** This is when the mUser object has been correctly populated from Firebase */
  public void userIsReady() {
    // no-op, implement this in the activity you extend
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mFirebaseDatabase = FirebaseDatabase.getInstance();
    mFirebaseAuth = FirebaseAuth.getInstance();
    mFirebaseUser = mFirebaseAuth.getCurrentUser();

    if (mFirebaseUser == null) {
      Intent intent = new Intent(BaseActivity.this, AuthenticationActivity.class);
      startActivity(intent);
      finish();
    }

    final String encodedEmail = Etils.encodeEmail(mFirebaseUser.getEmail());
    mFirebaseDatabase.getReference("users").child(encodedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        mUser = dataSnapshot.getValue(User.class);
        if (mUser == null) {
          // This should only happen in developer mode since we may arbitrarily delete from the database - resulting in inconsistency between database and logged in status
          User.createUser(mFirebaseDatabase, mFirebaseUser);
          mFirebaseDatabase.getReference("users").child(encodedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              mUser = dataSnapshot.getValue(User.class);
              userIsReady();
              if (mUser == null) {
                // Assume shit's gone to hell, try to sign out the user and send them to the AuthenticationActivity
                mFirebaseAuth.signOut();
                Intent intent = new Intent(BaseActivity.this, AuthenticationActivity.class);
                startActivity(intent);
                finish();
              }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { lawg.e("onCancelled " + databaseError); }
          });
        } else {
          lawg.d("mUser obtained email: " + mUser.email + " name: " + mUser.name);
          userIsReady();
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) { lawg.e("onCancelled " + databaseError); }
    });

  }
}
