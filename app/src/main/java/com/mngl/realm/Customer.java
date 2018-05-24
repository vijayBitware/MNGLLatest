package com.mngl.realm;

import java.util.Arrays;

import io.realm.RealmObject;

/**
 * Created by bitware on 30/4/18.
 */

public class Customer extends RealmObject {

    String user_id, mu_number, meter_no, meter_reading, note, isAvailable, reason, bp_number, comment,
    customerName, customerMobile, addres, totalAmnt, userId;
    byte[] image;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddres() {
        return addres;
    }

    public void setAddres(String addres) {
        this.addres = addres;
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


    public String getTotalAmnt() {
        return totalAmnt;
    }

    public void setTotalAmnt(String totalAmnt) {
        this.totalAmnt = totalAmnt;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMeter_reading() {
        return meter_reading;
    }

    public void setMeter_reading(String meter_reading) {
        this.meter_reading = meter_reading;
    }

    public String getBp_number() {
        return bp_number;
    }

    public void setBp_number(String bp_number) {
        this.bp_number = bp_number;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMu_number() {
        return mu_number;
    }

    public void setMu_number(String mu_number) {
        this.mu_number = mu_number;
    }

    public String getMeter_no() {
        return meter_no;
    }

    public void setMeter_no(String meter_no) {
        this.meter_no = meter_no;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "user_id='" + user_id + '\'' +
                ", mu_number='" + mu_number + '\'' +
                ", meter_no='" + meter_no + '\'' +
                ", meter_reading='" + meter_reading + '\'' +
                ", note='" + note + '\'' +
                ", isAvailable='" + isAvailable + '\'' +
                ", reason='" + reason + '\'' +
                ", image=" + Arrays.toString(image) +
                '}';
    }
}
