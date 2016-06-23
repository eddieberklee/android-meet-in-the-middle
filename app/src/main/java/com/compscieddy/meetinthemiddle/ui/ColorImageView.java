package com.compscieddy.meetinthemiddle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.meetinthemiddle.R;

/**
 * Created by elee on 1/25/16.
 */
public class ColorImageView extends ImageView {

  /** I love StackOverflow
   *  http://stackoverflow.com/questions/35003312/am-i-applying-the-colorfilter-in-the-right-place-in-my-custom-imageview
   *  except the answer didn't work so hopefully someone adds another answer soon
   */

  private int mColor = -1;

  public ColorImageView(Context context) {
    super(context);
  }
  public ColorImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ColorImageView);
    mColor = ta.getColor(R.styleable.ColorImageView_customColor, -1);

    if (mColor != -1) {
      Drawable drawable = getDrawable();
      if (drawable != null) {
        Etils.applyColorFilter(drawable, mColor, true);
      }
    }

    ta.recycle();
  }

  @Override
  public void setImageDrawable(Drawable drawable) {
    super.setImageDrawable(drawable);
    
    if (mColor != -1) {
      Etils.applyColorFilter(getDrawable(), mColor, true);
    }
  }
}
