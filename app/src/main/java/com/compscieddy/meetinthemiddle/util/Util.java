package com.compscieddy.meetinthemiddle.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.compscieddy.meetinthemiddle.R;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by elee on 6/9/16.
 */
public class Util {

  private static final Lawg L = Lawg.newInstance(Util.class.getSimpleName());

  public static Bitmap getCroppedBitmap(Context context, Bitmap bitmap) {
    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
        bitmap.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(output);

    final int color = context.getResources().getColor(R.color.white);
    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(color);

    canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
        bitmap.getWidth() / 2, paint);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(bitmap, rect, rect, paint);

    return output;
  }

  public static void rotateView(View view, float angle){
    ViewCompat.animate(view)
        .rotationBy(angle)
        .withLayer()
        .setDuration(400)
        .start();
  }

  public static void rotateLocationActive(View view) {
    ViewCompat.animate(view)
        .rotation(0.0f)
        .withLayer()
        .setDuration(400)
        .start();
  }

  public static void rotateLocationInactive(View view) {
    ViewCompat.animate(view)
        .rotation(90.0f)
        .withLayer()
        .setDuration(400)
        .start();
  }

  public static Date getCurrentUTCTime(){
    // Set device's time zone to UTC
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Calendar calendar = Calendar.getInstance();
    return calendar.getTime();
  }

  public static Date getLocalTimeFromUTC(Date utcDate){
    // Get device's time zone's offset from UTC and add to UTC time
   return new Date(utcDate.getTime() + TimeZone.getDefault().getOffset(utcDate.getTime()));
  }
}
