package com.compscieddy.meetinthemiddle.model;

import com.compscieddy.meetinthemiddle.util.Lawg;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by elee on 6/15/16.
 */
@IgnoreExtraProperties
public class Chat {
  private static final Lawg L = Lawg.newInstance(Chat.class.getSimpleName());

  String key;
  String groupKey;
  String userKey;
  String chatMessage;
  Map<String, String> timestamp;

  // TODO: don't forget to update toMap() for new fields

  public Chat() {}

  public Chat(String key, String groupKey, String userKey, String message) {
    this.key = key;
    this.groupKey = groupKey;
    this.userKey = userKey;
    this.chatMessage = message;
    this.timestamp = ServerValue.TIMESTAMP;
  }

  public String getKey() {
    return key;
  }

  public String getGroupKey() {
    return groupKey;
  }

  public String getUserKey() {
    return userKey;
  }

  public String getChatMessage() {
    return chatMessage;
  }

  public void update() {
    Map<String, Object> fields = toMap();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    database.getReference("chats").child(getKey()).updateChildren(fields);
  }

  @Exclude
  public Map<String, Object> toMap() {
    HashMap<String, Object> result = new HashMap<>();
    result.put("key", key);
    result.put("key", groupKey);
    result.put("userKey", userKey);
    result.put("chatMessage", chatMessage);
    result.put("timestamp", timestamp);
    return result;
  }

}
