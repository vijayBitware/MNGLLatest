package com.mngl.model.filter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mngl.webservice.BaseResponse;

import java.util.List;

/**
 * Created by bitware on 4/5/18.
 */

public class Filter extends BaseResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("code")
    @Expose
    private Integer code;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
