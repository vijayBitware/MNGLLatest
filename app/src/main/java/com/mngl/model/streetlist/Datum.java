package com.mngl.model.streetlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bitware on 14/5/18.
 */

public class Datum {

    @SerializedName("street")
    @Expose
    private String street;
    @SerializedName("street3")
    @Expose
    private String street3;
    @SerializedName("wing")
    @Expose
    private String wing;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet3() {
        return street3;
    }

    public void setStreet3(String street3) {
        this.street3 = street3;
    }

    public String getWing() {
        return wing;
    }

    public void setWing(String wing) {
        this.wing = wing;
    }
}
