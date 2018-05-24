package com.mngl.model.home;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bitware on 3/5/18.
 */

public class Datum {

    @SerializedName("society_name")
    @Expose
    private String societyName;
    @SerializedName("location")
    @Expose
    private String location;

    public String getSocietyName() {
        return societyName;
    }

    public void setSocietyName(String societyName) {
        this.societyName = societyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
