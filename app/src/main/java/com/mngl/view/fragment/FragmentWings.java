package com.mngl.view.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mngl.R;
import com.mngl.controller.AdapterRevisitingCustomers;
import com.mngl.controller.AdapterStreet;

import com.mngl.model.societyMember.SocietyMember;
import com.mngl.model.streetlist.Datum;
import com.mngl.model.streetlist.Street;
import com.mngl.realm.SocietyMembersList;
import com.mngl.realm.Wing;
import com.mngl.utils.AlertClass;
import com.mngl.utils.Constant;
import com.mngl.utils.FontChangeCrawler;
import com.mngl.utils.MyApplication;
import com.mngl.utils.NetworkStatus;
import com.mngl.utils.SharedPref;
import com.mngl.utils.SnackBarUtils;
import com.mngl.webservice.APIRequest;
import com.mngl.webservice.BaseResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bitware on 14/5/18.
 */

public class FragmentWings extends BaseFragment implements APIRequest.ResponseHandler, View.OnClickListener {
    FontChangeCrawler fontChanger;
    List<Datum> wingList;
    Realm realm;
    View view;
    @BindView(R.id.rv_winglist)
    RecyclerView rv_winglist;
    @BindView(R.id.txtNoData)
    TextView txtNoData;
    String society_name = "", street3 = "", flag = "";
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.txtSocietyName)
    TextView txtSocietyName;
    ImageView imgFilter;
    LinearLayout ll_filter, ll_wing, ll_society, ll_area, ll_street3;
    String FilterFlag = "";
    TextView txtMUNumber;
    long count;
    EditText edt_message;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_winglist, container, false);
        ButterKnife.bind(this, view);

        init();
        society_name = getArguments().getString("society_name");
        street3 = getArguments().getString("street3");
        flag = getArguments().getString("revisit");

        txtSocietyName.setText(street3);
        if (Constant.FRAGMENTWING.equalsIgnoreCase("no")) {
            if (NetworkStatus.isConnectingToInternet(mActivity)) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("revisit", flag);
                    jsonObject.put("society", society_name);
                    jsonObject.put("street3", street3);
                    Log.e("TAG", "wing request > " + jsonObject);
                    new APIRequest(mActivity, jsonObject, Constant.wings, this, Constant.API_WING, Constant.POST, "yes");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                getWingFromDatabase();
            }

        } else {

            deleteWingFromDb();
        }

        return view;
    }

    private void deleteWingFromDb() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmResults<Wing> result = realm.where(Wing.class).equalTo("societyName", society_name).and().equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, ""))
                        .and().equalTo("street3", street3).and().equalTo("wing", Constant.FRAGMENTWING).and().equalTo("isRevisit", flag).findAll();
                result.deleteAllFromRealm();

                count = realm.where(Wing.class).equalTo("societyName", society_name).and().equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, ""))
                        .and().equalTo("street3", street3).and().equalTo("isRevisit", flag).count();

                Constant.FRAGMENTWING = "no";

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e("TAG", "onSuccess >> Data Inserted Successfully..!!");

                getWingFromDatabase();

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e("TAG", "onError >>" + error.toString());
            }
        });
    }

    private void getWingFromDatabase() {
        long count = realm.where(Wing.class).equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, "")).and().equalTo("societyName", society_name).
                and().equalTo("street3", street3).and().equalTo("isRevisit", flag).count();
        RealmResults<Wing> results = realm.where(Wing.class).equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, "")).and().equalTo("societyName", society_name).
                and().equalTo("street3", street3).and().equalTo("isRevisit", flag).findAll();
        results.load();
        Log.e("TAG", "Current Database size >> " + count + "**" + results.size());

        if (count == 0) {
            if (NetworkStatus.isConnectingToInternet(mActivity)) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("revisit", flag);
                    jsonObject.put("society", society_name);
                    jsonObject.put("street3", street3);
                    Log.e("TAG", "wing request > " + jsonObject);
                    new APIRequest(mActivity, jsonObject, Constant.wings, this, Constant.API_WING, Constant.POST, "yes");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                rv_winglist.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
            }
        } else {

            for (int i = 0; i < results.size(); i++) {

                Datum data = new Datum();

                data.setStreet(results.get(i).getStreet());
                data.setStreet3(results.get(i).getStreet3());
                data.setWing(results.get(i).getWing());

                wingList.add(data);

            }

            rv_winglist.setAdapter(new AdapterStreet(mActivity, wingList, "wings", flag));
        }

    }

    private void saveDetailsToDatabase(final List<Datum> list) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmResults<Wing> result = realm.where(Wing.class).equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, "")).and().equalTo("societyName", society_name).
                        and().equalTo("street3", street3).and().equalTo("isRevisit", flag).findAll();

                result.deleteAllFromRealm();
                long count = realm.where(Wing.class).equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, "")).and().equalTo("societyName", society_name).
                        and().equalTo("street3", street3).and().equalTo("isRevisit", flag).count();

                for (int i = 0; i < list.size(); i++) {
                    Wing data = new Wing();

                    data.setIsRevisit(flag);
                    data.setSocietyName(society_name);
                    data.setStreet(list.get(i).getStreet());
                    data.setStreet3(list.get(i).getStreet3());
                    data.setWing(list.get(i).getWing());
                    data.setUserId(SharedPref.getPreferences().getString(SharedPref.USER_ID, ""));

                    realm.copyToRealm(data);
                }

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e("TAG", "onSuccess >> Data Inserted Successfully..!!");

                getWingFromDatabase();

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e("TAG", "onError >>" + error.toString());
            }
        });
    }

    private void init() {
        wingList = new ArrayList<Datum>();
        realm = Realm.getDefaultInstance();
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        rv_winglist.setLayoutManager(manager);

        ImageView imgFilter = mActivity.findViewById(R.id.imgFilter);
        imgFilter.setVisibility(View.VISIBLE);
        SharedPref pref = new SharedPref(mActivity);
        imgFilter = getActivity().findViewById(R.id.imgFilter);
        imgFilter.setOnClickListener(this);
        ll_filter = getActivity().findViewById(R.id.ll_filter);

        ll_street3 = getActivity().findViewById(R.id.ll_street3);
        ll_wing = getActivity().findViewById(R.id.ll_wing);
        ll_society = getActivity().findViewById(R.id.ll_society);
        ll_area = getActivity().findViewById(R.id.ll_area);
        txtMUNumber = getActivity().findViewById(R.id.txtMUNumber);

        ll_street3.setOnClickListener(this);
        ll_wing.setOnClickListener(this);
        ll_society.setOnClickListener(this);
        ll_area.setOnClickListener(this);
        txtMUNumber.setOnClickListener(this);

        txtMUNumber.setVisibility(View.GONE);
        ll_street3.setVisibility(View.GONE);
        ll_society.setVisibility(View.GONE);
        ll_filter.setVisibility(View.GONE);
        ll_wing.setVisibility(View.VISIBLE);
        ll_area.setVisibility(View.GONE);

        Constant.isFilter = false;
    }

    @OnClick(R.id.imgBack)
    public void onBackClick() {

        Constant.FRAGMENTWING = "no";

        long count = realm.where(Wing.class).equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, "")).and().equalTo("societyName", society_name).
                and().equalTo("street3", street3).and().equalTo("isRevisit", flag).count();

        if (count == 0) {
            Constant.FRAGMENTSTREET = street3;
        } else {
            Constant.FRAGMENTSTREET = "no";

        }

        mActivity.popFragments();
    }

    @Override
    public void onSuccess(BaseResponse response) {

        if (response.getApiName() == Constant.API_WING) {
            Street street = (Street) response;
            if (street.getSuccess()) {
                txtNoData.setVisibility(View.GONE);
                rv_winglist.setVisibility(View.VISIBLE);
                saveDetailsToDatabase(street.getData());

            } else if (street.getCode() == 401) {
                AlertClass.sessionExpiredDialog(mActivity, street.getMsg());
            }
        } else if (response.getApiName() == Constant.API_FILTER) {
            Street street = (Street) response;
            if (street.getSuccess()) {
                rv_winglist.setAdapter(new AdapterStreet(mActivity, ((Street) response).getData(), "wings", flag));
            } else if (street.getCode() == 401) {
                AlertClass.sessionExpiredDialog(getContext(), street.getMsg());
            } else {
                if (street.getData().size() == 0) {
                    SnackBarUtils.showSnackBarPink(MyApplication.getContext(), getActivity().findViewById(android.R.id.content), "No record's found for relevant filter");
                }
            }
        } else {
            txtNoData.setVisibility(View.VISIBLE);
            rv_winglist.setVisibility(View.GONE);
        }

    }

    @Override
    public void onFailure(BaseResponse response) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgFilter:
                if (!Constant.isFilter) {
                    ll_filter.setVisibility(View.VISIBLE);
                    Constant.isFilter = true;
                } else {
                    ll_filter.setVisibility(View.GONE);
                    Constant.isFilter = false;
                }
                break;
            case R.id.ll_wing:
                ll_filter.setVisibility(View.GONE);
                FilterFlag = "Wing";
                OnFilterClickPopUp();

                break;
            case R.id.txtSociety:
                ll_filter.setVisibility(View.GONE);
                FilterFlag = "Society";
                OnFilterClickPopUp();

                break;
            case R.id.txtArea:
                ll_filter.setVisibility(View.GONE);
                FilterFlag = "Area";
                OnFilterClickPopUp();
                break;
            case R.id.txtMUNumber:
                ll_filter.setVisibility(View.GONE);
                FilterFlag = "MUNumber";
                OnFilterClickPopUp();
                break;
        }

    }

    private void OnFilterClickPopUp() {
        final Dialog dialog = new Dialog(mActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        dialog.setContentView(R.layout.popup_on_filter_click);
        // set the custom dialog components - text, image and button

        final TextView btn_yes = (TextView) dialog.findViewById(R.id.btn_yes);

        edt_message = (EditText) dialog.findViewById(R.id.edt_message);
        final TextView btn_no = (TextView) dialog.findViewById(R.id.btn_no);
        fontChanger = new FontChangeCrawler(mActivity.getAssets());
        fontChanger.replaceFonts((ViewGroup) mActivity.findViewById(android.R.id.content));

        if (FilterFlag.equalsIgnoreCase("Wing")) {
            edt_message.setHint("Please enter wing name");
        } else if (FilterFlag.equalsIgnoreCase("Society")) {
            edt_message.setHint("Please enter Society name");
        } else if (FilterFlag.equalsIgnoreCase("Area")) {
            edt_message.setHint("Please enter Area name");
        } else if (FilterFlag.equalsIgnoreCase("MUNumber")) {
            edt_message.setHint("Please enter MU Number");
        }

        // if button is clicked, close the custom dialog
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Constant.isFilter = false;

                if (FilterFlag.equalsIgnoreCase("Wing")) {
                    Toast.makeText(getActivity(), "Wing wise filter will be applied", Toast.LENGTH_SHORT).show();

                } else if (FilterFlag.equalsIgnoreCase("Society")) {
                    Toast.makeText(getActivity(), "Society wise filter will be applied", Toast.LENGTH_SHORT).show();

                } else if (FilterFlag.equalsIgnoreCase("Area")) {
                    Toast.makeText(getActivity(), "Area wise filter will be applied", Toast.LENGTH_SHORT).show();

                } else if (FilterFlag.equalsIgnoreCase("MUNumber")) {
                    Toast.makeText(getActivity(), "MUNumber wise filter will be applied", Toast.LENGTH_SHORT).show();

                }
                if (NetworkStatus.isConnectingToInternet(mActivity)) {
                    callFilterApi();
                } else {

                   RealmResults<Wing> results = realm.where(Wing.class).equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, "")).and().equalTo("societyName", society_name).
                            and().equalTo("street3", street3).and().equalTo("isRevisit", flag).and().contains("wing", edt_message.getText().toString(), Case.INSENSITIVE).findAll();
                    results.load();
                    Log.e("TAG", "Current Database size >> "+ results.size());
                    wingList.clear();
                    for (int i = 0; i < results.size(); i++) {

                        Datum data = new Datum();

                        data.setStreet(results.get(i).getStreet());
                        data.setStreet3(results.get(i).getStreet3());
                        data.setWing(results.get(i).getWing());

                        wingList.add(data);

                    }

                    rv_winglist.setAdapter(new AdapterStreet(mActivity, wingList, "wings", flag));
                }

            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public boolean onBackPressed() {

        return super.onBackPressed();
    }

    private void callFilterApi() {
        Constant.FILTER = "street";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("society", society_name);
            jsonObject.put("mru_name", "");
            jsonObject.put("meter_number", "");
            jsonObject.put("customer_name", "");
            jsonObject.put("house_number", "");
            jsonObject.put("house_type", "");
            jsonObject.put("wing", edt_message.getText().toString());
            jsonObject.put("bp_number", "");
            jsonObject.put("street3", street3);
            jsonObject.put("revisit", flag);
            Log.e("TAG", "Request >> " + jsonObject);
            new APIRequest(getContext(), jsonObject, Constant.filter, this, Constant.API_FILTER, Constant.POST, "yes");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
