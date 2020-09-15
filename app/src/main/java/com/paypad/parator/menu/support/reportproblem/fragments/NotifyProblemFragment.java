package com.paypad.parator.menu.support.reportproblem.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.FragmentControllers.FragNavController;
import com.paypad.parator.MainActivity;
import com.paypad.parator.R;
import com.paypad.parator.enums.ToastEnum;
import com.paypad.parator.interfaces.PhotoChosenForReportCallback;
import com.paypad.parator.interfaces.ReturnObjectCallback;
import com.paypad.parator.menu.support.reportproblem.model.ProblemNotifyModel;
import com.paypad.parator.model.pojo.PhotoSelectUtil;
import com.paypad.parator.utils.BitmapUtils;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.IntentSelectUtil;
import com.paypad.parator.utils.PermissionModule;
import com.paypad.parator.utils.ShapeUtil;
import com.paypad.parator.utils.dialogboxutil.DialogBoxUtil;
import com.paypad.parator.utils.dialogboxutil.interfaces.InfoDialogBoxCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.paypad.parator.constants.CustomConstants.GALLERY_TEXT;

public class NotifyProblemFragment extends BaseFragment {

    View mView;

    @BindView(R.id.cancelImgv)
    ClickableImageView cancelImgv;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;


    @BindView(R.id.noteTextEditText)
    EditText noteTextEditText;

    @BindView(R.id.addPhotoImgv1)
    ImageView addPhotoImgv1;
    @BindView(R.id.addPhotoImgv2)
    ImageView addPhotoImgv2;
    @BindView(R.id.addPhotoImgv3)
    ImageView addPhotoImgv3;
    @BindView(R.id.addPhotoImgv4)
    ImageView addPhotoImgv4;

    @BindView(R.id.imgDelete1)
    ImageView imgDelete1;
    @BindView(R.id.imgDelete2)
    ImageView imgDelete2;
    @BindView(R.id.imgDelete3)
    ImageView imgDelete3;
    @BindView(R.id.imgDelete4)
    ImageView imgDelete4;

    private Button screenShotApproveBtn;
    private Button screenShotCancelBtn;
    private RelativeLayout screenShotMainLayout;
    private LinearLayout mainll;

    private List<ProblemNotifyModel> problemListBox;
    private List<PhotoSelectUtil> photoSelectUtilList = new ArrayList<>();
    private PermissionModule permissionModule;

    private ImageView chosenImgv = null;
    private Context mContext;

    private static final int CODE_GALLERY_REQUEST = 665;

    public NotifyProblemFragment() {

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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_notify_problem, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            addListeners();
            setShapes();
            initProblemList();
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initVariables() {
        toolbarTitleTv.setText(getResources().getString(R.string.report_or_comment));
        permissionModule = new PermissionModule(getContext());
        screenShotApproveBtn = Objects.requireNonNull(getActivity()).findViewById(R.id.screenShotApproveBtn);
        screenShotCancelBtn = getActivity().findViewById(R.id.screenShotCancelBtn);
        screenShotMainLayout = getActivity().findViewById(R.id.screenShotMainLayout);
        mainll = getActivity().findViewById(R.id.mainll);
        MainActivity.notifyProblemFragment = this;
    }

    public void setShapes() {
        GradientDrawable shape = ShapeUtil.getShape(getResources().getColor(R.color.White, null),
                getResources().getColor(R.color.Gray, null), GradientDrawable.RECTANGLE, 15, 2);
        addPhotoImgv1.setBackground(shape);
        addPhotoImgv2.setBackground(shape);
        addPhotoImgv3.setBackground(shape);
        addPhotoImgv4.setBackground(shape);
    }

    public void addListeners() {
        cancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noteTextEditText != null && noteTextEditText.getText() != null &&
                        noteTextEditText.getText().toString().isEmpty()) {
                    CommonUtils.showCustomToast(mContext, getResources().getString(R.string.specify_the_problem_briefly), ToastEnum.TOAST_INFO);
                    return;
                }
                saveReport();
            }
        });

        addPhotoImgv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenImgv = addPhotoImgv1;
                managePhotoChosen();
            }
        });

        addPhotoImgv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenImgv = addPhotoImgv2;
                managePhotoChosen();
            }
        });

        addPhotoImgv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenImgv = addPhotoImgv3;
                managePhotoChosen();
            }
        });

        addPhotoImgv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenImgv = addPhotoImgv4;
                managePhotoChosen();
            }
        });

        imgDelete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenImgv = addPhotoImgv1;
                removePhoto();
            }
        });

        imgDelete2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenImgv = addPhotoImgv2;
                removePhoto();
            }
        });

        imgDelete3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenImgv = addPhotoImgv3;
                removePhoto();
            }
        });

        imgDelete4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenImgv = addPhotoImgv4;
                removePhoto();
            }
        });
    }

    public void initProblemList() {
        problemListBox = new ArrayList<>();

        ProblemNotifyModel p1 = new ProblemNotifyModel();
        p1.setImageView(addPhotoImgv1);
        p1.setDeleteImgv(imgDelete1);
        problemListBox.add(p1);
        setViewPadding(addPhotoImgv1, p1);

        ProblemNotifyModel p2 = new ProblemNotifyModel();
        p2.setImageView(addPhotoImgv2);
        p2.setDeleteImgv(imgDelete2);
        problemListBox.add(p2);
        setViewPadding(addPhotoImgv2, p2);

        ProblemNotifyModel p3 = new ProblemNotifyModel();
        p3.setImageView(addPhotoImgv3);
        p3.setDeleteImgv(imgDelete3);
        problemListBox.add(p3);
        setViewPadding(addPhotoImgv3, p3);

        ProblemNotifyModel p4 = new ProblemNotifyModel();
        p4.setImageView(addPhotoImgv4);
        p4.setDeleteImgv(imgDelete4);
        problemListBox.add(p4);
        setViewPadding(addPhotoImgv4, p4);
    }

    public void setViewPadding(ImageView view, ProblemNotifyModel problemNotifyModel) {
        problemNotifyModel.getImageView().setPadding(70, 70, 70, 70);
        problemNotifyModel.getImageView().setColorFilter(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.Gray, null), PorterDuff.Mode.SRC_IN);
        problemNotifyModel.getImageView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        problemNotifyModel.getDeleteImgv().setVisibility(View.GONE);
    }

    public void clearViewPadding(ProblemNotifyModel problemNotifyModel) {
        problemNotifyModel.getImageView().setPadding(0, 0, 0, 0);
        problemNotifyModel.getImageView().setColorFilter(null);
        problemNotifyModel.getImageView().setScaleType(ImageView.ScaleType.FIT_XY);
        problemNotifyModel.getDeleteImgv().setVisibility(View.VISIBLE);
    }

    public void managePhotoChosen() {
        for (final ProblemNotifyModel problemNotifyModel : problemListBox) {

            if (problemNotifyModel.getImageView() == chosenImgv) {
                if (problemNotifyModel.getPhotoSelectUtil() != null) {

                    if (mFragmentNavigation != null) {
                        mFragmentNavigation.pushFragment(new MarkProblemFragment(problemNotifyModel.getPhotoSelectUtil(), new ReturnObjectCallback() {
                                    @Override
                                    public void OnReturn(Object object) {
                                        PhotoSelectUtil util = (PhotoSelectUtil) object;
                                        problemNotifyModel.setPhotoSelectUtil(util);
                                        setPhotoSelectUtil(util);
                                    }
                                }));
                    }
                    return;
                }
            }
        }

        startPhotoChosen();
    }

    public void startPhotoChosen() {
        DialogBoxUtil.photoChosenForProblemReportDialogBox(getContext(), null, new PhotoChosenForReportCallback() {
            @Override
            public void onGallerySelected() {
                startGalleryProcess();
            }

            @Override
            public void onScreenShot() {
                screenShotStart();
            }
        });
    }

    private void screenShotStart() {
        ((Activity) mContext).onBackPressed();

        screenShotMainLayout.setVisibility(View.VISIBLE);

        screenShotApproveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = BitmapUtils.getScreenShot(mainll);
                PhotoSelectUtil photoSelectUtil = new PhotoSelectUtil();
                photoSelectUtil.setBitmap(bitmap);
                setPhotoSelectUtil(photoSelectUtil);
                returnNotifyFragment();
            }
        });

        screenShotCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnNotifyFragment();
            }
        });
    }

    public void returnNotifyFragment() {
        screenShotMainLayout.setVisibility(View.GONE);
        screenShotApproveBtn.setOnClickListener(null);
        screenShotCancelBtn.setOnClickListener(null);

        if (getActivity() != null)
            ((MainActivity) getActivity()).switchAndUpdateTabSelection(FragNavController.TAB1);
        else {
            if (mFragmentNavigation != null) {
                mFragmentNavigation.pushFragment(MainActivity.notifyProblemFragment);
            }
        }
    }

    public void removePhoto() {
        for (ProblemNotifyModel problemNotifyModel : problemListBox) {
            if (problemNotifyModel.getImageView() == chosenImgv) {
                problemNotifyModel.setPhotoSelectUtil(null);
                setViewPadding(chosenImgv, problemNotifyModel);
                Glide.with(MainActivity.mainActivity)
                        .load(R.drawable.ic_add_white)
                        .apply(RequestOptions.centerInsideTransform())
                        .into(chosenImgv);
                break;
            }
        }
    }

    private void startGalleryProcess() {
        if (permissionModule.checkWriteExternalStoragePermission())
            startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntent(),
                    getResources().getString(R.string.select_picture)), CODE_GALLERY_REQUEST);
        else
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PermissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PermissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntent(),
                        getResources().getString(R.string.select_picture)), CODE_GALLERY_REQUEST);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CODE_GALLERY_REQUEST) {
                PhotoSelectUtil photoSelectUtil = new PhotoSelectUtil(getActivity(), data, GALLERY_TEXT);
                setPhotoSelectUtil(photoSelectUtil);
            }
        }
    }

    public void setPhotoSelectUtil(PhotoSelectUtil photoSelectUtil) {
        for (ProblemNotifyModel problemNotifyModel : problemListBox) {
            if (problemNotifyModel.getImageView() == chosenImgv) {
                problemNotifyModel.setPhotoSelectUtil(photoSelectUtil);
                clearViewPadding(problemNotifyModel);

                if (photoSelectUtil.getBitmap() != null)
                    Glide.with(MainActivity.mainActivity)
                            .load(photoSelectUtil.getBitmap())
                            .apply(RequestOptions.fitCenterTransform())
                            .into(chosenImgv);
                break;
            }
        }
    }

    public void saveReport() {
        setFinalReportBox();
        DialogBoxUtil.showInfoDialogWithLimitedTime(getContext(), null,
                getResources().getString(R.string.thanks_for_feedback), 3000, new InfoDialogBoxCallback() {
                    @Override
                    public void okClick() {
                        ((Activity) mContext).onBackPressed();


                        //TODO - Async calisacak sekilde problem bildirimi yapalim

                        if(1 == 1) { // Yukleme save basarili ise
                            mFragmentNavigation.clearStackWithGivenIndex(FragNavController.TAB1);
                            mFragmentNavigation.newSaleTriggered();
                            //((MainActivity) getActivity()).clearStackGivenIndex(FragNavController.TAB1);
                            //((MainActivity) getActivity()).switchAndUpdateTabSelection(FragNavController.TAB1);


                        }

                        /*new SaveReportProblemProcess(photoSelectUtilList,
                                noteTextEditText.getText().toString(),
                                AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid(),
                                REPORT_PROBLEM_TYPE_BUG,
                                new CompleteCallback() {
                                    @Override
                                    public void onComplete(Object object) {

                                    }

                                    @Override
                                    public void onFailed(Exception e) {

                                    }
                                });*/
                    }
                });
    }

    private void setFinalReportBox() {
        for (ProblemNotifyModel problemNotifyModel : problemListBox) {
            if (problemNotifyModel != null && problemNotifyModel.getPhotoSelectUtil() != null &&
                    problemNotifyModel.getPhotoSelectUtil().getBitmap() != null) {
                photoSelectUtilList.add(problemNotifyModel.getPhotoSelectUtil());
            }
        }
    }
}