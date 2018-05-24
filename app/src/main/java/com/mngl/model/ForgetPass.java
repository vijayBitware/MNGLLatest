package com.mngl.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mngl.webservice.BaseResponse;

/**
 * Created by bitware on 3/5/18.
 */

public class ForgetPass extends BaseResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("code")
    @Expose
    private Integer code;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
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

}
