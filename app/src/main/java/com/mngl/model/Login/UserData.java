package com.mngl.model.Login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bitware on 2/5/18.
 */

public class UserData {

    @SerializedName("user_firstname")
    @Expose
    private String userFirstname;
    @SerializedName("user_lastname")
    @Expose
    private String userLastname;
    @SerializedName("user_mobile")
    @Expose
    private String userMobile;
    @SerializedName("user_email")
    @Expose
    private String userEmail;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;
    @SerializedName("status")
    @Expose
    private String status;

    public String getUserFirstname() {
        return userFirstname;
    }

    public void setUserFirstname(String userFirstname) {
        this.userFirstname = userFirstname;
    }

    public String getUserLastname() {
        return userLastname;
    }

    public void setUserLastname(String userLastname) {
        this.userLastname = userLastname;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
