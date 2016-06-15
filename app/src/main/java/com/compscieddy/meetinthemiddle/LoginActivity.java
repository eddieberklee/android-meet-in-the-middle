package com.compscieddy.meetinthemiddle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by ambar on 6/15/16.
 */
public class LoginActivity extends Activity {

  private static final int RC_SIGN_IN = 100;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //Checking if the user is already logged in
    FirebaseAuth auth = FirebaseAuth.getInstance();
    if (auth.getCurrentUser() != null) {
      Intent intent = new Intent(this, HomeActivity.class);
      startActivity(intent);
      finish();
    }

    startActivityForResult(
        AuthUI.getInstance().createSignInIntentBuilder()
            .setLogo(R.mipmap.ic_launcher)
            .setProviders(AuthUI.EMAIL_PROVIDER,
                AuthUI.FACEBOOK_PROVIDER,
                AuthUI.GOOGLE_PROVIDER)
            .build(),
        RC_SIGN_IN);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RC_SIGN_IN) {
      if (resultCode == RESULT_OK) {
        // user is signed in!
        startActivity(new Intent(this, HomeActivity.class));
        finish();
      } else {
        // user is not signed in. Maybe just wait for the user to press
        // "sign in" again, or show a message
        Log.d("FAIL", "FAIL");
      }
    }
  }
}
