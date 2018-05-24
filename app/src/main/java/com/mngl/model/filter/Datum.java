package com.mngl.model.filter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bitware on 4/5/18.
 */

public class Datum {

    @SerializedName("customer_id")
    @Expose
    private Integer customerId;
    @SerializedName("bp_no")
    @Expose
    private String bpNo;
    @SerializedName("bp_name")
    @Expose
    private String bpName;
    @SerializedName("mru_id")
    @Expose
    private Integer mruId;
    @SerializedName("installation_number")
    @Expose
    private String installationNumber;
    @SerializedName("device_serial_number")
    @Expose
    private String deviceSerialNumber;
    @SerializedName("co_name")
    @Expose
    private Object coName;
    @SerializedName("customer_telephone")
    @Expose
    private Object customerTelephone;
    @SerializedName("customer_mobile")
    @Expose
    private String customerMobile;
    @SerializedName("building_number")
    @Expose
    private String buildingNumber;
    @SerializedName("house_number_supplement")
    @Expose
    private String houseNumberSupplement;
    @SerializedName("house_number")
    @Expose
    private String houseNumber;
    @SerializedName("floor_in_building")
    @Expose
    private String floorInBuilding;
    @SerializedName("street2")
    @Expose
    private Object street2;
    @SerializedName("street3")
    @Expose
    private Object street3;
    @SerializedName("street")
    @Expose
    private String street;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("area")
    @Expose
    private String area;
    @SerializedName("city_postal_code")
    @Expose
    private String cityPostalCode;
    @SerializedName("register")
    @Expose
    private String register;
    @SerializedName("scheduled_meter_reading")
    @Expose
    private String scheduledMeterReading;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getBpNo() {
        return bpNo;
    }

    public void setBpNo(String bpNo) {
        this.bpNo = bpNo;
    }

    public String getBpName() {
        return bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    public Integer getMruId() {
        return mruId;
    }

    public void setMruId(Integer mruId) {
        this.mruId = mruId;
    }

    public String getInstallationNumber() {
        return installationNumber;
    }

    public void setInstallationNumber(String installationNumber) {
        this.installationNumber = installationNumber;
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public Object getCoName() {
        return coName;
    }

    public void setCoName(Object coName) {
        this.coName = coName;
    }

    public Object getCustomerTelephone() {
        return customerTelephone;
    }

    public void setCustomerTelephone(Object customerTelephone) {
        this.customerTelephone = customerTelephone;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getHouseNumberSupplement() {
        return houseNumberSupplement;
    }

    public void setHouseNumberSupplement(String houseNumberSupplement) {
        this.houseNumberSupplement = houseNumberSupplement;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getFloorInBuilding() {
        return floorInBuilding;
    }

    public void setFloorInBuilding(String floorInBuilding) {
        this.floorInBuilding = floorInBuilding;
    }

    public Object getStreet2() {
        return street2;
    }

    public void setStreet2(Object street2) {
        this.street2 = street2;
    }

    public Object getStreet3() {
        return street3;
    }

    public void setStreet3(Object street3) {
        this.street3 = street3;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCityPostalCode() {
        return cityPostalCode;
    }

    public void setCityPostalCode(String cityPostalCode) {
        this.cityPostalCode = cityPostalCode;
    }

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public String getScheduledMeterReading() {
        return scheduledMeterReading;
    }

    public void setScheduledMeterReading(String scheduledMeterReading) {
        this.scheduledMeterReading = scheduledMeterReading;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
