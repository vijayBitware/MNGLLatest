package com.mngl.view.fragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mngl.R;
import com.mngl.model.count.Count;
import com.mngl.model.count.GetCount;
import com.mngl.utils.Constant;
import com.mngl.utils.EventDecorator;
import com.mngl.utils.FontChangeCrawler;
import com.mngl.utils.HighlightWeekendsDecorator;
import com.mngl.utils.MyApplication;
import com.mngl.utils.MySelectorDecorator;
import com.mngl.utils.NetworkStatus;
import com.mngl.utils.OneDayDecorator;
import com.mngl.utils.SnackBarUtils;
import com.mngl.webservice.APIRequest;
import com.mngl.webservice.BaseResponse;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bitware on 22/5/18.
 */

public class FragmentMonthlyRecord extends BaseFragment implements OnDateSelectedListener, OnMonthChangedListener,APIRequest.ResponseHandler {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    View view;
    @BindView(R.id.calendarView)
    MaterialCalendarView calendarView;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    @BindView(R.id.txtCompleted)
    TextView txtCompleted;
    @BindView(R.id.txtNotCompleted)
    TextView txtNotCompleted;
    List<Count> listCount = new ArrayList<>();
    FontChangeCrawler fontChanger;
    String month = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_monthly_record,container,false);
        ButterKnife.bind(this,view);


        calendarView.setOnDateChangedListener(this);
        calendarView.setOnMonthChangedListener(this);
        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);

        Calendar instance = Calendar.getInstance();
        calendarView.setSelectedDate(instance.getTime());

        Calendar instance1 = Calendar.getInstance();
        instance1.set(instance1.get(Calendar.YEAR), Calendar.JANUARY, 1);

        Calendar instance2 = Calendar.getInstance();
        int month = instance2.get(Calendar.MONTH) ;
        instance2.set(instance2.get(Calendar.YEAR),month , 31);

        calendarView.state().edit()
                .setMinimumDate(instance1.getTime())
                .setMaximumDate(instance2.getTime())
                .commit();

        calendarView.addDecorators(
                new MySelectorDecorator(mActivity),
                new HighlightWeekendsDecorator(),
                oneDayDecorator
        );

       // new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fontChanger = new FontChangeCrawler(mActivity.getAssets());
        fontChanger.replaceFonts((ViewGroup) mActivity.findViewById(android.R.id.content));
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        if (String.valueOf(date.getMonth()).length() == 1) {
            month = "0" + String.valueOf(date.getMonth());
        } else {
            month = String.valueOf(date.getMonth());
        }
        String year = String.valueOf(date.getYear());

        callGetCOuntApi(month, year);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        String month="";
        String selectedMonth = String.valueOf(date.getMonth() +1);
        if (selectedMonth.length() ==1){
            month = "0"+selectedMonth;
        }else {
            month = selectedMonth;
        }
        String selectedDate = date.getYear() +"-"+month +"-"+date.getDay();
        System.out.println("Selected Date >> " +selectedDate);
        for (int i=0;i<listCount.size();i++){
            if (listCount.get(i).getDate().equalsIgnoreCase(selectedDate)){
                txtCompleted.setText("Completed - " + String.valueOf(listCount.get(i).getCompleted()));
                txtNotCompleted.setText("Pending - " + String.valueOf(listCount.get(i).getNotCompleted()));
            }
        }
    }

    private void callGetCOuntApi(String month,String year){
        int mon = Integer.parseInt(month) + 1;
        if (NetworkStatus.isConnectingToInternet(mActivity)) {
            JSONObject jsonObject = new JSONObject();

            try {
                if (mon < 10) {
                    String mnth = "0" + String.valueOf(mon);

                    jsonObject.put("month", mnth);
                } else {

                    jsonObject.put("month", String.valueOf(mon));
                }

                jsonObject.put("year", year);
                Log.e("TAG", "Monthly record request > " + jsonObject);
                new APIRequest(mActivity, jsonObject, Constant.getCount, this, Constant.API_COUNT, Constant.POST, "yes");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            SnackBarUtils.showSnackBarPink(MyApplication.getContext(), getActivity().findViewById(android.R.id.content), "No record's found for relevant filter");
        }
    }

    @Override
    public void onSuccess(BaseResponse response) {
        GetCount getCount = (GetCount) response;
        if (getCount.getSuccess()){
            //plot data here
            listCount = getCount.getCount();
            Date currentDate = Calendar.getInstance().getTime();
            System.out.println("Current time => " + currentDate);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-d");
            String formattedDate = df.format(currentDate);
            System.out.println("Formatted time => " + formattedDate);


            Calendar calendar = Calendar.getInstance();

            calendar.add(Calendar.MONTH, Integer.parseInt(month));
            //plot data here
            ArrayList<CalendarDay> completedDates = new ArrayList<>();
            for (int i = 0; i < listCount.size(); i++) {

                if (listCount.get(i).getDate().equalsIgnoreCase(formattedDate)){
                    txtCompleted.setText("Completed - " +listCount.get(i).getCompleted());
                    txtNotCompleted.setText("Pending - " + listCount.get(i).getNotCompleted());
                }

                if (listCount.get(i).getCompleted() != 0) {

                    String date = listCount.get(i).getDate();

                    String split[] = date.split("-");

                    String year = split[0];
                    String month = split[1];
                    String day = split[2];

                    CalendarDay calendarDay = CalendarDay.from(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
                    completedDates.add(calendarDay);

                }

            }
            calendarView.addDecorator(new EventDecorator(Color.GREEN, completedDates));

            ArrayList<CalendarDay> incompletedDates = new ArrayList<>();
            for (int i = 0; i < listCount.size(); i++) {

                if (listCount.get(i).getNotCompleted() != 0) {

                    String date = listCount.get(i).getDate();

                    String split[] = date.split("-");

                    String year = split[0];
                    String month = split[1];
                    String day = split[2];

                    CalendarDay calendarDay = CalendarDay.from(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
                    incompletedDates.add(calendarDay);

                }

            }
            calendarView.addDecorator(new EventDecorator(Color.RED, incompletedDates));
        }
    }

    @Override
    public void onFailure(BaseResponse response) {

    }
}
