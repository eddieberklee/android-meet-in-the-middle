package com.compscieddy.meetinthemiddle.model;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.eddie_utils.Lawg;
import com.compscieddy.meetinthemiddle.MitmApplication;
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

  private static final Lawg lawg = Lawg.newInstance(Group.class.getSimpleName());

  public String key;
  public String groupTitle;
  public Set<String> groupUserIds; // this could just be phone numbers, that would be so convenient - guaranteed user uniqueness

  public Group() {}

  public Group(String key, @Nullable String groupTitle, @Nullable Set<String> groupUserIds) {
    init(key, groupTitle, groupUserIds);
  }

  private void init(Group group) {
    init(
        group.key,
        group.groupTitle,
        group.groupUserIds
    );
  }
  private void init(String groupKey, @Nullable String groupTitle, @Nullable Set<String> groupUserIds) {
    this.key = groupKey;
    if (this.groupTitle != null) {
      this.groupTitle = groupTitle;
    }
    if (this.groupUserIds != null) {
      this.groupUserIds = groupUserIds;
    }
  }

  public String getKey() {
    return key;
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

  public void updateWith(Group updatedGroup) {
    if (!TextUtils.equals(this.key, updatedGroup.getKey())) {
      Etils.logAndToast(MitmApplication.getContext(), lawg, "Group being updated doesn't have same key " + key + " vs " + updatedGroup.getKey());
    }
    init(updatedGroup);
    update();
  }

  @Exclude
  public Map<String, Object> toMap() {
    HashMap<String, Object> result = new HashMap<>();
    result.put("key", key);
    result.put("groupTitle", groupTitle);
    result.put("groupUserIds", groupUserIds);
    return result;
  }


}
