package com.compscieddy.meetinthemiddle.ui;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;

import com.compscieddy.eddie_utils.Etils;

/**
 * Created by ambar on 6/18/16.
 */

public class ScrollAwareNewGroupButtonBehavior extends CoordinatorLayout.Behavior {

  private final static FastOutSlowInInterpolator FASTOUT_SLOWIN_INTERPOLATOR = new FastOutSlowInInterpolator();
  public ScrollAwareNewGroupButtonBehavior(Context context, AttributeSet attrs) {
    super();
  }

  @Override
  public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
    return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
  }

  @Override
  public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    if (dyConsumed > 0) {
      // Scrolling down. Hide the view.
      CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
      int view_bottomMargin = layoutParams.bottomMargin;
      int slightPeekOffset = Etils.dpToPx(40);
      child.animate().translationY(child.getHeight() + view_bottomMargin - slightPeekOffset).setInterpolator(FASTOUT_SLOWIN_INTERPOLATOR).start();
    } else if (dyConsumed < 0) {
      // Scrolling up. Display the view.
      child.animate().translationY(0).setInterpolator(FASTOUT_SLOWIN_INTERPOLATOR).start();
    }
  }
}
