package com.compscieddy.meetinthemiddle.model;

import android.support.annotation.Nullable;

import com.google.firebase.database.IgnoreExtraProperties;

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


}
