package com.mngl.model.count;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bitware on 22/5/18.
 */

public class Count {
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("completed")
    @Expose
    private Integer completed;
    @SerializedName("not_completed")
    @Expose
    private Integer notCompleted;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }

    public Integer getNotCompleted() {
        return notCompleted;
    }

    public void setNotCompleted(Integer notCompleted) {
        this.notCompleted = notCompleted;
    }


}
