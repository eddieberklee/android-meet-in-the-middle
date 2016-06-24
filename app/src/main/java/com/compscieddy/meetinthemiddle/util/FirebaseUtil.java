package com.compscieddy.meetinthemiddle.util;

import com.compscieddy.meetinthemiddle.model.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by elee on 6/23/16.
 */

public class FirebaseUtil {

  public static StorageReference getFirebaseStorageReference() {
    StorageReference storageReference = FirebaseStorage.getInstance()
        .getReferenceFromUrl("gs://project-6276882918492215981.appspot.com");
    return storageReference;
  }

  public static StorageReference getProfilePictureStorageRef(User user) {
    StorageReference storageReference = FirebaseUtil.getFirebaseStorageReference();
    storageReference = storageReference.child("profile_pics").child(user.getKey());
    return storageReference;
  }

}
