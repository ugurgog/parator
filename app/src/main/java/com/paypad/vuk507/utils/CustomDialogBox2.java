package com.paypad.vuk507.utils;


import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.paypad.vuk507.R;
import com.paypad.vuk507.interfaces.CustomDialogListener;

import java.util.Objects;


public class CustomDialogBox2 {

    private CustomDialogBox2(CustomDialogBox2.Builder builder) {
        String title = builder.title;
        String message = builder.message;
        Activity activity = builder.activity;
        CustomDialogListener pListener = builder.pListener;
        CustomDialogListener nListener = builder.nListener;
        int pBtnColor = builder.pBtnColor;
        int nBtnColor = builder.nBtnColor;
        int pBtnVisibleType = builder.pBtnVisibleType;
        int nBtnVisibleType = builder.nBtnVisibleType;
        int editTextVisibleType = builder.editTextVisibleType;
        String positiveBtnText = builder.positiveBtnText;
        String negativeBtnText = builder.negativeBtnText;
        boolean cancel = builder.cancel;
        long durationTime = builder.durationTime;
    }

    public static class Builder {
        private String title;
        private String message;
        private String positiveBtnText;
        private String negativeBtnText;
        private int pBtnColor;
        private int nBtnColor;
        private int pBtnVisibleType;
        private int nBtnVisibleType;
        private int editTextVisibleType;
        private Activity activity;
        private CustomDialogListener pListener;
        private CustomDialogListener nListener;
        private boolean cancel;
        private long durationTime;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public CustomDialogBox2.Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public CustomDialogBox2.Builder setEditTextVisibility(int visibleType) {
            this.editTextVisibleType = visibleType;
            return this;
        }

        public CustomDialogBox2.Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public CustomDialogBox2.Builder setPositiveBtnText(String positiveBtnText) {
            this.positiveBtnText = positiveBtnText;
            return this;
        }

        public CustomDialogBox2.Builder setPositiveBtnBackground(int pBtnColor) {
            this.pBtnColor = pBtnColor;
            return this;
        }

        public CustomDialogBox2.Builder setNegativeBtnText(String negativeBtnText) {
            this.negativeBtnText = negativeBtnText;
            return this;
        }

        public CustomDialogBox2.Builder setNegativeBtnBackground(int nBtnColor) {
            this.nBtnColor = nBtnColor;
            return this;
        }

        public CustomDialogBox2.Builder setPositiveBtnVisibility(int visibleType) {
            this.pBtnVisibleType = visibleType;
            return this;
        }

        public CustomDialogBox2.Builder setNegativeBtnVisibility(int visibleType) {
            this.nBtnVisibleType = visibleType;
            return this;
        }


        public CustomDialogBox2.Builder setDurationTime(long durationTime) {
            this.durationTime = durationTime;
            return this;
        }

        public CustomDialogBox2.Builder OnPositiveClicked(CustomDialogListener pListener) {
            this.pListener = pListener;
            return this;
        }

        public CustomDialogBox2.Builder OnNegativeClicked(CustomDialogListener nListener) {
            this.nListener = nListener;
            return this;
        }

        public CustomDialogBox2.Builder isCancellable(boolean cancel) {
            this.cancel = cancel;
            return this;
        }

        public CustomDialogBox2 build() {
            final Dialog dialog = new Dialog(this.activity);
            dialog.requestWindowFeature(1);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));
            dialog.setCancelable(this.cancel);
            dialog.setContentView(R.layout.layout_custom_dialog_box2);
            TextView title1 = dialog.findViewById(R.id.title);
            TextView message1 = dialog.findViewById(R.id.message);
            TextView negTv = dialog.findViewById(R.id.negativeTv);
            TextView posTv = dialog.findViewById(R.id.positiveTv);
            View buttonsView = dialog.findViewById(R.id.buttonsView);

            negTv.setVisibility(nBtnVisibleType);
            posTv.setVisibility(pBtnVisibleType);

            if(nBtnVisibleType == View.GONE || pBtnVisibleType == View.GONE)
                buttonsView.setVisibility(View.GONE);

            if (message != null && !message.isEmpty())
                message1.setText(this.message);
            else
                message1.setVisibility(View.GONE);

            if (title != null && !title.isEmpty())
                title1.setText(this.title);
            else
                title1.setVisibility(View.GONE);

            if (pBtnColor != 0) {
                //GradientDrawable bgShape = (GradientDrawable) pBtn.getBackground();
                //bgShape.setColor(pBtnColor);

                posTv.setBackgroundColor(pBtnColor);
            }
            if (nBtnColor != 0) {
                //GradientDrawable bgShape = (GradientDrawable) nBtn.getBackground();
                //bgShape.setColor(nBtnColor);

                negTv.setBackgroundColor(nBtnColor);
            }

            if (this.positiveBtnText != null) {
                posTv.setText(this.positiveBtnText);
            }

            if (this.negativeBtnText != null) {
                negTv.setText(this.negativeBtnText);
            }

            if (this.pListener != null) {
                posTv.setOnClickListener(view -> {
                    Builder.this.pListener.OnClick();
                    dialog.dismiss();
                });
            } else {
                posTv.setOnClickListener(view -> dialog.dismiss());
            }

            if (this.nListener != null) {
                negTv.setVisibility(View.VISIBLE);
                posTv.setOnClickListener(view -> {
                    Builder.this.nListener.OnClick();
                    dialog.dismiss();
                });
            }

            dialog.show();

            if (this.durationTime > 0) {
                new Handler().postDelayed(() -> {
                    if (dialog.isShowing())
                        dialog.dismiss();
                }, this.durationTime);
            }
            return new CustomDialogBox2(this);
        }
    }
}
