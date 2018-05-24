package com.mngl.webservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.mngl.model.ForgetPass;
import com.mngl.model.Login.Login;
import com.mngl.model.count.GetCount;
import com.mngl.model.filter.BPNumerResponse;
import com.mngl.model.home.Home;
import com.mngl.model.societyMember.SocietyMember;
import com.mngl.model.streetlist.Street;
import com.mngl.utils.Constant;
import com.mngl.utils.SharedPref;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bitwarepc on 04-Jul-17.
 */

public class APIRequest extends AppCompatActivity {

    private JSONObject mJsonObject;
    private String mUrl, authorization;
    private ResponseHandler responseHandler;
    private int API_NAME;
    private Context mContext;
    ProgressDialog progressDialog;
    BaseResponse baseResponse;
    SharedPref pref;

    public APIRequest(Context context, JSONObject jsonObject, String url, ResponseHandler responseHandler, int api, String methodName, String authorization) {
        this.responseHandler = responseHandler;
        this.API_NAME = api;
        this.mUrl = url;
        this.mJsonObject = jsonObject;
        this.mContext = context;
        this.authorization = authorization;

        pref = new SharedPref(context);

        System.out.println("api NO >>> " + api);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (methodName.equals(Constant.GET)) {
            System.out.println("***method*****" + methodName);
            apiGetRequest();
        } else if (methodName.equals(Constant.POST)) {
            System.out.println("***method*****" + methodName);
            apiPostRequest();
        } else {
            // apiDeleteRequest();
        }
    }

    private void apiPostRequest() {
        System.out.println("***apiPostRequest*****");
        String REQUEST_TAG = String.valueOf(API_NAME);
        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(mUrl, mJsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(" >>> API RESPONSE " + response);
                        setResponseToBody(response);
                        progressDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("**********error********" + error.toString());

                progressDialog.dismiss();
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                System.out.println("user token >> " + SharedPref.getPreferences().getString(SharedPref.USER_TOKEN, ""));
                String bearer = "Bearer ".concat(SharedPref.getPreferences().getString(SharedPref.USER_TOKEN, ""));
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                if (authorization.equalsIgnoreCase("yes"))
                    headers.put("Authorization", bearer);
                return headers;
            }

        };

        jsonObjectReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(mContext).addToRequestQueue(jsonObjectReq, REQUEST_TAG);

    }

    private void apiGetRequest() {
        System.out.println("********get******");
        String REQUEST_TAG = String.valueOf(API_NAME);
        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(mUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response is " + response);
                        setResponseToBody(response);
                        progressDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("************" + error.toString());
//
                progressDialog.dismiss();
            }
        })

        {
            @Override
            public Map getHeaders() throws AuthFailureError {
                System.out.println("user token >> " + SharedPref.getPreferences().getString(SharedPref.USER_TOKEN, ""));
                String bearer = "Bearer ".concat(SharedPref.getPreferences().getString(SharedPref.USER_TOKEN, ""));
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                if (authorization.equalsIgnoreCase("yes"))
                    headers.put("Authorization", bearer);
                return headers;
            }

        };

        jsonObjectReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppSingleton.getInstance(mContext).addToRequestQueue(jsonObjectReq, REQUEST_TAG);
    }

    private void setResponseToBody(JSONObject response) {
        Gson gson = new Gson();
        System.out.println("*****API NAme******" + API_NAME);
        switch (API_NAME) {
            case Constant.API_LOGIN:
                baseResponse = gson.fromJson(response.toString(), Login.class);
                break;
            case Constant.API_FORGOT_PWD:
                baseResponse = gson.fromJson(response.toString(), ForgetPass.class);
                break;
            case Constant.API_HOME:
                baseResponse = gson.fromJson(response.toString(), Home.class);
                break;
            case Constant.API_SOCIETY_MEMBER:
                baseResponse = gson.fromJson(response.toString(), SocietyMember.class);
                break;
            case Constant.API_FILTER:
                if(Constant.FILTER.equalsIgnoreCase("home"))
                {
                    baseResponse = gson.fromJson(response.toString(), Home.class);
                }else if(Constant.FILTER.equalsIgnoreCase("street"))
                {
                    baseResponse = gson.fromJson(response.toString(), Street.class);
                }

                break;
            case Constant.API_SAVE_CUSTOMER_DETAILS:
                baseResponse = gson.fromJson(response.toString(), SocietyMember.class);
                break;
            case Constant.API_REVISIT:
                baseResponse = gson.fromJson(response.toString(), SocietyMember.class);
                break;
            case Constant.API_HISTORY:
                baseResponse = gson.fromJson(response.toString(), SocietyMember.class);
                break;
            case Constant.API_STREET:
                baseResponse = gson.fromJson(response.toString(), Street.class);
                break;
            case Constant.API_WING:
                baseResponse = gson.fromJson(response.toString(), Street.class);
                break;

            case Constant.API_FILTER_BPNUMBER:
                baseResponse = gson.fromJson(response.toString(), BPNumerResponse.class);
                break;
            case Constant.API_COUNT:
                baseResponse = gson.fromJson(response.toString(), GetCount.class);
                break;

        }
        baseResponse.setApiName(API_NAME);
        responseHandler.onSuccess(baseResponse);
    }

    public interface ResponseHandler {
        public void onSuccess(BaseResponse response);

        public void onFailure(BaseResponse response);
    }

}



