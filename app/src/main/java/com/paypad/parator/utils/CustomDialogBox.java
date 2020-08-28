package com.paypad.parator.utils;


import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.paypad.parator.R;
import com.paypad.parator.interfaces.CustomDialogBoxTextListener;
import com.paypad.parator.interfaces.CustomDialogListener;

import java.util.Objects;


public class CustomDialogBox {

    private CustomDialogBox(CustomDialogBox.Builder builder) {
        String title = builder.title;
        String message = builder.message;
        Activity activity = builder.activity;
        CustomDialogListener pListener = builder.pListener;
        CustomDialogListener nListener = builder.nListener;
        CustomDialogBoxTextListener textListener = builder.textListener;
        int pBtnColor = builder.pBtnColor;
        int nBtnColor = builder.nBtnColor;
        int pBtnVisibleType = builder.pBtnVisibleType;
        int nBtnVisibleType = builder.nBtnVisibleType;
        String edittextHint = builder.edittextHint;
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
        private int pEtVisibleType = View.GONE;
        private String edittextHint;
        private Activity activity;
        private CustomDialogListener pListener;
        private CustomDialogListener nListener;
        private CustomDialogBoxTextListener textListener;
        private boolean cancel;
        private long durationTime;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public CustomDialogBox.Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public CustomDialogBox.Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public CustomDialogBox.Builder setPositiveBtnText(String positiveBtnText) {
            this.positiveBtnText = positiveBtnText;
            return this;
        }

        public CustomDialogBox.Builder setEdittextHint(String hint) {
            this.edittextHint = hint;
            return this;
        }

        public CustomDialogBox.Builder setPositiveBtnBackground(int pBtnColor) {
            this.pBtnColor = pBtnColor;
            return this;
        }

        public CustomDialogBox.Builder setNegativeBtnText(String negativeBtnText) {
            this.negativeBtnText = negativeBtnText;
            return this;
        }

        public CustomDialogBox.Builder setNegativeBtnBackground(int nBtnColor) {
            this.nBtnColor = nBtnColor;
            return this;
        }

        public CustomDialogBox.Builder setPositiveBtnVisibility(int visibleType) {
            this.pBtnVisibleType = visibleType;
            return this;
        }

        public CustomDialogBox.Builder setNegativeBtnVisibility(int visibleType) {
            this.nBtnVisibleType = visibleType;
            return this;
        }

        public CustomDialogBox.Builder setEdittextVisibility(int visibleType) {
            this.pEtVisibleType = visibleType;
            return this;
        }

        public CustomDialogBox.Builder setDurationTime(long durationTime) {
            this.durationTime = durationTime;
            return this;
        }

        public CustomDialogBox.Builder OnPositiveClicked(CustomDialogListener pListener) {
            this.pListener = pListener;
            return this;
        }

        public CustomDialogBox.Builder OnNegativeClicked(CustomDialogListener nListener) {
            this.nListener = nListener;
            return this;
        }

        public CustomDialogBox.Builder OnTextReturn(CustomDialogBoxTextListener textListener) {
            this.textListener = textListener;
            return this;
        }

        public CustomDialogBox.Builder isCancellable(boolean cancel) {
            this.cancel = cancel;
            return this;
        }

        public CustomDialogBox build() {
            final Dialog dialog = new Dialog(this.activity);
            dialog.requestWindowFeature(1);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));
            dialog.setCancelable(this.cancel);
            dialog.setContentView(R.layout.layout_custom_dialog_box);
            TextView title1 = dialog.findViewById(R.id.title);
            TextView message1 = dialog.findViewById(R.id.message);
            Button nBtn = dialog.findViewById(R.id.negativeBtn);
            Button pBtn = dialog.findViewById(R.id.positiveBtn);
            EditText editText = dialog.findViewById(R.id.editText);
            View buttonsView = dialog.findViewById(R.id.buttonsView);

            nBtn.setVisibility(nBtnVisibleType);
            pBtn.setVisibility(pBtnVisibleType);
            editText.setVisibility(pEtVisibleType);

            if(this.edittextHint != null)
                editText.setHint(this.edittextHint);

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
                GradientDrawable bgShape = (GradientDrawable) pBtn.getBackground();
                bgShape.setColor(pBtnColor);
            }
            if (nBtnColor != 0) {
                GradientDrawable bgShape = (GradientDrawable) nBtn.getBackground();
                bgShape.setColor(nBtnColor);
            }

            if (this.positiveBtnText != null) {
                pBtn.setText(this.positiveBtnText);
            }

            if (this.negativeBtnText != null) {
                nBtn.setText(this.negativeBtnText);
            }

            if (this.pListener != null) {
                pBtn.setOnClickListener(view -> {
                    Builder.this.pListener.OnClick();

                    if(Builder.this.textListener != null)
                        Builder.this.textListener.OnTextReturn(editText.getText().toString());

                    dialog.dismiss();
                });
            } else {
                pBtn.setOnClickListener(view -> dialog.dismiss());
            }

            if (this.nListener != null) {
                nBtn.setVisibility(View.VISIBLE);
                nBtn.setOnClickListener(view -> {
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
            return new CustomDialogBox(this);
        }
    }
}
