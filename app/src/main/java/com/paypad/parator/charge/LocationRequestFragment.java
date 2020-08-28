package com.paypad.parator.charge;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.paypad.parator.FragmentControllers.BaseFragment;
import com.paypad.parator.R;
import com.paypad.parator.db.UserDBHelper;
import com.paypad.parator.eventBusModel.UserBus;
import com.paypad.parator.interfaces.LocationGrantedCallback;
import com.paypad.parator.model.User;
import com.paypad.parator.utils.PermissionModule;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationRequestFragment extends BaseFragment {

    private View mView;

    @BindView(R.id.locationReqTitleTv)
    TextView locationReqTitleTv;
    @BindView(R.id.btnEnableLocation)
    Button btnEnableLocation;

    private User user;
    private LocationGrantedCallback locationGrantedCallback;
    private PermissionModule permissionModule;

    public LocationRequestFragment() {

    }

    public void setLocationGrantedCallback(LocationGrantedCallback locationGrantedCallback) {
        this.locationGrantedCallback = locationGrantedCallback;
    }

    @Override
    public void onResume() {
        super.onResume();
        permissionModule = new PermissionModule(getContext());

        if (permissionModule.checkAccessFineLocationPermission())
            locationGrantedCallback.OnLocationGranted(true);
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
            mView = inflater.inflate(R.layout.fragment_location_request, container, false);
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
        btnEnableLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermissionModule.PERMISSION_ACCESS_FINE_LOCATION);
            }
        });
    }

    private void initVariables() {
        locationReqTitleTv.setText(getResources().getString(R.string.app_name)
            .concat(" ")
            .concat(getResources().getString(R.string.needs_your_current_location)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionModule.PERMISSION_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationGrantedCallback.OnLocationGranted(true);
            } else {
                if(!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){

                }
                locationGrantedCallback.OnLocationGranted(false);
            }
        }
    }
}