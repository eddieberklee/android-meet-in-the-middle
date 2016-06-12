package com.compscieddy.meetinthemiddle;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by Darren on 12-Jun-16.
 */
public class ResizeAnimation extends Animation {
  final int targetHeight;
  View view;
  int startHeight;

  public ResizeAnimation(View view, int targetHeight, int startHeight) {
    this.view = view;
    this.targetHeight = targetHeight;
    this.startHeight = startHeight;
  }

  @Override
  protected void applyTransformation(float interpolatedTime, Transformation t) {
    int newHeight = (int) (startHeight+(targetHeight - startHeight) * interpolatedTime);
    view.getLayoutParams().height = newHeight;
    view.requestLayout();
  }

  @Override
  public void initialize(int width, int height, int parentWidth, int parentHeight) {
    super.initialize(width, height, parentWidth, parentHeight);
  }

  @Override
  public boolean willChangeBounds() {
    return true;
  }
}