package com.compscieddy.meetinthemiddle.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.compscieddy.meetinthemiddle.util.Lawg;

/**
 * Created by elee on 6/8/16.
 * Based off http://stackoverflow.com/a/26201117/4326052
 */
public class CropCircleFrameLayout extends FrameLayout {

  private final static Lawg L = Lawg.newInstance(CropCircleFrameLayout.class.getSimpleName());

  private Paint paint;
  private Paint maskPaint;
  private Bitmap maskBitmap;

  public CropCircleFrameLayout(Context context) {
    super(context, null, 0);
    init(context, null, 0);
  }

  public CropCircleFrameLayout(Context context, AttributeSet attrs) {
    super(context, attrs, 0);
    init(context, attrs, 0);
  }

  public CropCircleFrameLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context, attrs, defStyle);
  }

  public void init(Context context, AttributeSet attrs, int defStyle) {
//    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ForadayTextView);
//    int typefaceId = ta.getInt(R.styleable.ForadayTextView_fontface, FontCache.MONTSERRAT_REGULAR);
//    setTypeface(FontCache.get(context, typefaceId));
//    ta.recycle();

    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    setWillNotDraw(false);

  }

  @Override
  public void draw(Canvas canvas) {
    Bitmap offscreenBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas offscreenCanvas = new Canvas(offscreenBitmap);

    super.draw(offscreenCanvas);

    if (maskBitmap == null) {
      maskBitmap = createMask(canvas.getWidth(), canvas.getHeight());
    }

    offscreenCanvas.drawBitmap(maskBitmap, 0, 0, maskPaint);
    canvas.drawBitmap(offscreenBitmap, 0, 0, paint);
  }

  private Bitmap createMask(int width, int height) {
    Bitmap mask = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
    Canvas canvas = new Canvas(mask);
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setColor(Color.WHITE);
    canvas.drawCircle(width / 2f, height / 2f, width / 2f, paint);
    return mask;
  }

}

