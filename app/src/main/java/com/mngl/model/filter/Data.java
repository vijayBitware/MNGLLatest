
package com.mngl.model.filter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("mru_name")
    @Expose
    private String mruName;
    @SerializedName("bp_no")
    @Expose
    private String bpNo;
    @SerializedName("meter_number")
    @Expose
    private String meterNumber;
    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("customer_mobile")
    @Expose
    private String customerMobile;
    @SerializedName("customer_telephone")
    @Expose
    private String customerTelephone;
    @SerializedName("house_type")
    @Expose
    private String houseType;
    @SerializedName("wing")
    @Expose
    private String wing;
    @SerializedName("house_number")
    @Expose
    private String houseNumber;
    @SerializedName("floor")
    @Expose
    private String floor;
    @SerializedName("street2")
    @Expose
    private String street2;
    @SerializedName("street3")
    @Expose
    private String street3;
    @SerializedName("society_name")
    @Expose
    private String societyName;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("total_amount")
    @Expose
    private String totalAmount;

    public String getMruName() {
        return mruName;
    }

    public void setMruName(String mruName) {
        this.mruName = mruName;
    }

    public String getBpNo() {
        return bpNo;
    }

    public void setBpNo(String bpNo) {
        this.bpNo = bpNo;
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public String getCustomerTelephone() {
        return customerTelephone;
    }

    public void setCustomerTelephone(String customerTelephone) {
        this.customerTelephone = customerTelephone;
    }

    public String getHouseType() {
        return houseType;
    }

    public void setHouseType(String houseType) {
        this.houseType = houseType;
    }

    public String getWing() {
        return wing;
    }

    public void setWing(String wing) {
        this.wing = wing;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getStreet3() {
        return street3;
    }

    public void setStreet3(String street3) {
        this.street3 = street3;
    }

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

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

}
