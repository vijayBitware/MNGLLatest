package com.mngl.controller;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mngl.R;
import com.mngl.model.streetlist.Datum;
import com.mngl.view.activity.DrawerActivity;
import com.mngl.view.fragment.FragmentCustomerList;

import com.mngl.view.fragment.FragmentWings;

import java.util.List;

/**
 * Created by bitware on 11/5/18.
 */

public class AdapterStreet extends RecyclerView.Adapter<AdapterStreet.MyViewHolder>  {

    DrawerActivity activity;
    Typeface face;
    List<Datum> arrList ;
    String flag,revisit;

    public AdapterStreet(DrawerActivity activity, List<Datum> arrList,String flag,String revisit) {
        this.activity = activity;
        this.arrList = arrList;
        this.flag =flag;
        this.revisit = revisit;
        face = Typeface.createFromAsset(activity.getAssets(),"lato_regular.ttf");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_hom, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (flag.equalsIgnoreCase("street"))
            holder.txtName.setText(arrList.get(position).getStreet3());
        else
            holder.txtName.setText("Wing "+arrList.get(position).getWing());

        holder.ll_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("street3",arrList.get(position).getStreet3());
                bundle.putString("society_name",arrList.get(position).getStreet());
                bundle.putString("wing",arrList.get(position).getWing());
                bundle.putString("revisit",revisit);
                if (flag.equalsIgnoreCase("street"))
                    activity.pushFragments(new FragmentWings(),true,true,bundle,"");
                else
                    activity.pushFragments(new FragmentCustomerList(),true,true,bundle,"");
            }
        });
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
            txtMobileNo.setTypeface(face);
        }
    }
}
