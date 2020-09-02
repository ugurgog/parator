package com.paypad.parator.uiUtils.tutorial;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.paypad.parator.R;
import com.paypad.parator.interfaces.CustomDialogListener;
import com.paypad.parator.uiUtils.keypad.KeyPadClick;
import com.paypad.parator.utils.CustomDialogBoxVert;

import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_CONTINUE;
import static com.paypad.parator.constants.CustomConstants.WALK_THROUGH_END;

public class Tutorial extends RelativeLayout {

    private RelativeLayout tutorialRl;
    private TextView tutorialMsgTv;
    private ImageView closeTutorialImgv;

    private KeyPadClick listener;
    private TableLayout tableLayout;
    private Context mContext;
    private WalkthroughCallback walkthroughCallback;

    public Tutorial(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        inflateNumpad(context);
    }

    public void setWalkthroughCallback(WalkthroughCallback walkthroughCallback) {
        this.walkthroughCallback = walkthroughCallback;
    }

    public void setTutorialMessage(String text){
        tutorialMsgTv.setText(text);
    }

    public void setLayoutVisibility(int visibility){
        tutorialRl.setVisibility(visibility);
    }

    private void inflateNumpad(Context context) {
        View view = inflate(context, R.layout.layout_tutorial_message, this);
        initialiseWidgets(view);
    }

    private void initialiseWidgets(View view) {
        tutorialRl = view.findViewById(R.id.tutorialRl);
        tutorialMsgTv = view.findViewById(R.id.tutorialMsgTv);
        closeTutorialImgv = view.findViewById(R.id.closeTutorialImgv);

        closeTutorialImgv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new CustomDialogBoxVert.Builder((Activity) getContext())
                        .setTitle(mContext.getResources().getString(R.string.dont_need_help))
                        .setMessage(mContext.getResources().getString(R.string.dont_need_help_message))
                        .setNegativeBtnVisibility(View.VISIBLE)
                        .setPositiveBtnVisibility(View.VISIBLE)
                        .setPositiveBtnText(getContext().getResources().getString(R.string.continue_walkthrough))
                        .setNegativeBtnText(getContext().getResources().getString(R.string.end_walkthrough))
                        .setPositiveBtnBackground(getContext().getResources().getColor(R.color.Green, null))
                        .setNegativeBtnBackground(getContext().getResources().getColor(R.color.custom_btn_bg_color, null))
                        .setDurationTime(0)
                        .isCancellable(false)
                        .setEdittextVisibility(View.GONE)
                        .setpBtnTextColor(getContext().getResources().getColor(R.color.White, null))
                        .setnBtnTextColor(getContext().getResources().getColor(R.color.Green, null))
                        .OnPositiveClicked(new CustomDialogListener() {
                            @Override
                            public void OnClick() {
                                walkthroughCallback.OnWalkthroughResult(WALK_THROUGH_CONTINUE);
                            }
                        }).OnNegativeClicked(new CustomDialogListener() {
                        @Override
                            public void OnClick() {
                               walkthroughCallback.OnWalkthroughResult(WALK_THROUGH_END);
                               tutorialRl.setVisibility(GONE);
                            }
                        }).build();
            }
        });
    }
}