package com.compscieddy.meetinthemiddle.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by elee on 6/15/16.
 */
@IgnoreExtraProperties
public class Chat {

    String name;
    String text;
    String uid;

    public Chat() {}

    public Chat(String name, String uid, String message) {
      this.name = name;
      this.text = message;
      this.uid = uid;
    }

    public String getName() {
      return name;
    }

    public String getUid() {
      return uid;
    }

    public String getText() {
      return text;
    }
}
