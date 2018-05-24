package com.mngl.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.mngl.realm.Customer;
import com.mngl.realm.Society;
import com.mngl.webservice.APIRequest;
import com.mngl.webservice.BaseResponse;
import com.mngl.webservice.WebServiceImage;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;

import io.realm.Realm;
import io.realm.RealmResults;

public class NetworkBroadcast extends BroadcastReceiver implements APIRequest.ResponseHandler {

    Context contextObject;

    int count;
    JSONObject obj = null;
    JSONObject main_obj = null;
    Realm realm;
    Boolean network_status;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        network_status = NetworkStatus.isConnectingToInternet(context);

        contextObject = context;
        realm = Realm.getDefaultInstance();
        System.out.println("***********broadcast******");
        System.out.println("*********net state********"+network_status);
        if (network_status == true && Constant.count == 0) {
            Constant.count = 1;

            System.out.println("on Receieve if condition called=========================");

            Log.d("Connection", "On");

            Log.d("Connection after while", "On");

                    //uploadAsynchData();


        } else {
            Constant.count = 0;
            Log.d("Connection", "OFF");
        }

    }

    private void uploadAsynchData() {
        // TODO Auto-generated method stub
        Log.v("IN BROADCAST RECEIVER", "uploadAsynchData");

        SharedPref pref = new SharedPref(contextObject);
        obj = new JSONObject();
        main_obj = new JSONObject();
        JSONArray req = new JSONArray();

        try {

            RealmResults<Customer> results = realm.where(Customer.class).equalTo("userId", SharedPref.getPreferences().getString(SharedPref.USER_ID, "")).findAllAsync();
            results.load();
            count = results.size();
            //Log.e("TAG", "Current Database size >> " + count + "**" + results.size());

            for (int i = 0; i < results.size(); i++) {

                String bp_number = results.get(i).getBp_number();
                String meter_reading = results.get(i).getMeter_reading();
                String note = results.get(i).getNote();
                String comment = results.get(i).getComment();

                String meterimg = Base64.encodeToString(results.get(i).getImage(), Base64.DEFAULT);

                System.out.println("******bppppp*****" + bp_number);
                JSONObject reqObj = new JSONObject();
                reqObj.put("is_available", "true");
                reqObj.put("bp_number", bp_number);
                reqObj.put("meter_reading", meter_reading);
                reqObj.put("note", note);
                reqObj.put("comment", comment);
                reqObj.put("meter_image", meterimg);

                req.put(reqObj);
            }

            SharedPref sharedPref = new SharedPref(contextObject);
            SharedPreferences preferences = SharedPref.getPreferences();

            main_obj.put("result", req);
            System.out.println("******size*******" + req.length());

            System.out.println("Json Obj>>>>>" + main_obj.toString());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        submitDetails();

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

            WebServiceImage service = new WebServiceImage(callback);

            MultipartEntity reqEntity = new MultipartEntity();

            reqEntity.addPart("result", new StringBody(main_obj.toString()));

            service.getService(contextObject, Constant.submitCustomerDetails, reqEntity);
        } catch (NullPointerException e) {
            System.out.println("Nullpointer Exception at Login Screen" + e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    WebServiceImage.CallbackImage callback = new WebServiceImage.CallbackImage() {
        @Override
        public void onSuccessImage(int reqestcode, JSONObject rootjson) {
            // SnackBarUtils.showSnackBarPink(MyApplication.getContext(), contextObject.findViewById(android.R.id.content), "No record's found for relevant filter");

            String msg = count + " records saved successfully.";
            Toast.makeText(contextObject, msg, Toast.LENGTH_SHORT).show();
            removeDataAfterSubmission();
            System.out.println("++++++-result++++++" + rootjson);

        }

        @Override
        public void onErrorImage(int reqestcode, String error) {
        }

    };

    private void removeDataAfterSubmission() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Customer> result = realm.where(Customer.class).findAll();
                result.deleteAllFromRealm();
                long count = realm.where(Customer.class).count();
                System.out.println("********delete all*****" + count);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e("TAG", "onSuccess >> Data Inserted Successfully..!!");

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e("TAG", "onError >>" + error.toString());
            }
        });
    }

    @Override
    public void onSuccess(BaseResponse response) {

    }

    @Override
    public void onFailure(BaseResponse response) {

    }

}
