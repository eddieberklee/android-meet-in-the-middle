package com.compscieddy.meetinthemiddle.model;

import com.compscieddy.meetinthemiddle.util.Lawg;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
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
  Date currentUTCTime;

  // TODO: don't forget to update toMap() for new fields

  public Chat() {}

  public Chat(String key, String groupKey, String userKey, String message, Date currentUTCTime) {
    this.key = key;
    this.groupKey = groupKey;
    this.userKey = userKey;
    this.chatMessage = message;
    this.currentUTCTime = currentUTCTime;
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

  public Date getCurrentUTCTime(){
    return currentUTCTime;
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
    result.put("groupKey", groupKey);
    result.put("userKey", userKey);
    result.put("chatMessage", chatMessage);
    result.put("currentUTCTime", currentUTCTime);
    return result;
  }

}
