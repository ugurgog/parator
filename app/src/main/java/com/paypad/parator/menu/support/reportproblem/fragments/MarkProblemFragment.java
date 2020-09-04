package com.paypad.parator.menu.support.reportproblem.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.interfaces.ReturnObjectCallback;
import com.paypad.parator.menu.support.reportproblem.model.PaintView;
import com.paypad.parator.model.pojo.PhotoSelectUtil;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.ShapeUtil;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MarkProblemFragment extends BaseFragment {

    View mView;

    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.paintView)
    PaintView paintView;
    @BindView(R.id.markProblemLayout)
    RelativeLayout markProblemLayout;

    private PhotoSelectUtil photoSelectUtil;
    private ReturnObjectCallback returnObjectCallback;
    private Context mContext;

    public MarkProblemFragment(PhotoSelectUtil photoSelectUtil, ReturnObjectCallback returnObjectCallback) {
        this.photoSelectUtil = photoSelectUtil;
        this.returnObjectCallback = returnObjectCallback;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_mark_problem, container, false);
        ButterKnife.bind(this, mView);
        initVariables();
        addListeners();
        setCanvas();
        return mView;
    }

    private void setCanvas() {
        paintView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                paintView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                paintView.init(paintView.getWidth(), paintView.getHeight(), photoSelectUtil.getBitmap());
                paintView.normal();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initVariables() {
        toolbarTitleTv.setText(getResources().getString(R.string.circle_the_problem));
        markProblemLayout.setBackground(ShapeUtil.getShape(0,
                getResources().getColor(R.color.DodgerBlue, null), GradientDrawable.RECTANGLE, 0, 2));
    }

    public void addListeners() {
        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) mContext).onBackPressed();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoSelectUtil.setBitmap(paintView.getmBitmap());
                returnObjectCallback.OnReturn(photoSelectUtil);
                ((Activity) mContext).onBackPressed();
            }
        });
    }

}
