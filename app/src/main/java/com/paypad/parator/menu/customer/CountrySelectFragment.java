package com.paypad.parator.menu.customer;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.paypad.parator.R;
import com.paypad.parator.enums.CountryDataEnum;
import com.paypad.parator.httpprocess.CountryProcess;
import com.paypad.parator.httpprocess.interfaces.OnEventListener;
import com.paypad.parator.interfaces.CountrySelectListener;
import com.paypad.parator.interfaces.ReturnSizeCallback;
import com.paypad.parator.model.pojo.Country;
import com.paypad.parator.utils.CommonUtils;
import com.paypad.parator.utils.DataUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CountrySelectFragment extends BottomSheetDialogFragment {

    private RecyclerView countryRv;
    private EditText searchEdittext;
    private TextView searchResultTv;

    private CountrySelectAdapter countrySelectAdapter;
    private CountrySelectListener selectListener;
    private Context mContext;
    TextToSpeech textToSpeech = null;

    public CountrySelectFragment() {

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
        View contentView = View.inflate(mContext, R.layout.fragment_select_country, null);

        dialog.setContentView(contentView);

        View parent = ((View) contentView.getParent());

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        parent.setBackgroundColor(getResources().getColor(android.R.color.transparent, null));
        countryRv = contentView.findViewById(R.id.countryRv);
        ImageButton closeImgBtn = contentView.findViewById(R.id.closeImgBtn);

        searchEdittext = contentView.findViewById(R.id.searchEdittext);
        ImageView searchCancelImgv = contentView.findViewById(R.id.searchCancelImgv);
        searchResultTv = contentView.findViewById(R.id.searchResultTv);
        searchEdittext.setHint(mContext.getResources().getString(R.string.search_by_name_or_code));


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        countryRv.setLayoutManager(linearLayoutManager);

        List<Country> countries = DataUtils.getCountries(mContext);

        countrySelectAdapter = new CountrySelectAdapter(countries);
        countryRv.setAdapter(countrySelectAdapter);



        textToSpeech = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });
        textToSpeech.speak("Selam Ugur nasilsin", TextToSpeech.QUEUE_FLUSH, null);


        //getCountryNameList();

        closeImgBtn.setOnClickListener(new View.OnClickListener() {
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

    /*private void getCountryNameList() {
        CountryProcess countryProcess = new CountryProcess(getContext(), CountryDataEnum.NAMES, new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                if (object != null) {
                    List<String> countries = (List<String>) object;
                    Collections.sort(countries);
                    countrySelectAdapter = new CountrySelectAdapter(countries);
                    countryRv.setAdapter(countrySelectAdapter);
                }
            }

            @Override
            public void onFailure(Exception e) {
                CommonUtils.showToastShort(getContext(), "There is an error occured while getting countries!");
                dismiss();
            }

            @Override
            public void onTaskContinue() {

            }
        });
        countryProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }*/

    public void updateAdapter(String searchText) {
        if (searchText != null) {
            countrySelectAdapter.updateAdapter(searchText, new ReturnSizeCallback() {
                @Override
                public void OnReturn(int size) {
                    if (size == 0)
                        searchResultTv.setVisibility(View.VISIBLE);
                    else
                        searchResultTv.setVisibility(View.GONE);
                }
            });
        }
    }

    public void setCountryListener(CountrySelectListener listener) {
        selectListener = listener;
    }

    public class CountrySelectAdapter extends RecyclerView.Adapter<CountrySelectAdapter.CountryHolder> {

        private List<Country> countries = new ArrayList<>();
        private List<Country> orgCountries = new ArrayList<>();

        CountrySelectAdapter(List<Country> countries) {
            this.countries.addAll(countries);
            this.orgCountries.addAll(countries);
        }

        @NonNull
        @Override
        public CountryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country_list, parent, false);
            return new CountryHolder(view);
        }

        @Override
        public void onBindViewHolder(CountryHolder holder, int position) {
            holder.country = countries.get(position);
            holder.countryNameTv.setText("(".concat(holder.country.getCode()).concat(") ").concat(holder.country.getName()));
        }

        @Override
        public int getItemCount() {
            return countries.size();
        }

        class CountryHolder extends RecyclerView.ViewHolder {
            TextView countryNameTv;
            LinearLayout countryll;

            Country country;

            CountryHolder(View itemView) {
                super(itemView);
                countryNameTv = itemView.findViewById(R.id.countryNameTv);
                countryll = itemView.findViewById(R.id.structItemll);

                itemView.setOnClickListener(v -> {
                    if (selectListener != null) {
                        selectListener.onCountryClick(country);
                    }
                    dismiss();
                });
            }
        }

        public void updateAdapter(String searchText, ReturnSizeCallback returnSizeCallback) {
            if (searchText.trim().isEmpty()){
                countries = orgCountries;
            } else {

                List<Country> tempCountryList = new ArrayList<>();

                for (Country country : orgCountries) {
                    if (country != null && ((country.getName().toLowerCase().contains(searchText.toLowerCase())) || (country.getCode().toLowerCase().contains(searchText.toLowerCase()))))
                        tempCountryList.add(country);
                }
                countries = tempCountryList;
            }

            this.notifyDataSetChanged();

            if (countries != null)
                returnSizeCallback.OnReturn(countries.size());
            else
                returnSizeCallback.OnReturn(0);
        }
    }
}