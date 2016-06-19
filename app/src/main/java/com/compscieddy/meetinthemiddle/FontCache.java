package com.compscieddy.meetinthemiddle;

import android.content.Context;
import android.graphics.Typeface;

import com.compscieddy.eddie_utils.Lawg;

import java.util.HashMap;

/**
 * Created by elee on 1/7/16.
 */
public class FontCache {

  private static final Lawg lawg = Lawg.newInstance(FontCache.class.getSimpleName());

  public static final int MONTSERRAT_HAIRLINE   = 0;
  public static final int MONTSERRAT_ULTRALIGHT = 1;
  public static final int MONTSERRAT_LIGHT      = 2;
  public static final int MONTSERRAT_REGULAR    = 3;
  public static final int MONTSERRAT_SEMIBOLD   = 4;
  public static final int MONTSERRAT_BOLD       = 5;
  public static final int MONTSERRAT_EXTRABOLD  = 6;
  public static final int MONTSERRAT_BLACK      = 7;

  private static HashMap<Integer, Typeface> fontCache = new HashMap<>();

  public static Typeface get(Context context, int id) {
    Typeface tf = fontCache.get(id);
    if (tf == null) {
      String path = "";
      switch (id) {
        case MONTSERRAT_HAIRLINE:
          path = "Montserrat-Hairline.otf";
          break;
        case MONTSERRAT_ULTRALIGHT:
          path = "Montserrat-UltraLight.otf";
          break;
        case MONTSERRAT_LIGHT:
          path = "Montserrat-Light.otf";
          break;
        case MONTSERRAT_REGULAR:
          path = "Montserrat-Regular.otf";
          break;
        case MONTSERRAT_SEMIBOLD:
          path = "Montserrat-SemiBold.otf";
          break;
        case MONTSERRAT_BOLD:
          path = "Montserrat-Bold.otf";
          break;
        case MONTSERRAT_EXTRABOLD:
          path = "Montserrat-ExtraBold.otf";
          break;
        case MONTSERRAT_BLACK:
          path = "Montserrat-Black.otf";
          break;
      }
      tf = Typeface.createFromAsset(context.getAssets(), path);
      fontCache.put(id, tf);
    }
    return tf;
  }
}
