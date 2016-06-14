package com.compscieddy.meetinthemiddle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.eddie_utils.Lawg;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthenticationActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

  private static final Lawg lawg = Lawg.newInstance(AuthenticationActivity.class.getSimpleName());

  @Bind(R.id.google_sign_in_button) SignInButton mSignInButton;
  private GoogleApiClient mGoogleApiClient;
  private static final int RC_SIGN_IN = 9001;
  private FirebaseAuth mAuth;
  private FirebaseAuth.AuthStateListener mAuthListener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_authentication);
    ButterKnife.bind(this);
    GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build();
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .enableAutoManage(this, this)
        .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
        .build();
    mAuth = FirebaseAuth.getInstance();
    mAuthListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
          lawg.d("onAuthStateChanged:signed_in:" + user.getUid());
          lawg.e("email: " + user.getEmail());
          lawg.e("uid: " + user.getUid());
        } else {
          lawg.d("onAuthStateChanged:signed_out");
        }
      }
    };
  }

  @OnClick(R.id.google_sign_in_button)
  public void signIn() {
    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
    startActivityForResult(signInIntent, RC_SIGN_IN);
  }

  private void signOut() {
    // Firebase sign out
    mAuth.signOut();

    // Google sign out
    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
        new ResultCallback<Status>() {
          @Override
          public void onResult(@NonNull Status status) {
            handleAuthenticationFail(null);
          }
        });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == RC_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      handleSignInResult(result);
    }
  }

  private void handleSignInResult(GoogleSignInResult result) {
    if (result.isSuccess()) {
      GoogleSignInAccount account = result.getSignInAccount();
      firebaseAuthWithGoogle(account);
    } else {
      handleAuthenticationFail(result);
    }
  }

  private void handleAuthenticationFail(@Nullable GoogleSignInResult result) {
    if (result != null) {
      lawg.e("GoogleSignInResult: " + result);
      lawg.e("SignInAccount: " + result.getSignInAccount() + " status: " + result.getStatus());
      // todo: implement
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    mAuth.addAuthStateListener(mAuthListener);
  }

  @Override
  public void onStop() {
    super.onStop();
    if (mAuthListener != null) {
      mAuth.removeAuthStateListener(mAuthListener);
    }
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    lawg.e("onConnectionFailed " + connectionResult);
  }

  private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
    lawg.d("firebaseAuthWithGoogle: " + acct.getId());
//    showProgressDialog();todo
    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
    mAuth.signInWithCredential(credential)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            lawg.d("signInWithCredential:onComplete: " + task.isSuccessful());

            // If sign in fails, display a message to the user. If sign in succeeds
            // the auth state listener will be notified and logic to handle the
            // signed in user can be handled in the listener.
            if (!task.isSuccessful()) {
              lawg.d("signInWithCredential " +  task.getException());
              Etils.showToast(AuthenticationActivity.this, "Authentication failed.");
            }
//            hideProgressDialog();todo
          }
        });
  }

}
