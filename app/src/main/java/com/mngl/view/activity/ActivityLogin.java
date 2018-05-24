package com.mngl.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mngl.R;
import com.mngl.model.ForgetPass;
import com.mngl.model.Login.Login;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Admin on 4/27/2018.
 */

public class ActivityLogin extends AppCompatActivity implements APIRequest.ResponseHandler {

    @BindView(R.id.edtUsername)
    EditText edtUsername;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.txt_forgotPwd)
    TextView txt_forgotPwd;
    FontChangeCrawler fontChanger;
    String username, password;
    public static String EMAIL_PATTERN;
    Typeface face, face1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // bind the view using butterknife
        ButterKnife.bind(this);
        fontChanger = new FontChangeCrawler(getAssets());
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        init();

    }

    private void init() {
        SharedPref pref = new SharedPref(this);
    }

    @OnClick(R.id.btnLogin)
    public void onLoginClick(View view) {
        if (validationLogin()) {

            if (NetworkStatus.isConnectingToInternet(ActivityLogin.this)) {
                loginWebservice();
            } else {
                SnackBarUtils.showSnackBarPink(MyApplication.getContext(), findViewById(android.R.id.content), getResources().getString(R.string.no_internet));
            }
        }
    }

    private void loginWebservice() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", edtUsername.getText().toString());
            jsonObject.put("password", edtPassword.getText().toString());
            Log.e("TAG", "Login request >> " + jsonObject);
            new APIRequest(ActivityLogin.this, jsonObject, Constant.login, this, Constant.API_LOGIN, Constant.POST, "no");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.txt_forgotPwd)
    public void onForgetPassWordClick(View view) {
        PopupForgotPass();
    }

    private void PopupForgotPass() {

        face = Typeface.createFromAsset(getAssets(), "lato_regular.ttf");
        face1 = Typeface.createFromAsset(getAssets(), "lato_bold.ttf");
        final Dialog dialog = new Dialog(ActivityLogin.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_forgot_password);

        // set the custom dialog components - text, image and button

        TextView txtAlertTile = dialog.findViewById(R.id.txtAlertTile);
        final TextView btn_yes = (TextView) dialog.findViewById(R.id.btn_yes);
        final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
        final TextView btn_no = (TextView) dialog.findViewById(R.id.btn_no);

        txtAlertTile.setTypeface(face1);
        btn_yes.setTypeface(face1);
        btn_no.setTypeface(face1);
        edt_message.setTypeface(face);



        edt_message.setHint("Please enter username");

        // if button is clicked, close the custom dialog
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!edt_message.getText().toString().isEmpty()) {
                    callForgetPasswordApi(edt_message.getText().toString());
                } else {
                    edt_message.setError("Please enter username");
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

    private void callForgetPasswordApi(String username) {
        if (NetworkStatus.isConnectingToInternet(MyApplication.getContext())) {
            System.out.println("Forget pass url >> " + Constant.forget_pass);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", username);
                Log.e("TAG", "Forgot pass >>" + jsonObject);
                new APIRequest(ActivityLogin.this, jsonObject, Constant.forget_pass, this, Constant.API_FORGOT_PWD, Constant.POST, "no");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            SnackBarUtils.showSnackBarPink(MyApplication.getContext(), findViewById(android.R.id.content), getResources().getString(R.string.no_internet));
        }
    }

    public boolean validationLogin() {

        username = edtUsername.getText().toString();
        password = edtPassword.getText().toString();

        if (username.isEmpty()) {

            SnackBarUtils.showSnackBarPink(this, findViewById(android.R.id.content), getResources().getString(R.string.error_username));
            hideKeyboard(edtUsername);

            return false;
        } else if (password.isEmpty()) {

            SnackBarUtils.showSnackBarPink(this, findViewById(android.R.id.content), getResources().getString(R.string.error_password));
            hideKeyboard(edtPassword);
            return false;
        } else if (password.length() < 6) {
           SnackBarUtils.showSnackBarPink(this, findViewById(android.R.id.content), getResources().getString(R.string.error_pwd_length));
            hideKeyboard(edtPassword);
            return false;

        }
        return true;
    }

    private boolean isValidEmail(String email) {
        // TODO Auto-generated method stub
        Pattern pattern = Pattern.compile(Constant.EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }

    public void hideKeyboard(EditText edt) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }

    @Override
    public void onSuccess(BaseResponse response) {
        if (response.getApiName() == Constant.API_LOGIN) {
            Login login = (Login) response;
            if (login.getSuccess()) {
                SharedPref.writeString(SharedPref.USER_TOKEN, login.getToken());
                SharedPref.writeString(SharedPref.USER_ID, String.valueOf(login.getUserID()));
                SharedPref.writeString(SharedPref.USER_FIRSTNAME, login.getUserData().getUserFirstname());
                SharedPref.writeString(SharedPref.USER_LASTNAME, login.getUserData().getUserLastname());
                SharedPref.writeString(SharedPref.USER_EMAIL, login.getUserData().getUserEmail());
                SharedPref.writeString(SharedPref.USER_MOBILE, login.getUserData().getUserMobile());
                SharedPref.writeString(SharedPref.USER_STATUS, login.getUserData().getStatus());
                SharedPref.writeString(SharedPref.USER_IMAGE, login.getUserData().getProfileImage());
                SharedPref.writeString(SharedPref.LOG_IN, "yes");
                SnackBarUtils.showSnackBarBlue(MyApplication.getContext(), findViewById(android.R.id.content), login.getMsg());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent1 = new Intent(ActivityLogin.this, DrawerActivity.class);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.slide_out_left);
                    }
                }, 1000);
            } else {
                SnackBarUtils.showSnackBarPink(MyApplication.getContext(), findViewById(android.R.id.content), login.getMsg());

            }
        } else {
            ForgetPass forgetPass = (ForgetPass) response;
            if (forgetPass.getSuccess()) {
                SnackBarUtils.showSnackBarBlue(MyApplication.getContext(), findViewById(android.R.id.content), forgetPass.getMsg());
            } else {
                SnackBarUtils.showSnackBarPink(MyApplication.getContext(), findViewById(android.R.id.content), forgetPass.getMsg());
            }
        }

    }

    @Override
    public void onFailure(BaseResponse response) {

    }
}
