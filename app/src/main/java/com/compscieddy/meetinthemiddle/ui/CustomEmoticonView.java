package com.compscieddy.meetinthemiddle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.compscieddy.meetinthemiddle.R;

/**
 * Created by Jet Wang on 2016/7/6.
 */
public class CustomEmoticonView extends ImageView{

    public enum Emotion{
        HAPPY, SAD, SHY, ANGRY, ASTONISHED, ECSTATIC
    }

    private Emotion e;
    private final Context mContext;
    public CustomEmoticonView(Context context) {
        this(context, null);
    }

    public CustomEmoticonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.CustomEmoticonView);
        int emoticonId = ta.getInt(R.styleable.CustomEmoticonView_emotion, 0);
        if (emoticonId >= 0 & emoticonId < Emotion.values().length){
            e = Emotion.values()[emoticonId];
        }
        setEmotion(e);
        ta.recycle();

    }

    public void setEmotion(Emotion e) {
        @DrawableRes int src = R.drawable.ic_emoticon_happy;
        if (e != null){
            switch (e){
                case HAPPY:
                    src = R.drawable.ic_emoticon_happy;
                    break;
                case SAD:
                    src = R.drawable.ic_emoticon_sad;
                    break;
                case SHY:
                    src = R.drawable.ic_emoticon_shy;
                    break;
                case ANGRY:
                    src = R.drawable.ic_emoticon_angry;
                    break;
                case ASTONISHED:
                    src = R.drawable.ic_emoticon_astonished;
                    break;
                case ECSTATIC:
                    src = R.drawable.ic_emoticon_ecstatic;
                    break;
            }
        }
        setImageDrawable(mContext.getResources().getDrawable(src));
    }


}
