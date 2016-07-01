package com.compscieddy.meetinthemiddle;

import android.content.Context;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by ambar on 6/23/16.
 */
public  class TouchableWrapper extends FrameLayout {

  private long lastTouchedMillis = 0L;
  private static final long MIN_SCROLL_THRESHOLD_MILLIS = 100L; // 100 Milliseconds
  private UserMapDrag userMapDrag;

  public TouchableWrapper(Context context) {
    super(context);
    // Force the host activity to implement the UpdateMapAfterUserInteraction Interface
    try {
      userMapDrag = (GroupActivity) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString() + " must implement UpdateMapAfterUserInterection");
    }
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        lastTouchedMillis = SystemClock.uptimeMillis();
        break;
      case MotionEvent.ACTION_UP:
        long nowMillis = SystemClock.uptimeMillis();
        if (nowMillis - lastTouchedMillis > MIN_SCROLL_THRESHOLD_MILLIS) {
          // User dragged the map, perform the action
          userMapDrag.onMapDrag();
        }
        break;
    }
    return super.dispatchTouchEvent(ev);
  }

  // Map Activity must implement this interface
  public interface UserMapDrag {
    void onMapDrag();
  }
}
