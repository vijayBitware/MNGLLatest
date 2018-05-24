package com.mngl.controller;

import android.content.Context;
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
import com.mngl.model.societyMember.Datum;
import com.mngl.utils.Constant;
import com.mngl.view.activity.DrawerActivity;
import com.mngl.view.fragment.CustomerDetailsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Admin on 4/26/2018.
 */

/**
 * Created by Admin on 4/24/2018.
 */

public class AdapterRevisitingCustomers extends RecyclerView.Adapter<AdapterRevisitingCustomers.MyViewHolder> {

    Context context, mContext;
    List<Datum> modellist = new ArrayList<>();
    String flag;
    Typeface face;
    DrawerActivity activity;

    public AdapterRevisitingCustomers(DrawerActivity activity, List<Datum> modellist, String flag) {

        this.activity = activity;
        this.context = context;
        this.modellist = modellist;
        this.mContext = context;
        this.flag = flag;

        face = Typeface.createFromAsset(activity.getAssets(), "lato_regular.ttf");

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (flag.equalsIgnoreCase("revisit")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wing_list, parent, false);
        } else if (flag.equalsIgnoreCase("wing_list")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wing_list, parent, false);
        } else if (flag.equalsIgnoreCase("home")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wing_list, parent, false);
        } else if (flag.equalsIgnoreCase("revisiting_customers_offline")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_revisiting_customers, parent, false);
        }
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.txtName.setText(modellist.get(position).getCustomerName());
        holder.txtMobileNo.setText(modellist.get(position).getCustomerMobile());
        if (flag.equalsIgnoreCase("revisiting_customers_offline")) {
            holder.txtAddress.setText(modellist.get(position).getLocation());
        } else {
            if (modellist.get(position).getHouseType().equalsIgnoreCase("FLAT")) {
                if (modellist.get(position).getWing().isEmpty()) {
                    holder.txtAddress.setText(modellist.get(position).getSocietyName() + " " + modellist.get(position).getStreet3() + "\n" + "Flat No. :" + modellist.get(position).getHouseNumber() + " " + modellist.get(position).getStreet2() + " " +modellist.get(position).getArea());

                } else {
                    holder.txtAddress.setText(modellist.get(position).getSocietyName() + " " + modellist.get(position).getStreet3() + "\n" + "Wing " + modellist.get(position).getWing() + " Flat No. :" + modellist.get(position).getHouseNumber() + " " + modellist.get(position).getStreet2() + " " +modellist.get(position).getArea());

                }
            } else if (modellist.get(position).getHouseType().equalsIgnoreCase("PLOT")) {
                holder.txtAddress.setText(modellist.get(position).getSocietyName() + " " + modellist.get(position).getStreet2() + " " + modellist.get(position).getStreet3() + " " + modellist.get(position).getHouseType()+ " " +modellist.get(position).getArea());
            } else if (modellist.get(position).getHouseType().equalsIgnoreCase("BUNGALOW")) {
                holder.txtAddress.setText(modellist.get(position).getSocietyName() + " " + modellist.get(position).getStreet2() + " " + modellist.get(position).getStreet3() + " " + modellist.get(position).getHouseType()+ " " +modellist.get(position).getArea());
            } else {
                holder.txtAddress.setText(modellist.get(position).getSocietyName() + " " + modellist.get(position).getStreet3() + "\n" + "Wing " + modellist.get(position).getWing() + " Flat No. :" + modellist.get(position).getHouseNumber() + " " + modellist.get(position).getStreet2()+ " " +modellist.get(position).getArea());

            }
        }

        holder.ll_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.position = String.valueOf(position);
                Bundle bundle = new Bundle();
                bundle.putString("name", modellist.get(position).getCustomerName());
                bundle.putString("bp_no", modellist.get(position).getBpNo());
                bundle.putString("mru_name", modellist.get(position).getMruName());
                bundle.putString("mobile", modellist.get(position).getCustomerMobile());
                bundle.putString("meter_number", modellist.get(position).getMeterNumber());
                bundle.putString("address", modellist.get(position).getSocietyName() + " " + modellist.get(position).getStreet3() + "\n"
                        + "Wing " + modellist.get(position).getWing() + " Flat No.: " + modellist.get(position).getHouseNumber() + " " + modellist.get(position).getStreet2()
                        + "\n" + modellist.get(position).getLocation() + "\n"+modellist.get(position).getArea());
                bundle.putString("amount", modellist.get(position).getTotal_amount());
                activity.pushFragments(new CustomerDetailsFragment(), true, true, bundle, "");

            }
        });

    }

    @Override
    public int getItemCount() {
        return modellist.size();
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








