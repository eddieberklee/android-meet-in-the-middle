package com.compscieddy.meetinthemiddle.model;

import android.graphics.Bitmap;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.meetinthemiddle.util.Lawg;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by elee on 6/15/16.
 */
@IgnoreExtraProperties
public class User {

  private static final Lawg lawg = Lawg.newInstance(User.class.getSimpleName());

  public String email;
  public String name;
  public Map<String, Boolean> groups = new HashMap<>();
  public int loyaltyPoints;
  // TODO: don't forget to update toMap() for new fields
  @Exclude
  public Bitmap profilePictureBitmap;

  public User() {}

  public User(String email, String name) {
    this.email = email;
    this.name = name;
    this.loyaltyPoints = 0;
  }

  public String getKey() {
    return Etils.encodeEmail(email);
  }

  public int getLoyaltyPoints() { return loyaltyPoints; }
  public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }
  public Bitmap getProfilePictureBitmap() { return profilePictureBitmap; }
  public void setProfilePictureBitmap(Bitmap bitmap) { profilePictureBitmap = bitmap; }
  public void incrementLoyaltyPoints() {
    loyaltyPoints += 1;
  }

  public void addGroup(String groupKey) {
    groups.put(groupKey, true);
  }

  public void update() {
    Map<String, Object> fields = toMap();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    database.getReference("users").child(getKey()).updateChildren(fields);
  }

  @Exclude
  public Map<String, Object> toMap() {
    HashMap<String, Object> result = new HashMap<>();
    result.put("email", email);
    result.put("name", name);
    result.put("groups", groups);
    result.put("loyaltyPoints", loyaltyPoints);
    return result;
  }

  /********************* STATIC METHODS **************************/

  public static User createUser(FirebaseDatabase firebaseDatabase, FirebaseUser firebaseUser) {
    String encodedEmail = Etils.encodeEmail(firebaseUser.getEmail());
    String name = firebaseUser.getDisplayName();
    DatabaseReference userReference = firebaseDatabase.getReference("users").child(encodedEmail);
    User user = new User(encodedEmail, name);
    userReference.setValue(user);
    return user;
  }

}
