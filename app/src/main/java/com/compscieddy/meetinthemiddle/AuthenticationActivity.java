package com.compscieddy.meetinthemiddle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.compscieddy.eddie_utils.Lawg;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthenticationActivity extends AppCompatActivity implements
    GoogleApiClient.OnConnectionFailedListener,
    View.OnClickListener {

  private static final Lawg lawg = Lawg.newInstance(AuthenticationActivity.class.getSimpleName());
  private static final int RC_SIGN_IN = 9001;

  // [START declare_auth]
  private FirebaseAuth mAuth;
  // [END declare_auth]

  // [START declare_auth_listener]
  private FirebaseAuth.AuthStateListener mAuthListener;
  // [END declare_auth_listener]

  private GoogleApiClient mGoogleApiClient;
  private TextView mStatusTextView;
  private TextView mDetailTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_authentication);
    ButterKnife.bind(this);

    // [START config_signin]
    // Configure Google Sign In
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id))
        .requestEmail()
        .build();
    // [END config_signin]

    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
        .build();

    // [START initialize_auth]
    mAuth = FirebaseAuth.getInstance();
    // [END initialize_auth]

    // [START auth_state_listener]
    mAuthListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
          // User is signed in
          lawg.d("onAuthStateChanged:signed_in:" + user.getUid());
        } else {
          // User is signed out
          lawg.d("onAuthStateChanged:signed_out");
        }
        // [START_EXCLUDE]
        updateUI(user);
        // [END_EXCLUDE]
      }
    };
    // [END auth_state_listener]
  }

  // [START on_start_add_listener]
  @Override
  public void onStart() {
    super.onStart();
    mAuth.addAuthStateListener(mAuthListener);
  }
  // [END on_start_add_listener]

  // [START on_stop_remove_listener]
  @Override
  public void onStop() {
    super.onStop();
    if (mAuthListener != null) {
      mAuth.removeAuthStateListener(mAuthListener);
    }
  }
  // [END on_stop_remove_listener]

  // [START onactivityresult]
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    if (requestCode == RC_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      lawg.d("success: " + result.isSuccess());
      if (result.isSuccess()) {
        // Google Sign In was successful, authenticate with Firebase
        GoogleSignInAccount account = result.getSignInAccount();
        firebaseAuthWithGoogle(account);
      } else {
        lawg.e("error " + result.getStatus() + " " + result.getSignInAccount());
        // Google Sign In failed, update UI appropriately
        // [START_EXCLUDE]
        updateUI(null);
        // [END_EXCLUDE]
      }
    }
  }
  // [END onactivityresult]

  // [START auth_with_google]
  private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
    lawg.d("firebaseAuthWithGoogle:" + acct.getId());
    // [START_EXCLUDE silent]
//    showProgressDialog();
    // [END_EXCLUDE]

    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
    mAuth.signInWithCredential(credential)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            lawg.d("signInWithCredential:onComplete:" + task.isSuccessful());

            // If sign in fails, display a message to the user. If sign in succeeds
            // the auth state listener will be notified and logic to handle the
            // signed in user can be handled in the listener.
            if (!task.isSuccessful()) {
              lawg.d("signInWithCredential " + task.getException());
              Toast.makeText(AuthenticationActivity.this, "Authentication failed.",
                  Toast.LENGTH_SHORT).show();
            }
            // [START_EXCLUDE]
//            hideProgressDialog();
            // [END_EXCLUDE]
          }
        });
  }
  // [END auth_with_google]

  @OnClick(R.id.google_sign_in_button)
  public void signIn() {
    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
    startActivityForResult(signInIntent, RC_SIGN_IN);
  }
  // [END signin]

  private void signOut() {
    // Firebase sign out
    mAuth.signOut();

    // Google sign out
    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
        new ResultCallback<Status>() {
          @Override
          public void onResult(@NonNull Status status) {
            updateUI(null);
          }
        });
  }

  private void revokeAccess() {
    // Firebase sign out
    mAuth.signOut();

    // Google revoke access
    Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
        new ResultCallback<Status>() {
          @Override
          public void onResult(@NonNull Status status) {
            updateUI(null);
          }
        });
  }

  private void updateUI(FirebaseUser user) {
//    hideProgressDialog();
    if (user != null) {
      lawg.e(user.getEmail());
      lawg.e(user.getUid());
//      mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
//      mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//
//      findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//      findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
    } else {
//      mStatusTextView.setText(R.string.signed_out);
//      mDetailTextView.setText(null);
//
//      findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//      findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
    }
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    // An unresolvable error has occurred and Google APIs (including Sign-In) will not
    // be available.
    lawg.d("onConnectionFailed:" + connectionResult);
    Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
//      case R.id.google_sign_in_button:
//        signIn();
//        break;
//      case R.id.sign_out_button:
//        signOut();
//        break;
//      case R.id.disconnect_button:
//        revokeAccess();
//        break;
    }
  }
}