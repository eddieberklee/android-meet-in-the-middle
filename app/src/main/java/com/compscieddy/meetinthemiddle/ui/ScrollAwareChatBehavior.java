package com.compscieddy.meetinthemiddle.ui;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by ambar on 6/18/16.
 */

public class ScrollAwareChatBehavior extends CoordinatorLayout.Behavior {

  public ScrollAwareChatBehavior(Context context, AttributeSet attrs) {
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
      // Scrolling down. Hide the layout.
      CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
      int layout_bottomMargin = layoutParams.bottomMargin;
      child.animate().translationY(child.getHeight() + layout_bottomMargin).setInterpolator(new LinearInterpolator()).start();
    } else if (dyConsumed < 0) {
      // Scrolling up. Display the layout.
      child.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
    }

    if (target instanceof RecyclerView){
      final RecyclerView recyclerView = (RecyclerView) target;
      LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
      // Check if we've reached the bottom of RecyclerView, and display layout if so.
      if (linearLayoutManager.findLastVisibleItemPosition() == recyclerView.getAdapter().getItemCount() - 1){
        child.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
      }

    }
  }
}
