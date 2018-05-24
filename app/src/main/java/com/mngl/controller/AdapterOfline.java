package com.mngl.controller;

import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mngl.R;
import com.mngl.model.societyMember.Datum;
import com.mngl.realm.Customer;
import com.mngl.view.activity.DrawerActivity;

import java.util.List;

/**
 * Created by Admin on 5/11/2018.
 */

public class AdapterOfline extends RecyclerView.Adapter<AdapterOfline.MyViewHolder> {

    DrawerActivity activity;
    Typeface face;
    List<Customer> arrList ;

    public AdapterOfline(DrawerActivity activity, List<Customer> arrList) {
        this.activity = activity;
        this.arrList = arrList;
        face = Typeface.createFromAsset(activity.getAssets(),"lato_regular.ttf");
    }

    @Override
    public AdapterOfline.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_history, parent, false);
        return new AdapterOfline.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AdapterOfline.MyViewHolder holder, final int position) {
      holder.txtName.setText(arrList.get(position).getCustomerName());
        holder.txtMobileNo.setText(arrList.get(position).getCustomerMobile());
        holder.txtAddress.setVisibility(View.GONE);


    }


    @Override
    public int getItemCount() {
        return arrList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtName,txtAddress,txtMobileNo;
        ImageView imgNext;
        CardView ll_row;

        public MyViewHolder(View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            imgNext = itemView.findViewById(R.id.imgNext);
            ll_row = itemView.findViewById(R.id.ll_row);
            txtMobileNo = itemView.findViewById(R.id.txtMobileNo);

            txtName.setTypeface(face);
            txtAddress.setTypeface(face);
        }
    }
}
