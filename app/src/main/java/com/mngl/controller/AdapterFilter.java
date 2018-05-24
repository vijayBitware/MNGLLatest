package com.mngl.controller;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mngl.R;

import com.mngl.model.filter.Datum;
import com.mngl.utils.Constant;
import com.mngl.view.activity.DrawerActivity;
import com.mngl.view.fragment.CustomerDetailsFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bitware on 4/5/18.
 */

public class AdapterFilter extends RecyclerView.Adapter<AdapterFilter.MyViewHolder> {

    DrawerActivity activity;
    Typeface face;
    List<Datum> arrList;

    public AdapterFilter(DrawerActivity activity, List<Datum> arrList) {
        this.activity = activity;
        this.arrList = arrList;
        face = Typeface.createFromAsset(activity.getAssets(), "lato_regular.ttf");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wing_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.txtMobileNo.setText(arrList.get(position).getCustomerMobile());
        if (arrList.get(position).getHouseNumberSupplement().equalsIgnoreCase("FLAT")) {
            if (arrList.get(position).getBuildingNumber().isEmpty()) {
                holder.txtAddress.setText("Flat No. :" + arrList.get(position).getHouseNumber());

            } else {
                holder.txtAddress.setText("Wing " + arrList.get(position).getBuildingNumber() + "\tFlat No. :" + arrList.get(position).getHouseNumber());

            }
        } else if (arrList.get(position).getHouseNumberSupplement().equalsIgnoreCase("PLOT")) {
            holder.txtAddress.setText("PLOT");
        } else if (arrList.get(position).getHouseNumberSupplement().equalsIgnoreCase("BUNGALOW")) {
            holder.txtAddress.setText("BUNGALOW");
        }

        holder.ll_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.position = String.valueOf(position);
                Bundle bundle = new Bundle();

                bundle.putString("bp_no", arrList.get(position).getBpNo());

                bundle.putString("mobile", arrList.get(position).getCustomerMobile());

                bundle.putString("address", arrList.get(position).getLocation());
                bundle.putString("amount", "");
                activity.pushFragments(new CustomerDetailsFragment(), true, true, bundle, "");
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.txtAddress)
        TextView txtAddress;
        @BindView(R.id.txtMobileNo)
        TextView txtMobileNo;
        ImageView imgNext;

        @BindView(R.id.ll_row)
        CardView ll_row;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            imgNext = itemView.findViewById(R.id.imgBack);
            txtName.setTypeface(face);
            txtMobileNo.setTypeface(face);
            txtAddress.setTypeface(face);
        }
    }
}
