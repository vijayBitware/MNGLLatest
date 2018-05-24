package com.mngl.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class for local storage of constants. This contains all web service url.
 *
 * @author (Arun Chougule)
 */

public class Constant {

    // public static String base_url = "http://192.168.1.128/mngl-web-services/api/";  //localhost
    public static String base_url = "http://159.203.89.161/laravel/mngl-web-services/api/";    //live server prev
    //    public static String base_url = "http://159.203.89.161/laravel/mngl-bitware/mngl-web-services/api/";
    public static String GET = "get";
    public static String POST = "post";
    public static String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)$";

    public static final int API_LOGIN = 101;
    public static final int API_FORGOT_PWD = 102;
    public static final int API_HOME = 103;
    public static final int API_SOCIETY_MEMBER = 104;
    public static final int API_FILTER = 105;
    public static final int API_SAVE_CUSTOMER_DETAILS = 108;
    public static final int API_REVISIT = 106;
    public static final int API_HISTORY = 107;
    public static final int API_STREET = 110;
    public static final int API_WING = 109;
    public static final int API_FILTER_BPNUMBER = 111;
    public static final int API_COUNT = 112;
    public static String revisit = base_url + "user/revisit";
    public static String history = base_url + "user/history";

    public static String userId = "";

    public static String position;
    public static String login = base_url + "user/login";
    public static String forget_pass = base_url + "user/forgot_password";
    public static String filter = base_url + "user/filter";
    public static String submitCustomerDetails = base_url + "customer/save";
    public static String street = base_url + "user/street3";
    public static String wings = base_url + "user/wing";
    public static String society_list = base_url + "user/society_list";
    public static String society_member = base_url + "user/customers";
    public static String filter_bpnumber = base_url + "user/get_customer";
    public static String getCount = base_url + "user/count";

    public static Bitmap imageCrop;
    public static int count = 0;
    public static String BP_NUMBER = "";
    public static String FRAGMENTCUSTOMER = "no";
    public static String FRAGMENTWING = "no";
    public static String FRAGMENTSTREET = "no";
    public static String FRAGMENTSOCIETY = "no";
    public static String FILTER = "";
    public static boolean isFilter = false;
    public static Uri imageUri;

    public static String onlyFirstLetterCapital(String s) {
        String upperCase = s.substring(0, 1).toUpperCase() + s.substring(1);
        return upperCase;
    }

    public static String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    public static String getTimeStamp() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        return timeStamp;
    }

    public static String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "MMM-dd-yyyy h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String formattedDate(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd MMM yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
}
