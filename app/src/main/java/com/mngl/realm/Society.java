package com.mngl.realm;

import io.realm.RealmObject;

/**
 * Created by Admin on 5/8/2018.
 */

public class Society extends RealmObject {
    String societyName, location, area, userId, isRevisit;

    public String getIsRevisit() {
        return isRevisit;
    }

    public void setIsRevisit(String isRevisit) {
        this.isRevisit = isRevisit;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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
}
