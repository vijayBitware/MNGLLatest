package com.mngl.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.mngl.R;
import com.mngl.view.activity.ActivityLogin;

/**
 * This class for display error alert.
 *
 * @author (Arun Chougule)
 */

public class AlertClass {

    static Typeface face, face1;

    public static void exitDialog(final Context context, String message) {
        // TODO Auto-generated method stub

        face = Typeface.createFromAsset(context.getAssets(), "lato_regular.ttf");
        face1 = Typeface.createFromAsset(context.getAssets(), "lato_bold.ttf");
        String text = "";
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        dialog.setContentView(R.layout.popup_exit);

        dialog.setCancelable(false);

        TextView txtTitle = (TextView) dialog.findViewById(R.id.txt_message);
        TextView txtMsg = (TextView) dialog.findViewById(R.id.txt_message);
        TextView txtUpdate = (TextView) dialog.findViewById(R.id.btn_yes);
        TextView txtCancel = (TextView) dialog.findViewById(R.id.btn_no);

        txtMsg.setText(message);
        txtMsg.setTypeface(face);
        txtUpdate.setTypeface(face1);
        txtCancel.setTypeface(face1);

        txtCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // if button is clicked, close the custom dialog
        txtUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(homeIntent);

            }
        });
        dialog.show();
    }

    public static void sessionExpiredDialog(final Context context, String msg) {

        face = Typeface.createFromAsset(context.getAssets(), "lato_regular.ttf");
        face1 = Typeface.createFromAsset(context.getAssets(), "lato_bold.ttf");
        String text = "";
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        dialog.setContentView(R.layout.popup_exit);

        dialog.setCancelable(false);

        TextView txtTitle = (TextView) dialog.findViewById(R.id.txt_message);
        TextView txtMsg = (TextView) dialog.findViewById(R.id.txt_message);
        TextView txtUpdate = (TextView) dialog.findViewById(R.id.btn_yes);
        TextView txtCancel = (TextView) dialog.findViewById(R.id.btn_no);

        txtMsg.setText(msg);
        txtMsg.setTypeface(face);
        txtUpdate.setTypeface(face1);
        txtCancel.setTypeface(face1);

        txtCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // if button is clicked, close the custom dialog
        txtUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent i = new Intent(context, ActivityLogin.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.getContext().startActivity(i);

            }
        });
        dialog.show();
    }
}
