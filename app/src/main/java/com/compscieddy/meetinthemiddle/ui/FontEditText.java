package com.compscieddy.meetinthemiddle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.meetinthemiddle.FontCache;
import com.compscieddy.meetinthemiddle.R;
import com.compscieddy.meetinthemiddle.util.Lawg;

/**
 * Created by elee on 1/7/16.
 */
public class FontEditText extends EditText {

  private static final Lawg lawg = Lawg.newInstance(FontEditText.class.getSimpleName());

  public FontEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    if (isInEditMode()) return;

    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FontEditText);
    int typefaceId = ta.getInt(R.styleable.FontEditText_fontface, FontCache.MONTSERRAT_REGULAR);
    int color = ta.getInt(R.styleable.FontEditText_color, -1);
    ta.recycle();

    if (color != -1) {
      setAllColors(color);
    }
    setTypeface(FontCache.get(context, typefaceId));
  }

  public void setAlphaHighlightColor(int color) {
    this.setHighlightColor(Etils.setAlpha(color, 0.4f));
  }

  public void setAllColors(int color) {
    if (!(this.getBackground() instanceof GradientDrawable)) {
      Log.e("UFUCKEDUP", "Background is not a custom drawable!!! -Recheck your code");
    }
    this.setTextColor(color);
    setHintTextColor(color);

  }

}
