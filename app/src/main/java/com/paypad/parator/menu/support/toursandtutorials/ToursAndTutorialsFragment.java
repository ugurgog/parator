package com.paypad.parator.menu.support.toursandtutorials;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.enums.SupportListEnum;
import com.paypad.parator.enums.TutorialTypeEnum;
import com.paypad.parator.interfaces.SupportListItemCallback;
import com.paypad.parator.interfaces.TutorialSelectedCallback;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ToursAndTutorialsFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.firstSalell)
    LinearLayout firstSalell;
    @BindView(R.id.firstItemll)
    LinearLayout firstItemll;
    @BindView(R.id.firstTaxll)
    LinearLayout firstTaxll;
    @BindView(R.id.firstUnitll)
    LinearLayout firstUnitll;
    @BindView(R.id.firstDiscountll)
    LinearLayout firstDiscountll;
    @BindView(R.id.firstCategoryll)
    LinearLayout firstCategoryll;

    private Context mContext;
    private TutorialSelectedCallback tutorialSelectedCallback;

    public ToursAndTutorialsFragment() {

    }

    public void setTutorialSelectedCallback(TutorialSelectedCallback tutorialSelectedCallback) {
        this.tutorialSelectedCallback = tutorialSelectedCallback;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_tours_and_tutorials, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            initListeners();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initListeners() {
        backImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) mContext).onBackPressed();
            }
        });

        firstSalell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutorialSelectedCallback.OnSelectedTutorial(TutorialTypeEnum.TUTORIAL_PAYMENT);
                mFragmentNavigation.popFragments(2);
            }
        });

        firstItemll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutorialSelectedCallback.OnSelectedTutorial(TutorialTypeEnum.TUTORIAL_CREATE_ITEM);
                mFragmentNavigation.popFragments(2);
            }
        });

        firstTaxll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutorialSelectedCallback.OnSelectedTutorial(TutorialTypeEnum.TUTORIAL_CREATE_TAX);
                mFragmentNavigation.popFragments(2);
            }
        });

        firstUnitll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutorialSelectedCallback.OnSelectedTutorial(TutorialTypeEnum.TUTORIAL_CREATE_UNIT);
                mFragmentNavigation.popFragments(2);
            }
        });

        firstCategoryll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutorialSelectedCallback.OnSelectedTutorial(TutorialTypeEnum.TUTORIAL_CREATE_CATEGORY);
                mFragmentNavigation.popFragments(2);
            }
        });

        firstDiscountll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutorialSelectedCallback.OnSelectedTutorial(TutorialTypeEnum.TUTORIAL_CREATE_DISCOUNT);
                mFragmentNavigation.popFragments(2);
            }
        });
    }

    private void initVariables() {
        toolbarTitleTv.setText(mContext.getResources().getString(R.string.tours_and_tutorials));
    }

}