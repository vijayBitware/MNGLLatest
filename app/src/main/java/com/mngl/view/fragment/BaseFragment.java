package com.mngl.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.mngl.utils.MyApplication;
import com.mngl.view.activity.DrawerActivity;
/**
 * Created by Dvimay on 6/30/2016.
 */
public class BaseFragment extends Fragment {

    public DrawerActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = (DrawerActivity) this.getActivity();

        System.out.println("BaseFragment OnCreate");

    }

    public MyApplication app() {
        return ((MyApplication) getActivity().getApplication());
    }

    public boolean onBackPressed() {
        return false;
    }
}
