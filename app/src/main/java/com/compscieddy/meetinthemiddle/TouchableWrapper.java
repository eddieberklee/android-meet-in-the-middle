package com.compscieddy.meetinthemiddle;

import android.content.Context;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by ambar on 6/23/16.
 */
public  class TouchableWrapper extends FrameLayout {

  private long lastTouched = 0;
  private static final long SCROLL_TIME = 100L; // 100 Milliseconds
  private UpdateMapAfterUserInteraction updateMapAfterUserInteraction;

  public TouchableWrapper(Context context) {
    super(context);
    // Force the host activity to implement the UpdateMapAfterUserInteraction Interface
    try {
      updateMapAfterUserInteraction = (GroupActivity) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString() + " must implement UpdateMapAfterUserInterection");
    }
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        lastTouched = SystemClock.uptimeMillis();
        break;
      case MotionEvent.ACTION_UP:
        final long now = SystemClock.uptimeMillis();
        if (now - lastTouched > SCROLL_TIME) {
          // Update the map
          updateMapAfterUserInteraction.onUpdateMapAfterUserInteraction();
        }
        break;
    }
    return super.dispatchTouchEvent(ev);
  }

  // Map Activity must implement this interface
  public interface UpdateMapAfterUserInteraction {
    void onUpdateMapAfterUserInteraction();
  }
}
