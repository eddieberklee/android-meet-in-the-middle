package com.compscieddy.meetinthemiddle.ui;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by ambar on 6/18/16.
 */

public class ScrollAwareViewBehavior extends CoordinatorLayout.Behavior {

  public ScrollAwareViewBehavior(Context context, AttributeSet attrs) {
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
      CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
      int fab_bottomMargin = layoutParams.bottomMargin;
      child.animate().translationY(child.getHeight() + fab_bottomMargin).setInterpolator(new LinearInterpolator()).start();
    } else if (dyConsumed < 0) {
      child.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
    }
  }
}
