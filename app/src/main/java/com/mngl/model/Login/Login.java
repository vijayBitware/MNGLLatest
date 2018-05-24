package com.mngl.model.Login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mngl.webservice.BaseResponse;

/**
 * Created by bitware on 2/5/18.
 */

public class Login extends BaseResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("user_id")
    @Expose
    private Integer user_id;
    @SerializedName("user_data")
    @Expose
    private UserData userData;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getUserID() {
        return user_id;
    }

    public void setUserID(Integer user) {
        this.user_id = user;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }
}
