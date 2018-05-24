package com.mngl.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mngl.R;
import com.mngl.controller.AdapterHistory;
import com.mngl.model.societyMember.SocietyMember;
import com.mngl.utils.AlertClass;
import com.mngl.utils.Constant;
import com.mngl.utils.MyApplication;
import com.mngl.utils.NetworkStatus;
import com.mngl.utils.SnackBarUtils;
import com.mngl.webservice.APIRequest;
import com.mngl.webservice.BaseResponse;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by bitware on 8/5/18.
 */

public class FragmentHistory extends BaseFragment implements APIRequest.ResponseHandler {

    View view;
    @BindView(R.id.rv_history)
    RecyclerView rv_history;
    AdapterHistory adapterHistory;
    TextView txtTitle;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.txtNoData)
    TextView txtNoData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);
        init();
        if (NetworkStatus.isConnectingToInternet(getContext())) {
            new APIRequest(getContext(), new JSONObject(), Constant.history, this, Constant.API_HISTORY, Constant.GET, "yes");
        } else {
            SnackBarUtils.showSnackBarPink(MyApplication.getContext(), getActivity().findViewById(android.R.id.content), getResources().getString(R.string.no_internet));
        }
        return view;
    }

    private void init() {
        ImageView imgFilter = mActivity.findViewById(R.id.imgFilter);
        imgFilter.setVisibility(View.VISIBLE);
        txtTitle = getActivity().findViewById(R.id.txtTitle);
        txtTitle.setText("History");
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rv_history.setLayoutManager(manager);

    }

    @OnClick(R.id.imgBack)
    public void onBackClick() {
        mActivity.popFragments();
    }

    @Override
    public void onSuccess(BaseResponse response) {
        SocietyMember societyMember = (SocietyMember) response;
        if (societyMember.getSuccess()) {
            rv_history.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
            rv_history.setAdapter(new AdapterHistory(mActivity, societyMember.getData()));
        } else if (societyMember.getCode() == 401) {
            AlertClass.sessionExpiredDialog(getContext(), societyMember.getMsg());
        } else {
            rv_history.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onFailure(BaseResponse response) {

    }
}
