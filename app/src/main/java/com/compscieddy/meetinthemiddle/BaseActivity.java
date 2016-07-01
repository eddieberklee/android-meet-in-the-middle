package com.compscieddy.meetinthemiddle;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.meetinthemiddle.model.User;
import com.compscieddy.meetinthemiddle.util.Lawg;
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
    lawg.d("BaseActivity onCreate()");
    mFirebaseDatabase = FirebaseDatabase.getInstance();
    mFirebaseAuth = FirebaseAuth.getInstance();
    mFirebaseUser = mFirebaseAuth.getCurrentUser();

    mFirebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != mFirebaseUser) {
          lawg.e("WHAT HAVE WE FOUND HERE");
          Etils.showToast(BaseActivity.this, "We have found a unicorn");
        }
        lawg.d("user " + user + " user2 " + mFirebaseUser);
        if (user == null) {
          // Firebase has deemed them auth-worthy so just recreate the user object for them
          lawg.d("user is null and firebase says auth worthy so creating a user");
          User.createUser(mFirebaseDatabase, mFirebaseUser);
        } else {
          final String encodedEmail = Etils.encodeEmail(mFirebaseUser.getEmail());
          mFirebaseDatabase.getReference("users").child(encodedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              mUser = dataSnapshot.getValue(User.class);
              if (mUser == null) {
                /*TODO: java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String com.compscieddy.meetinthemiddle.model.User.getKey()' on a null object reference
                at com.compscieddy.meetinthemiddle.HomeActivity.initFirebaseData(HomeActivity.java:370)
                at com.compscieddy.meetinthemiddle.HomeActivity.userIsReady(HomeActivity.java:365)
                at com.compscieddy.meetinthemiddle.BaseActivity$1$1.onDataChange(BaseActivity.java:65)*/
                lawg.d("Safety Check: mUser is null so creating a user");
                  User.createUser(mFirebaseDatabase, user);
//                userIsReady();
              } else { // Successful Sign-In
                lawg.d("mUser obtained email: " + mUser.email + " name: " + mUser.name);
                userIsReady();
              }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { lawg.e("onCancelled " + databaseError); }
          });
        }
      }
    });

  }
}
