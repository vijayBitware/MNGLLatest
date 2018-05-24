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
import com.mngl.model.filter.BPNumerResponse;
import com.mngl.model.home.Datum;
import com.mngl.model.home.Home;
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
import com.mngl.view.activity.ActivityLogin;
import com.mngl.view.activity.DrawerActivity;
import com.mngl.webservice.APIRequest;
import com.mngl.webservice.BaseResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.CheckedOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bitware on 27/4/18.
 */

public class FragmentHome extends BaseFragment implements APIRequest.ResponseHandler, View.OnClickListener {
    Realm realm;
    List<Datum> societyList;
    View view;
    @BindView(R.id.rv_home)
    RecyclerView rv_home;
    FontChangeCrawler fontChanger;
    TextView txtTitle;
    @BindView(R.id.txtAreaName)
    TextView txtAreaName;
    ImageView imgFilter;
    LinearLayout ll_filter, ll_wing, ll_society, ll_area, ll_street3;
    String FilterFlag = "";
    TextView txtMUNumber;
    @BindView(R.id.txtNoRecords)
    TextView txtNoRecords;
    long count;
    EditText edt_message;
    String filter = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        init();

        if (Constant.FRAGMENTSOCIETY.equalsIgnoreCase("no")) {
            if (NetworkStatus.isConnectingToInternet(mActivity)) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("revisit", "false");
                    Log.e("TAG", "Home request > " + jsonObject);
                    new APIRequest(getContext(), jsonObject, Constant.society_list, this, Constant.API_HOME, Constant.POST, "yes");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                getSocietyFromDatabase();
            }

        } else {

            deleteSocietyFromDb();
        }

        return view;
    }

    private void deleteSocietyFromDb() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmResults<Society> result = realm.where(Society.class).equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, "")).and()
                        .equalTo("isRevisit", "false").findAll();
                result.deleteAllFromRealm();

                count = realm.where(Society.class).equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, ""))
                        .and().equalTo("isRevisit", "false").count();

                Constant.FRAGMENTSOCIETY = "no";

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e("TAG", "onSuccess >> Data Inserted Successfully..!!");

                getSocietyFromDatabase();

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e("TAG", "onError >>" + error.toString());
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fontChanger = new FontChangeCrawler(mActivity.getAssets());
        fontChanger.replaceFonts((ViewGroup) mActivity.findViewById(android.R.id.content));
    }

    private void init() {
        ImageView imgFilter = mActivity.findViewById(R.id.imgFilter);
        imgFilter.setVisibility(View.VISIBLE);
        SharedPref pref = new SharedPref(mActivity);
        realm = Realm.getDefaultInstance();
        societyList = new ArrayList<Datum>();
        imgFilter = getActivity().findViewById(R.id.imgFilter);
        imgFilter.setOnClickListener(this);
        ll_filter = getActivity().findViewById(R.id.ll_filter);
        txtTitle = getActivity().findViewById(R.id.txtTitle);
        txtTitle.setText("Location");
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rv_home.setLayoutManager(manager);

        ll_street3 = getActivity().findViewById(R.id.ll_street3);
        ll_wing = getActivity().findViewById(R.id.ll_wing);
        ll_society = getActivity().findViewById(R.id.ll_society);
        ll_area = getActivity().findViewById(R.id.ll_area);
        txtMUNumber = getActivity().findViewById(R.id.txtMUNumber);

        ll_wing.setOnClickListener(this);
        ll_society.setOnClickListener(this);
        ll_area.setOnClickListener(this);
        txtMUNumber.setOnClickListener(this);

        ll_society.setVisibility(View.VISIBLE);
        ll_filter.setVisibility(View.GONE);
        ll_wing.setVisibility(View.GONE);
        ll_area.setVisibility(View.GONE);
        ll_street3.setVisibility(View.GONE);
        txtMUNumber.setVisibility(View.VISIBLE);

        Constant.isFilter = false;
    }

    @Override
    public void onSuccess(BaseResponse response) {

        if (response.getApiName() == Constant.API_HOME) {
            Home home = (Home) response;
            if (home.getSuccess()) {

                txtAreaName.setText(home.getArea());
                txtNoRecords.setVisibility(View.GONE);
                rv_home.setVisibility(View.VISIBLE);
                saveDetailsToDatabase(home.getArea(), home.getData());

            } else if (home.getCode() == 401) {
                AlertClass.sessionExpiredDialog(getContext(), home.getMsg());
            }
        } else if (response.getApiName() == Constant.API_FILTER) {
            Home home = (Home) response;
            if (home.getSuccess()) {
                rv_home.setAdapter(new AdapterHome(mActivity, home.getData(), "home"));
            } else if (home.getCode() == 401) {
                AlertClass.sessionExpiredDialog(getContext(), home.getMsg());
            } else {
                if (home.getData().size() == 0) {
                    SnackBarUtils.showSnackBarPink(MyApplication.getContext(), getActivity().findViewById(android.R.id.content), "No record's found for relevant filter");
                }
            }
        } else if (response.getApiName() == Constant.API_FILTER_BPNUMBER) {
            BPNumerResponse res = (BPNumerResponse) response;
            if (res.getSuccess()) {
                Bundle bundle = new Bundle();
                bundle.putString("name", res.getData().getCustomerName());
                bundle.putString("bp_no", res.getData().getBpNo());
                bundle.putString("mru_name", res.getData().getMruName());
                bundle.putString("mobile", res.getData().getCustomerMobile());
                bundle.putString("meter_number", res.getData().getMeterNumber());
                bundle.putString("address", res.getData().getSocietyName() + " " + res.getData().getStreet3() + "\n"
                        + "Wing " + res.getData().getWing() + " Flat No.: " + res.getData().getHouseNumber() + " " + res.getData().getStreet2()
                        + "\n" + res.getData().getLocation());
                bundle.putString("amount", res.getData().getCustomerName());
                mActivity.pushFragments(new CustomerDetailsFragment(), true, true, bundle, "");

            } else if (res.getCode() == 401) {
                AlertClass.sessionExpiredDialog(getContext(), res.getMsg());
            }

        } else {
            txtNoRecords.setVisibility(View.VISIBLE);
            rv_home.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailure(BaseResponse response) {

    }

    private void saveDetailsToDatabase(final String area, final List<Datum> list) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmResults<Society> result = realm.where(Society.class).equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, "")).and()
                        .equalTo("isRevisit", "false").findAll();
                result.deleteAllFromRealm();
                long count = realm.where(Society.class).equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, "")).and()
                        .equalTo("isRevisit", "false").count();

                for (int i = 0; i < list.size(); i++) {
                    Society data = new Society();
                    data.setIsRevisit("false");
                    data.setArea(area);
                    data.setUserId(SharedPref.getPreferences().getString(SharedPref.USER_ID, ""));
                    data.setSocietyName(list.get(i).getSocietyName());
                    data.setLocation(list.get(i).getLocation());

                    realm.copyToRealm(data);
                }

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e("TAG", "onSuccess >> Data Inserted Successfully..!!");

                getSocietyFromDatabase();

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e("TAG", "onError >>" + error.toString());
            }
        });
    }

    private void getSocietyFromDatabase() {

        long count = realm.where(Society.class).equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, "")).equalTo("isRevisit", "false").count();
        RealmResults<Society> results = realm.where(Society.class).equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, "")).and()
                .equalTo("isRevisit", "false").findAllAsync();
        results.load();

        txtAreaName.setText(results.get(0).getArea());

        if (count == 0) {
            if (NetworkStatus.isConnectingToInternet(mActivity)) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("revisit", "false");
                    Log.e("TAG", "Home request > " + jsonObject);
                    new APIRequest(getContext(), jsonObject, Constant.society_list, this, Constant.API_HOME, Constant.POST, "yes");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                rv_home.setVisibility(View.GONE);
                txtNoRecords.setVisibility(View.VISIBLE);
            }
        } else {
            for (int i = 0; i < results.size(); i++) {

                Datum data = new Datum();

                data.setLocation(results.get(i).getLocation());
                data.setSocietyName(results.get(i).getSocietyName());

                societyList.add(data);

            }
            rv_home.setAdapter(new AdapterHome(mActivity, societyList, "home"));
        }
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
            case R.id.txtWing:
                ll_filter.setVisibility(View.GONE);
                FilterFlag = "Wing";
                OnFilterClickPopUp();

                break;
            case R.id.ll_society:
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
            edt_message.setHint("Please enter BP Number");
        }

        // if button is clicked, close the custom dialog
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Constant.isFilter = false;

                if (FilterFlag.equalsIgnoreCase("Wing")) {
                    // Toast.makeText(getActivity(), "Wing wise filter will be applied", Toast.LENGTH_SHORT).show();

                } else if (FilterFlag.equalsIgnoreCase("Society")) {
                    filter = "society";
                    // Toast.makeText(getActivity(), "Society wise filter will be applied", Toast.LENGTH_SHORT).show();

                } else if (FilterFlag.equalsIgnoreCase("Area")) {
                    //Toast.makeText(getActivity(), "Area wise filter will be applied", Toast.LENGTH_SHORT).show();

                } else if (FilterFlag.equalsIgnoreCase("MUNumber")) {
                    filter = "bpnumber";
                    //Toast.makeText(getActivity(), "BP Number wise filter will be applied", Toast.LENGTH_SHORT).show();

                }
                if (filter.equalsIgnoreCase("society")) {
                    if (NetworkStatus.isConnectingToInternet(mActivity)) {
                        callFilterApi();
                    } else {

                        RealmResults<Society> results = realm.where(Society.class).equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, "")).and()
                                .equalTo("isRevisit", "false").and().contains("societyName", edt_message.getText().toString(), Case.INSENSITIVE).findAllAsync();
                        results.load();

                        societyList.clear();
                        if (results.size() != 0) {
                            txtAreaName.setText(results.get(0).getArea());
                        }

                        System.out.println("*********filter*********" + results.size());
                        for (int i = 0; i < results.size(); i++) {

                            Datum data = new Datum();

                            data.setLocation(results.get(i).getLocation());
                            data.setSocietyName(results.get(i).getSocietyName());

                            societyList.add(data);

                        }
                        rv_home.setAdapter(new AdapterHome(mActivity, societyList, "home"));

                    }

                } else {
                    if (NetworkStatus.isConnectingToInternet(mActivity)) {
                        callFilterWithBpNumberApi();
                    } else {

                        SnackBarUtils.showSnackBarPink(MyApplication.getContext(), getActivity().findViewById(android.R.id.content), getResources().getString(R.string.no_internet));
                    }
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

    private void callFilterWithBpNumberApi() {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("bp_number", edt_message.getText().toString());
            jsonObject.put("revisit", "false");

            Log.e("TAG", "Request >> " + jsonObject);
            new APIRequest(getContext(), jsonObject, Constant.filter_bpnumber, this, Constant.API_FILTER_BPNUMBER, Constant.POST, "yes");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void callFilterApi() {
        Constant.FILTER = "home";
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("society", edt_message.getText().toString());
            jsonObject.put("mru_name", "");
            jsonObject.put("meter_number", "");
            jsonObject.put("customer_name", "");
            jsonObject.put("house_number", "");
            jsonObject.put("house_type", "");
            jsonObject.put("wing", "");
            jsonObject.put("bp_number", "");
            jsonObject.put("street3", "");
            jsonObject.put("revisit", "false");
            Log.e("TAG", "Request >> " + jsonObject);
            new APIRequest(getContext(), jsonObject, Constant.filter, this, Constant.API_FILTER, Constant.POST, "yes");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
