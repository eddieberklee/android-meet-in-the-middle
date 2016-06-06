package com.compscieddy.meetinthemiddle;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by elee on 1/6/16.
 */
public class ForadayTextView extends TextView {

  private final Context mContext;

  public ForadayTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    init(attrs);
  }

  private void init(AttributeSet attrs) {
    if (isInEditMode()) return;

    TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.ForadayTextView);
    int typefaceId = ta.getInt(R.styleable.ForadayTextView_fontface, FontCache.MONTSERRAT_REGULAR);
    setCustomTypeFace(typefaceId);
    ta.recycle();

  }

  public void setCustomTypeFace(int typeFaceId) {
    setTypeface(FontCache.get(mContext, typeFaceId));
  }


}
