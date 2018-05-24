package com.mngl.view.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mngl.R;
import com.mngl.controller.AdapterHistory;
import com.mngl.controller.AdapterOfline;
import com.mngl.controller.AdapterRevisitingCustomers;
import com.mngl.model.societyMember.Datum;
import com.mngl.realm.Customer;
import com.mngl.utils.Constant;
import com.mngl.utils.MyApplication;
import com.mngl.utils.NetworkStatus;
import com.mngl.utils.SharedPref;
import com.mngl.utils.SnackBarUtils;
import com.mngl.webservice.APIRequest;
import com.mngl.webservice.BaseResponse;
import com.mngl.webservice.WebServiceImage;
import com.mngl.webservice.WebServiceImageNew;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class UploadDataFragment extends BaseFragment {
    JSONObject obj = null;
    JSONObject main_obj = null;
    Realm realm;

    View view;
    @BindView(R.id.rv_history)
    RecyclerView rv_history;
    AdapterOfline adapterHistory;
    TextView txtTitle;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.txtNoData)
    TextView txtNoData;
    @BindView(R.id.btnUpload)
    Button btnUpload;
    ArrayList<String> bpnumberList;
    int count;
    List<Customer> memberList;
    int startRange = 0, endRange = 10, uploadedDataCount = 0;
    boolean first = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_upload_data, container, false);
        ButterKnife.bind(this, view);
        init();

        return view;
    }

    private void init() {
        txtTitle = getActivity().findViewById(R.id.txtTitle);
        txtTitle.setText("Offline Records");
        bpnumberList = new ArrayList<>();
        memberList = new ArrayList<>();
        realm = Realm.getDefaultInstance();
        ImageView imgFilter = mActivity.findViewById(R.id.imgFilter);
        imgFilter.setVisibility(View.VISIBLE);
        txtTitle = getActivity().findViewById(R.id.txtTitle);
        txtTitle.setText("History");
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rv_history.setLayoutManager(manager);

        setData();
    }

    private void setData() {
        long count = realm.where(Customer.class).count();
        RealmResults<Customer> results = realm.where(Customer.class).equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, "")).findAllAsync();

        results.load();
        Log.e("TAG", "Current Database size >> " + count + "**" + results.size());
        if (count == 0) {
            txtNoData.setVisibility(View.VISIBLE);
            rv_history.setVisibility(View.GONE);
        } else {
            txtNoData.setVisibility(View.GONE);
            rv_history.setVisibility(View.VISIBLE);

            for (int i = 0; i < results.size(); i++) {

                Customer data = new Customer();

                data.setAddres("");
                data.setCustomerName(results.get(i).getCustomerName());
                data.setCustomerMobile(results.get(i).getCustomerMobile());

                memberList.add(data);

            }
            adapterHistory = new AdapterOfline(mActivity, memberList);
            rv_history.setAdapter(adapterHistory);
        }

    }

    @OnClick(R.id.imgBack)
    public void onBackClick() {
        mActivity.popFragments();
    }

    @OnClick(R.id.btnUpload)
    public void onUploadClick() {
        if (NetworkStatus.isConnectingToInternet(mActivity)) {
            uploadAsynchData();
        } else {

            SnackBarUtils.showSnackBarPink(MyApplication.getContext(), getActivity().findViewById(android.R.id.content), getResources().getString(R.string.no_internet));
        }
    }

    private void uploadAsynchData() {
        // TODO Auto-generated method stub
        Log.v("IN BROADCAST RECEIVER", "uploadAsynchData");

        SharedPref pref = new SharedPref(mActivity);
        obj = new JSONObject();
        main_obj = new JSONObject();
        JSONArray req = new JSONArray();

        try {

            RealmResults<Customer> results = realm.where(Customer.class).equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, "")).findAllAsync();
            results.load();
            count = results.size();

            if (results.size() == 0) {
                SnackBarUtils.showSnackBarPink(MyApplication.getContext(), mActivity.findViewById(android.R.id.content), "No records to save");

            } else {

                if (first) {
                    if (count > 10) {
                        startRange = 0;
                        endRange = 10;

                    } else {
                        startRange = 0;
                        endRange = count;
                    }
                }

                for (int i = startRange; i < endRange; i++) {

                    String bp_number = results.get(i).getBp_number();
                    String meter_reading = results.get(i).getMeter_reading();
                    String note = results.get(i).getNote();
                    String comment = results.get(i).getComment();

                    String meterimg = Base64.encodeToString(results.get(i).getImage(), Base64.DEFAULT);

                    JSONObject reqObj = new JSONObject();
                    reqObj.put("is_available", results.get(i).getIsAvailable());
                    reqObj.put("bp_number", bp_number);
                    reqObj.put("meter_reading", meter_reading);
                    reqObj.put("note", note);
                    reqObj.put("comment", comment);
                    reqObj.put("meter_image", meterimg);

                    req.put(reqObj);
                }

                SharedPref sharedPref = new SharedPref(mActivity);
                SharedPreferences preferences = SharedPref.getPreferences();

                main_obj.put("result", req);

                System.out.println("Json Obj>>>>>" + main_obj.toString());
                submitDetails();
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void longLog(String str) {
        if (str.length() > 4000) {
            Log.d("str", str.substring(0, 4000));
            longLog(str.substring(4000));
        } else
            Log.d("str", str);
    }

    private void submitDetails() {
        try {

            WebServiceImageNew service = new WebServiceImageNew(callback);

            MultipartEntity reqEntity = new MultipartEntity();

            reqEntity.addPart("result", new StringBody(main_obj.toString()));

            service.getService(mActivity, Constant.submitCustomerDetails, reqEntity);
        } catch (NullPointerException e) {
            System.out.println("Nullpointer Exception at Login Screen" + e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    WebServiceImageNew.CallbackImage callback = new WebServiceImageNew.CallbackImage() {
        @Override
        public void onSuccessImage(int reqestcode, JSONObject rootjson) {

            JSONArray jsonarray;

            try {
                jsonarray = rootjson.getJSONArray("updated_orders");
                for (int i = 0; i < jsonarray.length(); i++) {
                    bpnumberList.add(jsonarray.get(i).toString());
                }

                uploadedDataCount = bpnumberList.size();

                startRange = endRange;

                int cnt = count - uploadedDataCount;
                if (cnt > 10) {
                    endRange = endRange + 10;

                } else {
                    endRange = count;
                }

                if (count != uploadedDataCount) {
                    first = false;
                    uploadAsynchData();
                } else {

                    SnackBarUtils.showSnackBarBlue(MyApplication.getContext(), mActivity.findViewById(android.R.id.content), "Customer details saved successfully");

                    removeDataAfterSubmission();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onErrorImage(int reqestcode, String error) {
        }

    };

    private void removeDataAfterSubmission() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                for (int i = 0; i < bpnumberList.size(); i++) {
                    RealmResults<Customer> result = realm.where(Customer.class).equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, "")).and().equalTo("bp_number", bpnumberList.get(i).toString()).findAll();
                    result.deleteAllFromRealm();
                    long count = realm.where(Customer.class).count();

                }

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                txtNoData.setVisibility(View.VISIBLE);
                rv_history.setVisibility(View.GONE);

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {

            }
        });
    }

}
