package com.paypad.parator.charge.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.paypad.parator.R;
import com.paypad.parator.utils.CommonUtils;

public class AnimationUtil {

    static TextView animationTextView;

    public static void startAnimation(View startView, TextView saleCountTv, FrameLayout mainFl, Activity activity){

        int[] location = new int[2];
        startView.getLocationOnScreen(location);
        int x1 = location[0];
        int y1 = location[1];

        location = new int[2];
        saleCountTv.getLocationOnScreen(location);
        int x2 = location[0];
        int y2 = location[1];

        TranslateAnimation anim = null;
        if(startView instanceof TextView){
            anim = new TranslateAnimation(x1 + startView.getWidth()/2,
                    x2, y1 - startView.getHeight()
                    , y2 - saleCountTv.getHeight() + 13);
        }else if(startView instanceof FrameLayout){
            anim = new TranslateAnimation(x1 + startView.getWidth()/2,
                    x2, y1  , y2 - saleCountTv.getHeight() + 13);
        }

        if(anim == null) return;

        anim.setDuration(300);

        anim.setAnimationListener(new TranslateAnimation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                mainFl.removeView(animationTextView);
            }
        });

        if(animationTextView != null)
            mainFl.removeView(animationTextView);

        View view = LayoutInflater.from(activity).inflate(R.layout.layout_sale_count, null);

        animationTextView = (TextView) view;

        animationTextView.setVisibility(View.VISIBLE);
        animationTextView.setText("1");

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        animationTextView.setLayoutParams(params);

        params.height = CommonUtils.getPaddingInPixels(activity, 30f);
        params.width = CommonUtils.getPaddingInPixels(activity, 30f);
        animationTextView.setLayoutParams(params);

        mainFl.addView(animationTextView);
        view.startAnimation(anim);
    }
}
