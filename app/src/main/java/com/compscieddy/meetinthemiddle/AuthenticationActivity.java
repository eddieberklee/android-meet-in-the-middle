package com.compscieddy.meetinthemiddle;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.eddie_utils.Lawg;
import com.compscieddy.meetinthemiddle.model.User;
import com.compscieddy.meetinthemiddle.util.FirebaseUtil;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ambar on 6/15/16.
 */
public class AuthenticationActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

  private static final Lawg lawg = Lawg.newInstance(AuthenticationActivity.class.getSimpleName());
  private static final int RC_SIGN_IN = 100;
  private static final String TAG_INTERNET_ERROR = "tag_internet_error";
  private String encodedEmail = null;

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

    //User isn't logged in, so check if he has working internet
    if (isInternetAvailable()) {
      startActivityForResult(
          AuthUI.getInstance().createSignInIntentBuilder()
              .setLogo(R.mipmap.ic_launcher)
              .setProviders(AuthUI.EMAIL_PROVIDER,
                  AuthUI.FACEBOOK_PROVIDER,
                  AuthUI.GOOGLE_PROVIDER)
              .build(),
          RC_SIGN_IN);
      finish();
    } else {
      FragmentManager fm = getSupportFragmentManager();
      InternetErrorFragment internetErrorFragment = InternetErrorFragment.newInstance();
      internetErrorFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppDialogTheme);
      internetErrorFragment.setCancelable(false);
      internetErrorFragment.show(fm, TAG_INTERNET_ERROR);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RC_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      if (resultCode == RESULT_OK) {
        // User creation is occurring! Tada! Welcome to being trapped to the most addictive map app ever
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        User.createUser(database, firebaseUser);
        encodedEmail = Etils.encodeEmail(firebaseUser.getEmail());

        GoogleSignInAccount signInAccount = result.getSignInAccount();
        if (signInAccount == null) {
          lawg.e("Sign-in account couldn't be retrieved");
        } else {
          Uri photoUri = signInAccount.getPhotoUrl();
          new DownloadProfilePictureTask().execute(photoUri);
        }

        startActivity(new Intent(this, HomeActivity.class));
        finish();
      } else {
        // user is not signed in. Maybe just wait for the user to press
        // "sign in" again, or show a message
        lawg.e("FAIL 1");
        lawg.e(" requestCode: " + requestCode + " resultCode: " + resultCode);
        lawg.e(" result: " + result);
        Etils.showToast(AuthenticationActivity.this, "Failed to Sign-In");
      }
    } else {
      lawg.e("FAIL 2");
      lawg.e(" requestCode: " + requestCode + " resultCode: " + resultCode);
    }
  }

  private class DownloadProfilePictureTask extends AsyncTask<Uri, Void, Bitmap> {
    @Override
    protected Bitmap doInBackground(Uri... params) {
      Uri photoUri = params[0];
      Bitmap profileBitmap = null;
      try {
        InputStream inputStream = new URL(photoUri.toString()).openStream();
        profileBitmap = BitmapFactory.decodeStream(inputStream);
      } catch (MalformedURLException e) {
        lawg.e("Bad url " + e); e.printStackTrace();
      } catch (IOException e) {
        lawg.e("IOException " + e); e.printStackTrace();
      }
      return profileBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
      uploadBitmapToFirebase(bitmap);
    }

  }

  private void uploadBitmapToFirebase(Bitmap bitmap) {
    StorageReference storageReference = FirebaseUtil.getFirebaseStorageReference();
    if (TextUtils.isEmpty(encodedEmail)) {
      lawg.e("Yo the email is empty, what's going on");
    }
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteOutputStream);
    byte[] byteData = byteOutputStream.toByteArray();

    storageReference = storageReference.child("profile_pics").child(encodedEmail);
    UploadTask uploadTask = storageReference.putBytes(byteData);
    // https://firebase.google.com/docs/storage/android/upload-files#upload_from_data_in_memory
    uploadTask
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            // couldn't upload now, let's todo: postDelay a Runnable to try one more time
          }
        })
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
          @Override
          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
            Uri downloadUrl = taskSnapshot.getDownloadUrl();
          }
        });
  }

  private boolean isInternetAvailable() {
    ConnectivityManager connMgr = (ConnectivityManager)
        getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    return (networkInfo != null && networkInfo.isConnected());
  }

  private boolean isFirstRun() {
    final String PREF_VERSION_CODE_KEY = "pref_version_code_key";
    final int DOESNT_EXIST = -1;

    // Get current version code
    int currentVersionCode = BuildConfig.VERSION_CODE;

    // Get saved version code
    SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

    // Check for first run or upgrade
    if (currentVersionCode == savedVersionCode) {

      // This is just a normal run
      return false;

    } else if (savedVersionCode == DOESNT_EXIST) {

      // This is a new install (or the user cleared the shared preferences)
      return true;

    } else if (currentVersionCode > savedVersionCode) {

      //This is an upgrade
       return false;
    }

    // Update the shared preferences with the current version code
    prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).commit();
    return false;

  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    startActivityForResult(
          AuthUI.getInstance().createSignInIntentBuilder()
              .setLogo(R.mipmap.ic_launcher)
              .setProviders(AuthUI.EMAIL_PROVIDER,
                  AuthUI.FACEBOOK_PROVIDER,
                  AuthUI.GOOGLE_PROVIDER)
              .build(),
          RC_SIGN_IN);
      finish();
  }
}
