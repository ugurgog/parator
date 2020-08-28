package com.paypad.parator.contact;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.paypad.parator.R;
import com.paypad.parator.contact.adapters.ContactListAdapter;
import com.paypad.parator.contact.interfaces.ReturnContactListener;
import com.paypad.parator.enums.CountryDataEnum;
import com.paypad.parator.httpprocess.CountryProcess;
import com.paypad.parator.httpprocess.interfaces.OnEventListener;
import com.paypad.parator.interfaces.CompleteCallback;
import com.paypad.parator.interfaces.ReturnSizeCallback;
import com.paypad.parator.model.pojo.BaseResponse;
import com.paypad.parator.model.pojo.Contact;
import com.paypad.parator.model.pojo.CountryPhoneCode;
import com.paypad.parator.utils.ClickableImage.ClickableImageView;
import com.paypad.parator.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContactDialogFragment extends BottomSheetDialogFragment {

    private View contentView;

    private ClickableImageView backImgv;
    private AppCompatTextView toolbarTitleTv;
    private EditText searchEdittext;
    private ImageView searchCancelImgv;
    private TextView searchResultTv;
    private RecyclerView contactRv;
    private LinearLayout contactMainll;

    private ContactListAdapter contactListAdapter;
    private List<Contact> contactList;
    private ReturnContactListener returnContactListener;
    private List<CountryPhoneCode> phoneCodes;
    private List<Contact> reformedContactList;

    public ContactDialogFragment(ReturnContactListener returnContactListener) {
        this.returnContactListener = returnContactListener;
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {

        super.setupDialog(dialog, style);
        contentView = View.inflate(getContext(), R.layout.fragment_contact_list, null);

        dialog.setContentView(contentView);

        initVariables();
        initListeners();

        View parent = ((View) contentView.getParent());

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            ((BottomSheetBehavior) behavior).setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
            ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        parent.setBackgroundColor(getResources().getColor(android.R.color.transparent, null));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        contactRv.setLayoutManager(linearLayoutManager);

        //getCountryDialCodeList();
        startGetContactList();
    }

    private void initVariables() {
        contactMainll = contentView.findViewById(R.id.contactMainll);
        contactRv = contentView.findViewById(R.id.contactRv);
        searchEdittext = contentView.findViewById(R.id.searchEdittext);
        searchCancelImgv = contentView.findViewById(R.id.searchCancelImgv);
        searchResultTv = contentView.findViewById(R.id.searchResultTv);
        toolbarTitleTv = contentView.findViewById(R.id.toolbarTitleTv);
        backImgv = contentView.findViewById(R.id.backImgv);

        toolbarTitleTv.setText(getResources().getString(R.string.choose_a_contact));
        reformedContactList = new ArrayList<>();
    }

    private void initListeners() {
        backImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        searchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.toString().trim().isEmpty()) {
                    updateAdapter(s.toString());
                    searchCancelImgv.setVisibility(View.VISIBLE);
                } else {
                    updateAdapter("");
                    searchCancelImgv.setVisibility(View.GONE);
                }
            }
        });

        searchCancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEdittext.setText("");
                searchCancelImgv.setVisibility(View.GONE);
                CommonUtils.showKeyboard(getContext(),false, searchEdittext);
            }
        });
    }

    public void startGetContactList() {
        ContactHelper.getContactList(Objects.requireNonNull(getActivity()), new CompleteCallback() {
            @Override
            public void onComplete(BaseResponse baseResponse) {

                if(baseResponse.isSuccess()){
                    contactList = (List<Contact>) baseResponse.getObject();
                    getCountryDialCodeList();
                }else {
                    CommonUtils.showToastShort(getActivity(), baseResponse.getMessage());
                }
            }
        });
    }

    public void clearDuplicateNumbers(List<Contact> tempContactList) {
        for (Contact contact : tempContactList) {

            Log.i("Info", "Contact:------------------" );
            Log.i("Info", "name   :" + contact.getName());
            Log.i("Info", "phone  :" + contact.getPhoneNumber());

            boolean isExist = false;

            for (Contact tempContact : reformedContactList) {
                if (tempContact != null && tempContact.getPhoneNumber() != null) {
                    if (contact.getPhoneNumber().trim().equals(tempContact.getPhoneNumber().trim())) {
                        isExist = true;
                        break;
                    }
                }
            }

            if (!isExist)
                reformedContactList.add(contact);
        }
    }

    private void getCountryDialCodeList() {
        CountryProcess countryProcess = new CountryProcess(getContext(), CountryDataEnum.PHONE_CODES, new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                if (object != null) {
                    phoneCodes = (List<CountryPhoneCode>) object;

                    ContactHelper.reformPhoneList(phoneCodes, contactList, new CompleteCallback() {
                        @Override
                        public void onComplete(BaseResponse baseResponse2) {
                            if(baseResponse2.isSuccess()){
                                clearDuplicateNumbers((List<Contact>) baseResponse2.getObject());
                                updateAdapterWithCurrentList();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.showToastShort(getContext(), "There is an error occured while getting country codes!");
            }

            @Override
            public void onTaskContinue() {

            }
        });
        countryProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void updateAdapterWithCurrentList(){
        contactListAdapter = new ContactListAdapter(getContext(), reformedContactList, new ReturnContactListener() {
            @Override
            public void OnReturn(Contact contact) {
                returnContactListener.OnReturn(contact);
                dismiss();
            }
        });
        contactRv.setAdapter(contactListAdapter);
    }

    public void updateAdapter(String searchText) {
        if (searchText != null) {
            contactListAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                @Override
                public void OnReturn(int size) {
                    if (size == 0 && (reformedContactList != null && reformedContactList.size() > 0))
                        searchResultTv.setVisibility(View.VISIBLE);
                    else
                        searchResultTv.setVisibility(View.GONE);
                }
            });
        }
    }
}