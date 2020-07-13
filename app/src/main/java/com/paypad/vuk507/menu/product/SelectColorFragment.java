package com.paypad.vuk507.menu.product;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.paypad.vuk507.FragmentControllers.BaseFragment;
import com.paypad.vuk507.R;
import com.paypad.vuk507.db.CategoryDBHelper;
import com.paypad.vuk507.db.ProductDBHelper;
import com.paypad.vuk507.db.UserDBHelper;
import com.paypad.vuk507.enums.ItemProcessEnum;
import com.paypad.vuk507.eventBusModel.UserBus;
import com.paypad.vuk507.interfaces.CompleteCallback;
import com.paypad.vuk507.interfaces.CustomDialogListener;
import com.paypad.vuk507.menu.category.CategoryEditFragment;
import com.paypad.vuk507.menu.category.interfaces.ReturnCategoryCallback;
import com.paypad.vuk507.menu.product.adapters.ColorSelectAdapter;
import com.paypad.vuk507.menu.product.adapters.ProductListAdapter;
import com.paypad.vuk507.menu.product.interfaces.ColorImageReturnCallback;
import com.paypad.vuk507.menu.product.interfaces.ReturnItemCallback;
import com.paypad.vuk507.model.Category;
import com.paypad.vuk507.model.Product;
import com.paypad.vuk507.model.User;
import com.paypad.vuk507.model.pojo.BaseResponse;
import com.paypad.vuk507.model.pojo.PhotoSelectUtil;
import com.paypad.vuk507.utils.BitmapUtils;
import com.paypad.vuk507.utils.ClickableImage.ClickableImageView;
import com.paypad.vuk507.utils.CommonUtils;
import com.paypad.vuk507.utils.CustomDialogBox;
import com.paypad.vuk507.utils.DataUtils;
import com.paypad.vuk507.utils.IntentSelectUtil;
import com.paypad.vuk507.utils.PermissionModule;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.paypad.vuk507.constants.CustomConstants.CAMERA_TEXT;
import static com.paypad.vuk507.constants.CustomConstants.FROM_FILE_TEXT;
import static com.paypad.vuk507.constants.CustomConstants.GALLERY_TEXT;


public class SelectColorFragment extends BaseFragment
    implements ColorImageReturnCallback {

    private View mView;

    @BindView(R.id.backImgv)
    ClickableImageView backImgv;
    @BindView(R.id.toolbarTitleTv)
    AppCompatTextView toolbarTitleTv;

    @BindView(R.id.imageRl)
    RelativeLayout imageRl;
    @BindView(R.id.itemShortNameTv)
    TextView itemShortNameTv;
    @BindView(R.id.editItemImgv)
    ImageView editItemImgv;
    @BindView(R.id.colorRv)
    RecyclerView colorRv;
    @BindView(R.id.photoLabelll)
    LinearLayout photoLabelll;
    @BindView(R.id.choosePhotoBtn)
    Button choosePhotoBtn;
    @BindView(R.id.takePhotoBtn)
    Button takePhotoBtn;

    private User user;

    private String mClassTag;
    private String itemName;
    private int mColorId;
    private ColorImageReturnCallback colorReturnCallback;
    private String galleryOrCameraSelect = "";
    private PermissionModule permissionModule;
    private PhotoSelectUtil photoSelectUtil;
    private Uri photoUri;
    private byte[] itemPictureByteArray = null;
    private boolean photoExist = false;


    private static final int ACTIVITY_REQUEST_CODE_OPEN_GALLERY = 385;
    private static final int ACTIVITY_REQUEST_CODE_OPEN_CAMERA = 85;


    public SelectColorFragment(String classTag, String itemName, int colorId, byte[] itemPictureByteArray) {
        this.mClassTag = classTag;
        this.itemName = itemName;
        this.mColorId = colorId;
        this.itemPictureByteArray = itemPictureByteArray;
    }

    public void setColorReturnCallback(ColorImageReturnCallback colorReturnCallback) {
        this.colorReturnCallback = colorReturnCallback;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void accountHolderUserReceived(UserBus userBus){
        user = userBus.getUser();
        if(user == null)
            user = UserDBHelper.getUserFromCache(getContext());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_select_color, container, false);
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

        choosePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryOrCameraSelect = GALLERY_TEXT;
                checkGalleryPermission();
            }
        });

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryOrCameraSelect = CAMERA_TEXT;
                checkCameraProcess();
            }
        });
    }

    private void initVariables() {
        setToolbarTitleTv();
        permissionModule = new PermissionModule(getContext());
        setAdapter();

        if(mClassTag.equals(CategoryEditFragment.class.getName()))
            photoLabelll.setVisibility(View.GONE);

        setInitialPhotoImage();
    }

    private void setInitialPhotoImage() {

        if(itemPictureByteArray != null){
            photoExist = true;
            editItemImgv.post(new Runnable() {
                @Override
                public void run() {
                    Bitmap bmp = BitmapFactory.decodeByteArray(itemPictureByteArray, 0, itemPictureByteArray.length);

                    try{
                        editItemImgv.setImageBitmap(Bitmap.createScaledBitmap(bmp, editItemImgv.getWidth(),
                                editItemImgv.getHeight(), false));

                    }catch (IllegalArgumentException e){
                        int size = Math.round(TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, Objects.requireNonNull(getContext()).getResources().getDimension(R.dimen.product_imageview_default_size),
                                getContext().getResources().getDisplayMetrics()));
                        editItemImgv.setImageBitmap(Bitmap.createScaledBitmap(bmp, size, size, false));
                    }
                }
            });
        }else {
            setColor();
        }
    }

    private void setToolbarTitleTv(){
        if(mClassTag.equals(CategoryEditFragment.class.getName()))
            toolbarTitleTv.setText(getResources().getString(R.string.edit_category_tile));
        else if(mClassTag.equals(ProductEditFragment.class.getName()))
            toolbarTitleTv.setText(getResources().getString(R.string.edit_item_tile));
    }

    private void setAdapter(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        colorRv.setLayoutManager(gridLayoutManager);

        int[] colorList = CommonUtils.getItemColors();

        if(itemPictureByteArray != null)
            mColorId = 0;

        ColorSelectAdapter colorSelectAdapter = new ColorSelectAdapter(getContext(), colorList, mColorId);
        colorSelectAdapter.setColorReturnCallback(this);
        colorRv.setAdapter(colorSelectAdapter);
    }

    @Override
    public void OnColorReturn(int colorId) {
        mColorId = colorId;
        setColor();
        colorReturnCallback.OnImageReturn(null, null);
    }

    @Override
    public void OnImageReturn(byte[] itemPictureByteArray, PhotoSelectUtil photoSelectUtil) {

    }

    private void setColor(){
        imageRl.setBackgroundColor(getResources().getColor(mColorId, null));
        colorReturnCallback.OnColorReturn(mColorId);
        editItemImgv.setImageDrawable(null);
        itemShortNameTv.setText(DataUtils.getProductNameShortenName(itemName));
    }

    private void checkGalleryPermission() {
        if (!permissionModule.checkWriteExternalStoragePermission())
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE);
        else
            startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntent(),
                    getResources().getString(R.string.select_picture)), ACTIVITY_REQUEST_CODE_OPEN_GALLERY);
    }

    private void checkCameraProcess() {
        if (!CommonUtils.checkCameraHardware(getContext())) {
            CommonUtils.showToastShort(getContext(), getContext().getResources().getString(R.string.device_has_no_camera));
            return;
        }

        if (permissionModule.checkCameraPermission() && permissionModule.checkWriteExternalStoragePermission())
            launchCamera();
        else if (permissionModule.checkCameraPermission())
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PermissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE);
        else if (permissionModule.checkWriteExternalStoragePermission())
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    PermissionModule.PERMISSION_CAMERA);
        else
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    PermissionModule.PERMISSION_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PermissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (galleryOrCameraSelect.equals(CAMERA_TEXT)) {
                    if (permissionModule.checkCameraPermission())
                        launchCamera();
                    else
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                PermissionModule.PERMISSION_CAMERA);
                } else if (galleryOrCameraSelect.equals(GALLERY_TEXT)) {
                    startActivityForResult(Intent.createChooser(IntentSelectUtil.getGalleryIntent(),
                            Objects.requireNonNull(getContext()).getResources().getString(R.string.select_picture)), ACTIVITY_REQUEST_CODE_OPEN_GALLERY);
                }
            }
        } else if (requestCode == PermissionModule.PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (permissionModule.checkWriteExternalStoragePermission())
                    launchCamera();
                else
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PermissionModule.PERMISSION_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ACTIVITY_REQUEST_CODE_OPEN_GALLERY) {
                photoSelectUtil = new PhotoSelectUtil(getContext(), data, GALLERY_TEXT);
                setPhoto();
            } else if (requestCode == ACTIVITY_REQUEST_CODE_OPEN_CAMERA) {
                photoSelectUtil = new PhotoSelectUtil(getContext(), photoUri, FROM_FILE_TEXT);
                setPhoto();
            }
        }
    }

    private void setPhoto() {

        if (photoSelectUtil != null && photoSelectUtil.getBitmap() != null) {

            photoExist = true;
            Glide.with(Objects.requireNonNull(getActivity()))
                    .load(photoSelectUtil.getBitmap())
                    .apply(RequestOptions.centerCropTransform())
                    .into(editItemImgv);
            itemPictureByteArray = BitmapUtils.getByteArrayFromBitmap(photoSelectUtil.getBitmap());
            itemShortNameTv.setText("");

            colorReturnCallback.OnImageReturn(itemPictureByteArray, photoSelectUtil);
            setAdapter();
        } else {

            photoExist = false;
            editItemImgv.setImageDrawable(null);
            itemPictureByteArray = null;

            imageRl.setBackgroundColor(getResources().getColor(mColorId, null));
            itemShortNameTv.setText(itemName != null ? itemName : "");

            colorReturnCallback.OnImageReturn(null, null);
        }
    }

    private void launchCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(Objects.requireNonNull(getContext()).getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = BitmapUtils.createTempImageFile(getContext());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {

                String authority = Objects.requireNonNull(getContext()).getPackageName() + ".provider";

                photoUri = FileProvider.getUriForFile(getContext(), authority, photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, ACTIVITY_REQUEST_CODE_OPEN_CAMERA);
            }
        }
    }
}