package com.mngl.model.count;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mngl.webservice.BaseResponse;

import java.util.List;

/**
 * Created by bitware on 22/5/18.
 */

public class GetCount extends BaseResponse {

    @SerializedName("count")
    @Expose
    private List<Count> count = null;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("code")
    @Expose
    private Integer code;

    public List<Count> getCount() {
        return count;
    }

    public void setCount(List<Count> count) {
        this.count = count;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
