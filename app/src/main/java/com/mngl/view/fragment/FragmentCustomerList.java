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
import com.mngl.controller.AdapterHome;
import com.mngl.controller.AdapterRevisitingCustomers;

import com.mngl.controller.AdapterStreet;
import com.mngl.model.societyMember.Datum;
import com.mngl.model.societyMember.SocietyMember;
import com.mngl.realm.Society;
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
 * Created by Admin on 4/28/2018.
 */

/**
 * Created by bitware on 27/4/18.
 */

public class FragmentCustomerList extends BaseFragment implements APIRequest.ResponseHandler, View.OnClickListener {

    Realm realm;
    List<Datum> memberList;
    View view;
    @BindView(R.id.rv_revisting_customers)
    RecyclerView rv_revisting_customers;
    FontChangeCrawler fontChanger;
    AdapterRevisitingCustomers adapterRevisitingCustomers;
    LinearLayoutManager lm;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    String society_name = "", wing = "", mru_number = "", street3 = "";
    ImageView imgFilter;
    LinearLayout ll_filter, ll_wing, ll_society, ll_area, ll_street3;
    String FilterFlag = "", flag = "", rowFlag = "";
    TextView txtMUNumber;
    @BindView(R.id.txtSocietyName)
    TextView txtSocietyName;
    @BindView(R.id.txtNoRecords)
    TextView txtNoRecords;
    long count;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_revisiting_customers, container, false);
        ButterKnife.bind(this, view);
        init();
        society_name = getArguments().getString("society_name");
        street3 = getArguments().getString("street3");
        wing = getArguments().getString("wing");
        txtSocietyName.setText("Wing " + wing);
        if (flag.equalsIgnoreCase("true"))
            rowFlag = "revisit";
        else if (flag.equalsIgnoreCase("false"))
            rowFlag = "home";

        if (Constant.FRAGMENTCUSTOMER.equalsIgnoreCase("no")) {
            if (NetworkStatus.isConnectingToInternet(mActivity)) {
                callSocietyApi();
            } else {

                getMemberFromDatabase();
            }
        } else {

            deleteMemberFromDb();
        }
        return view;
    }

    private void deleteMemberFromDb() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmResults<SocietyMembersList> result = realm.where(SocietyMembersList.class).equalTo("bpNumber", Constant.BP_NUMBER).and().equalTo("isRevisit", flag).findAll();
                result.deleteAllFromRealm();
                count = realm.where(SocietyMembersList.class).equalTo("societyName", society_name).and().equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, ""))
                        .and().equalTo("street3", street3).and().equalTo("wing", wing).and().equalTo("isRevisit", flag).count();// realm.where(SocietyMembersList.class).count();

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e("TAG", "onSuccess >> Data Inserted Successfully..!!");

                getMemberFromDatabase();

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e("TAG", "onError >>" + error.toString());
            }
        });
    }

    private void saveDetailsToDatabase(final List<Datum> list) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmResults<SocietyMembersList> result = realm.where(SocietyMembersList.class).equalTo("societyName", society_name).and().equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, ""))
                        .and().equalTo("street3", street3).and().equalTo("wing", wing).and().and().equalTo("isRevisit", flag).findAll();
                result.deleteAllFromRealm();

                long count = realm.where(SocietyMembersList.class).equalTo("societyName", society_name).and().equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, ""))
                        .and().equalTo("street3", street3).and().equalTo("wing", wing).and().equalTo("isRevisit", flag).count();

                for (int i = 0; i < list.size(); i++) {

                    SocietyMembersList data = new SocietyMembersList();
                    data.setIsRevisit(flag);
                    data.setUserId(SharedPref.getPreferences().getString(SharedPref.USER_ID, ""));
                    data.setMruNumber(list.get(i).getMruName());
                    data.setBpNumber(list.get(i).getBpNo());
                    data.setMeterNumber(list.get(i).getMeterNumber());
                    data.setCustomerName(list.get(i).getCustomerName());
                    data.setCustomerMobile(list.get(i).getCustomerMobile());
                    data.setHouseType(list.get(i).getHouseType());
                    data.setWing(list.get(i).getWing());
                    data.setHouseNumber(list.get(i).getHouseNumber());
                    data.setFloor(list.get(i).getFloor());
                    data.setStreet2(list.get(i).getStreet2());
                    data.setStreet3(list.get(i).getStreet3());
                    data.setSocietyName(list.get(i).getSocietyName());
                    data.setLocation(list.get(i).getLocation());
                    data.setTotal_amount(list.get(i).getTotal_amount());
                    data.setArea(list.get(i).getArea());

                    realm.copyToRealm(data);
                }

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e("TAG", "onSuccess >> Data Inserted Successfully..!!");

                getMemberFromDatabase();

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e("TAG", "onError >>" + error.toString());
            }
        });
    }

    private void getMemberFromDatabase() {

        long count = realm.where(SocietyMembersList.class).equalTo("societyName", society_name).and().equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, ""))
                .and().equalTo("street3", street3).and().equalTo("wing", wing).and().equalTo("isRevisit", flag).count();
        RealmResults<SocietyMembersList> results = realm.where(SocietyMembersList.class).equalTo("societyName", society_name).and().equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, ""))
                .and().equalTo("street3", street3).and().equalTo("wing", wing).and().equalTo("isRevisit", flag).findAll();
        results.load();
        Log.e("TAG", "Current Database size >> " + count + "**" + results.size());
        if (count == 0) {
            if (NetworkStatus.isConnectingToInternet(mActivity)) {
                callSocietyApi();
            } else {
                rv_revisting_customers.setVisibility(View.GONE);
                txtNoRecords.setVisibility(View.VISIBLE);
            }
        } else {

            for (int i = 0; i < results.size(); i++) {

                Datum data = new Datum();

                data.setMruName(results.get(i).getMruNumber());
                data.setBpNo(results.get(i).getBpNumber());
                data.setMeterNumber(results.get(i).getMeterNumber());
                data.setCustomerName(results.get(i).getCustomerName());
                data.setCustomerMobile(results.get(i).getCustomerMobile());
                data.setHouseType(results.get(i).getHouseType());
                data.setWing(results.get(i).getWing());
                data.setHouseNumber(results.get(i).getHouseNumber());
                data.setFloor(results.get(i).getFloor());
                data.setStreet2(results.get(i).getStreet2());
                data.setStreet3(results.get(i).getStreet3());
                data.setSocietyName(results.get(i).getSocietyName());
                data.setLocation(results.get(i).getLocation());
                data.setTotal_amount(results.get(i).getTotal_amount());
                data.setArea(results.get(i).getArea());

                memberList.add(data);

            }
            adapterRevisitingCustomers = new AdapterRevisitingCustomers(mActivity, memberList, rowFlag);
            rv_revisting_customers.setAdapter(adapterRevisitingCustomers);
        }

    }

    private void callSocietyApi() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("society", society_name);
            jsonObject.put("revisit", flag);
            jsonObject.put("street3", street3);
            jsonObject.put("wing", wing);
            Log.e("TAG", "wing list request >" + jsonObject);
            new APIRequest(mActivity, jsonObject, Constant.society_member, this, Constant.API_SOCIETY_MEMBER, Constant.POST, "yes");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.imgBack)
    public void onBackClick() {
        Constant.FRAGMENTCUSTOMER = "no";

        long count = realm.where(SocietyMembersList.class).equalTo("societyName", society_name).and().equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, ""))
                .and().equalTo("street3", street3).and().equalTo("wing", wing).and().equalTo("isRevisit", flag).count();

        if (count == 0) {
            Constant.FRAGMENTWING = wing;
        } else {

            Constant.FRAGMENTWING = "no";

        }

        mActivity.popFragments();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fontChanger = new FontChangeCrawler(mActivity.getAssets());
        fontChanger.replaceFonts((ViewGroup) mActivity.findViewById(android.R.id.content));
    }

    private void init() {

        flag = getArguments().getString("revisit");
        ImageView imgFilter = mActivity.findViewById(R.id.imgFilter);
        imgFilter.setVisibility(View.VISIBLE);
        SharedPref pref = new SharedPref(mActivity);
        realm = Realm.getDefaultInstance();
        memberList = new ArrayList<Datum>();
        lm = new LinearLayoutManager(mActivity);
        rv_revisting_customers.setLayoutManager(lm);

        imgFilter = getActivity().findViewById(R.id.imgFilter);
        imgFilter.setOnClickListener(this);

        ll_street3 = getActivity().findViewById(R.id.ll_street3);
        ll_filter = getActivity().findViewById(R.id.ll_filter);
        ll_wing = getActivity().findViewById(R.id.ll_wing);
        ll_society = getActivity().findViewById(R.id.ll_society);
        ll_area = getActivity().findViewById(R.id.ll_area);
        txtMUNumber = getActivity().findViewById(R.id.txtMUNumber);

        ll_street3.setOnClickListener(this);
        ll_wing.setOnClickListener(this);
        ll_society.setOnClickListener(this);
        ll_area.setOnClickListener(this);
        txtMUNumber.setOnClickListener(this);

        txtMUNumber.setVisibility(View.VISIBLE);
        ll_street3.setVisibility(View.GONE);
        ll_society.setVisibility(View.GONE);
        ll_wing.setVisibility(View.GONE);
        ll_area.setVisibility(View.GONE);
        ll_filter.setVisibility(View.GONE);

        Constant.isFilter = false;
    }

    @Override
    public void onSuccess(BaseResponse response) {
        if (response.getApiName() == Constant.API_SOCIETY_MEMBER) {
            SocietyMember societyMember = (SocietyMember) response;
            if (societyMember.getSuccess()) {
                rv_revisting_customers.setVisibility(View.VISIBLE);
                txtNoRecords.setVisibility(View.GONE);

                if (flag.equalsIgnoreCase("true"))
                    rowFlag = "revisit";
                else if (flag.equalsIgnoreCase("false"))
                    rowFlag = "home";
                saveDetailsToDatabase(societyMember.getData());
            } else if (societyMember.getCode() == 401) {
                AlertClass.sessionExpiredDialog(getContext(), societyMember.getMsg());
            } else if (societyMember.getSuccess() == false) {
                rv_revisting_customers.setVisibility(View.GONE);
                txtNoRecords.setVisibility(View.VISIBLE);
            }
        } else if (response.getApiName() == Constant.API_FILTER) {
            SocietyMember societyMember = (SocietyMember) response;
            if (societyMember.getSuccess()) {
                adapterRevisitingCustomers = new AdapterRevisitingCustomers(mActivity, societyMember.getData(), "wing_list");
                rv_revisting_customers.setAdapter(adapterRevisitingCustomers);
            } else if (societyMember.getCode() == 401) {
                AlertClass.sessionExpiredDialog(getContext(), societyMember.getMsg());
            } else {
                if (societyMember.getData().size() == 0) {
                    SnackBarUtils.showSnackBarPink(MyApplication.getContext(), getActivity().findViewById(android.R.id.content), "No record's found for relevant filter");
                }
            }
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
            case R.id.ll_society:
                ll_filter.setVisibility(View.GONE);
                FilterFlag = "Society";
                OnFilterClickPopUp();

                break;
            case R.id.ll_area:
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
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        dialog.setContentView(R.layout.popup_on_filter_click);
        // set the custom dialog components - text, image and button

        final TextView btn_yes = (TextView) dialog.findViewById(R.id.btn_yes);

        final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
        final TextView btn_no = (TextView) dialog.findViewById(R.id.btn_no);
        fontChanger = new FontChangeCrawler(getActivity().getAssets());
        fontChanger.replaceFonts((ViewGroup) getActivity().findViewById(android.R.id.content));

        if (FilterFlag.equalsIgnoreCase("Wing")) {
            edt_message.setHint("Please enter wing name");
        } else if (FilterFlag.equalsIgnoreCase("Society")) {
            edt_message.setHint("Please enter Society name");
        } else if (FilterFlag.equalsIgnoreCase("Area")) {
            edt_message.setHint("Please enter Area name");
        } else if (FilterFlag.equalsIgnoreCase("MUNumber")) {
            edt_message.setHint("Please enter BP Number");
        }

        // if button is clicked, close the custom dialog
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Constant.isFilter = false;
                if (FilterFlag.equalsIgnoreCase("Wing")) {
                    wing = edt_message.getText().toString();
                } else if (FilterFlag.equalsIgnoreCase("Society")) {
                    Toast.makeText(getActivity(), "Society wise filter will be applied", Toast.LENGTH_SHORT).show();

                } else if (FilterFlag.equalsIgnoreCase("Area")) {
                    Toast.makeText(getActivity(), "Area wise filter will be applied", Toast.LENGTH_SHORT).show();

                } else if (FilterFlag.equalsIgnoreCase("MUNumber")) {
                    mru_number = edt_message.getText().toString();

                }
                if (NetworkStatus.isConnectingToInternet(mActivity)) {
                    callFilterApi();
                } else {

                    RealmResults<SocietyMembersList> results = realm.where(SocietyMembersList.class).equalTo("societyName", society_name).and().equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, ""))
                            .and().equalTo("street3", street3).and().equalTo("wing", wing).and().equalTo("isRevisit", flag).contains("bpNumber", edt_message.getText().toString(), Case.INSENSITIVE).findAll();
                    results.load();
                    Log.e("TAG", "Current Database size >> " + results.size());
                    memberList.clear();
                    for (int i = 0; i < results.size(); i++) {

                        Datum data = new Datum();

                        data.setMruName(results.get(i).getMruNumber());
                        data.setBpNo(results.get(i).getBpNumber());
                        data.setMeterNumber(results.get(i).getMeterNumber());
                        data.setCustomerName(results.get(i).getCustomerName());
                        data.setCustomerMobile(results.get(i).getCustomerMobile());
                        data.setHouseType(results.get(i).getHouseType());
                        data.setWing(results.get(i).getWing());
                        data.setHouseNumber(results.get(i).getHouseNumber());
                        data.setFloor(results.get(i).getFloor());
                        data.setStreet2(results.get(i).getStreet2());
                        data.setStreet3(results.get(i).getStreet3());
                        data.setSocietyName(results.get(i).getSocietyName());
                        data.setLocation(results.get(i).getLocation());
                        data.setTotal_amount(results.get(i).getTotal_amount());

                        memberList.add(data);

                    }
                    adapterRevisitingCustomers = new AdapterRevisitingCustomers(mActivity, memberList, rowFlag);
                    rv_revisting_customers.setAdapter(adapterRevisitingCustomers);
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

    private void callFilterApi() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("society", society_name);
            jsonObject.put("mru_name", "");
            jsonObject.put("meter_number", "");
            jsonObject.put("customer_name", "");
            jsonObject.put("house_number", "");
            jsonObject.put("house_type", "");
            jsonObject.put("wing", wing);
            jsonObject.put("bp_number", mru_number);
            jsonObject.put("street3", "");
            jsonObject.put("revisit", flag);
            Log.e("TAG", "Request >> " + jsonObject);
            new APIRequest(getContext(), jsonObject, Constant.filter, this, Constant.API_FILTER, Constant.POST, "yes");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onBackPressed() {

        return super.onBackPressed();
    }
}
