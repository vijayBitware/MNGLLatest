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
import com.mngl.view.activity.DrawerActivity;

import java.util.List;

/**
 * Created by bitware on 8/5/18.
 */

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.MyViewHolder> {

    DrawerActivity activity;
    Typeface face;
    List<Datum> arrList ;

    public AdapterHistory(DrawerActivity activity, List<Datum> arrList) {
        this.activity = activity;
        this.arrList = arrList;
        face = Typeface.createFromAsset(activity.getAssets(),"lato_regular.ttf");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_history, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
       holder.txtName.setText(arrList.get(position).getCustomerName());
        if(arrList.get(position).getCustomerMobile().equalsIgnoreCase(""))
        {
            holder.txtMobileNo.setVisibility(View.GONE);
        }else
        {
            holder.txtMobileNo.setVisibility(View.VISIBLE);
            holder.txtMobileNo.setText(arrList.get(position).getCustomerMobile());
        }

        if (arrList.get(position).getHouseType().equalsIgnoreCase("FLAT")) {
            if (arrList.get(position).getWing().isEmpty()) {
                holder.txtAddress.setText(arrList.get(position).getSocietyName() + " " + arrList.get(position).getStreet3() +"\n"+ "Flat No. :" + arrList.get(position).getHouseNumber() + " " + arrList.get(position).getStreet2());
            } else {
                holder.txtAddress.setText(arrList.get(position).getSocietyName() + " " + arrList.get(position).getStreet3()+"\n" + "Wing " + arrList.get(position).getWing() + " Flat No. :" + arrList.get(position).getHouseNumber() + " " + arrList.get(position).getStreet2());
            }
        } else if (arrList.get(position).getHouseType().equalsIgnoreCase("PLOT")) {
            holder.txtAddress.setText(arrList.get(position).getSocietyName() + " " + arrList.get(position).getStreet2() + " " + arrList.get(position).getStreet3() + " " + arrList.get(position).getHouseType());
        } else if (arrList.get(position).getHouseType().equalsIgnoreCase("BUNGALOW")) {
            holder.txtAddress.setText(arrList.get(position).getSocietyName() + " " + arrList.get(position).getStreet2() + " " + arrList.get(position).getStreet3() + " " + arrList.get(position).getHouseType());
        }else
        {
            holder.txtAddress.setText(arrList.get(position).getSocietyName() + " " + arrList.get(position).getStreet3() + "\n" + "Wing " + arrList.get(position).getWing() + " Flat No. :" + arrList.get(position).getHouseNumber() + " " + arrList.get(position).getStreet2());

        }

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
