package com.paypad.parator.menu.product;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.MainActivity;
import com.paypad.parator.R;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoViewFragment extends BaseFragment {

    View mView;

    @BindView(R.id.photoSelectImgv)
    ImageView photoSelectImgv;

    private byte[] itemPictureByteArray;
    private Context mContext;

    public PhotoViewFragment(byte[] itemPictureByteArray) {
        this.itemPictureByteArray = itemPictureByteArray;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_photo_view, container, false);
        ButterKnife.bind(this, mView);
        initVariables();
        setImage();
        return mView;
    }

    private void initVariables() {
        Matrix initMatrix = new Matrix();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(photoSelectImgv);
        photoViewAttacher.update();
    }

    private void setImage() {
        photoSelectImgv.post(new Runnable() {
                @Override
                public void run() {
                    Bitmap bmp = BitmapFactory.decodeByteArray(itemPictureByteArray, 0, itemPictureByteArray.length);

                    try{
                        photoSelectImgv.setImageBitmap(Bitmap.createScaledBitmap(bmp, photoSelectImgv.getWidth(),
                                photoSelectImgv.getHeight(), false));

                    }catch (IllegalArgumentException e){
                        int size = Math.round(TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, mContext.getResources().getDimension(R.dimen.product_imageview_default_size),
                                mContext.getResources().getDisplayMetrics()));
                        photoSelectImgv.setImageBitmap(Bitmap.createScaledBitmap(bmp, size, size, false));
                    }
                }
            });
    }
}