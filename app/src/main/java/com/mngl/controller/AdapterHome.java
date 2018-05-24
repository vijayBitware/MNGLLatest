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
import com.mngl.model.home.Datum;
import com.mngl.view.activity.DrawerActivity;
import com.mngl.view.fragment.FragmentStreetList;

import java.util.List;

/**
 * Created by bitware on 27/4/18.
 */

public class AdapterHome extends RecyclerView.Adapter<AdapterHome.MyViewHolder> {

    DrawerActivity activity;
    Typeface face;
    List<Datum> arrList;
    String flag;

    public AdapterHome(DrawerActivity activity, List<Datum> arrList, String flag) {
        this.activity = activity;
        this.arrList = arrList;
        this.flag = flag;
        face = Typeface.createFromAsset(activity.getAssets(), "lato_regular.ttf");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (flag.equalsIgnoreCase("revisit")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_revisiting_customers, parent, false);
        } else if (flag.equalsIgnoreCase("home")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_hom, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.txtName.setText(arrList.get(position).getSocietyName());
        holder.txtAddress.setText(arrList.get(position).getLocation());
        if (flag.equalsIgnoreCase("revisit"))
            holder.txtMobileNo.setVisibility(View.GONE);
        holder.ll_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("society_name", arrList.get(position).getSocietyName());
                if (flag.equalsIgnoreCase("home"))
                    bundle.putString("revisit", "false");
                else
                    bundle.putString("revisit", "true");
                activity.pushFragments(new FragmentStreetList(), true, true, bundle, "");
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtAddress, txtMobileNo;
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
