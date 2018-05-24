package com.mngl.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mngl.R;
import com.mngl.utils.FontChangeCrawler;
import com.mngl.utils.SharedPref;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by bitware on 8/5/18.
 */

public class FragmentProfile extends BaseFragment {

    View view;
    @BindView(R.id.txtName)
    TextView txtName;
    @BindView(R.id.txtEmail)
    TextView txtEmail;
    @BindView(R.id.txtMobile)
    TextView txtMobile;
    SharedPref pref;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    TextView txtTitle;
    @BindView(R.id.imgProfile)
    ImageView imgProfile;
    FontChangeCrawler fontChanger;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        pref = new SharedPref(getContext());
        txtTitle = getActivity().findViewById(R.id.txtTitle);
        txtTitle.setText("Profile");

        txtName.setText(SharedPref.getPreferences().getString(SharedPref.USER_FIRSTNAME, "")
                + " " + SharedPref.getPreferences().getString(SharedPref.USER_LASTNAME, ""));
        txtEmail.setText(SharedPref.getPreferences().getString(SharedPref.USER_EMAIL, ""));
        txtMobile.setText(SharedPref.getPreferences().getString(SharedPref.USER_MOBILE, ""));
        Glide.with(getActivity()).load(SharedPref.getPreferences().getString(SharedPref.USER_IMAGE, "")).placeholder(R.mipmap.ic_useprofileblack).into(imgProfile);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fontChanger = new FontChangeCrawler(mActivity.getAssets());
        fontChanger.replaceFonts((ViewGroup) mActivity.findViewById(android.R.id.content));
    }

    @OnClick(R.id.imgBack)
    public void onBackClick() {
        mActivity.popFragments();
    }
}
