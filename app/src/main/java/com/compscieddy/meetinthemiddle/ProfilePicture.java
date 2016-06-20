package com.compscieddy.meetinthemiddle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.compscieddy.eddie_utils.Lawg;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfilePicture extends AppCompatActivity {

  private static final Lawg lawg = Lawg.newInstance(ProfilePicture.class.getSimpleName());

  //ImageView profilePictureView;

  @Bind(R.id.profile_picture) ImageView profilePictureView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile_picture);

    //profilePictureView = (ImageView) findViewById(R.id.profile_picture);
    ButterKnife.bind(ProfilePicture.this);

    downloadAvatar();

/*
    try {
      Bitmap mBitmap = getFacebookProfilePicture(id);
      profilePictureView.setImageBitmap(mBitmap);
    } catch (IOException e) {
      e.printStackTrace();
    }
*/

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_profile_picture, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

/*  public static Bitmap getFacebookProfilePicture(String userID) throws IOException {

    URL imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
    Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

    return bitmap;
  }*/

  private synchronized void downloadAvatar() {

    AsyncTask<Void, Void, Bitmap> task = new AsyncTask<Void, Void, Bitmap>() {

      //TODO: DYNAMICALLY CHOOSE THISE FACEBOOK userID IF THEY LOG IN WITH FACEBOOK
      String userId = "705621855";
      //String userId = me.getCurrentProfile().getId()
      @Override
      public Bitmap doInBackground(Void... params) {
        URL fbAvatarUrl = null;
        Bitmap fbAvatarBitmap = null;
        try {
          fbAvatarUrl = new URL("https://graph.facebook.com/"+userId+"/picture?type=large");
          fbAvatarBitmap = BitmapFactory.decodeStream(fbAvatarUrl.openConnection().getInputStream());
        } catch (MalformedURLException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
        return fbAvatarBitmap;
      }

      @Override
      protected void onPostExecute(Bitmap result) {
        profilePictureView.setImageBitmap(result);
      }

    };
    task.execute();
  }
}
