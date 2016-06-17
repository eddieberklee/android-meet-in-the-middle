package com.compscieddy.meetinthemiddle.model;

import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by elee on 6/15/16.
 */
@IgnoreExtraProperties
public class Group {

  public String groupKey;
  public String groupTitle;
  public Set<String> groupUserIds; // this could just be phone numbers, that would be so convenient - guaranteed user uniqueness

  public Group() {}

  public Group(String groupKey, @Nullable String groupTitle, @Nullable Set<String> groupUserIds) {
    this.groupKey = groupKey;
    if (this.groupTitle != null) {
      this.groupTitle = groupTitle;
    }
    if (this.groupUserIds != null) {
      this.groupUserIds = groupUserIds;
    }
  }

  public String getKey() {
    return groupKey;
  }
  public String getGroupTitle() {
    return groupTitle;
  }
  public void setGroupTitle(String newGroupTitle) {
    groupTitle = newGroupTitle;
  }
  public Set<String> getGroupUserIds() {
    return groupUserIds;
  }

  public void update() {
    Map<String, Object> fields = toMap();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    database.getReference("groups").child(getKey()).updateChildren(fields);
  }

  @Exclude
  public Map<String, Object> toMap() {
    HashMap<String, Object> result = new HashMap<>();
    result.put("groupKey", groupKey);
    result.put("groupTitle", groupTitle);
    result.put("groupUserIds", groupUserIds);
    return result;
  }


}
