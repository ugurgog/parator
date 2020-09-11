package com.paypad.parator.menu.support.announcement;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.MainActivity;
import com.paypad.parator.R;
import com.paypad.parator.enums.SupportListEnum;
import com.paypad.parator.enums.TutorialTypeEnum;
import com.paypad.parator.interfaces.SupportListItemCallback;
import com.paypad.parator.interfaces.TutorialSelectedCallback;
import com.paypad.parator.menu.support.about.SupportAboutFragment;
import com.paypad.parator.menu.support.help.HelpFragment;
import com.paypad.parator.menu.support.reportproblem.fragments.NotifyProblemFragment;
import com.paypad.parator.menu.support.toursandtutorials.ToursAndTutorialsFragment;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.parator.constants.CustomConstants.LANGUAGE_TR;

public class AnnouncementFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;
    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.itemsRv)
    RecyclerView itemsRv;
    @BindView(R.id.noAnnouncementRl)
    RelativeLayout noAnnouncementRl;
    @BindView(R.id.announcementsRl)
    RelativeLayout announcementsRl;

    private Context mContext;

    public AnnouncementFragment() {

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
            mView = inflater.inflate(R.layout.fragment_announcement, container, false);
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
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
    }

    private void initVariables() {
        toolbarTitleTv.setText(mContext.getResources().getString(R.string.announcements));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        itemsRv.setLayoutManager(linearLayoutManager);
        setAdapter();

        if(1 == 1){
            announcementsRl.setVisibility(View.GONE);
            noAnnouncementRl.setVisibility(View.VISIBLE);
        }

    }

    private void setAdapter() {

    }
}