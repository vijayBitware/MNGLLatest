package com.mngl.view.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mngl.R;
import com.mngl.utils.AlertClass;
import com.mngl.utils.Constant;
import com.mngl.utils.FontChangeCrawler;
import com.mngl.utils.SharedPref;
import com.mngl.view.fragment.CustomerDetailsFragment;
import com.mngl.view.fragment.FragmentDrawer;
import com.mngl.view.fragment.FragmentHistory;
import com.mngl.view.fragment.FragmentHome;
import com.mngl.view.fragment.FragmentMonthlyRecord;
import com.mngl.view.fragment.FragmentProfile;
import com.mngl.view.fragment.FragmentRevisitingCustomers;
import com.mngl.view.fragment.UploadDataFragment;

import java.util.HashMap;
import java.util.Stack;

import butterknife.ButterKnife;

import static com.mngl.R.id.drawer_layout;
import static com.mngl.R.id.fragment_navigation_drawer;

/**
 * Created by bitware on 15/2/17.
 */

public class DrawerActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, View.OnClickListener {

    Toolbar toolbar;
    private FragmentDrawer drawerFragment;
    ImageView imgFilter, imgSearch, imgNotification;
    Bundle bundleFrag = null;
    public static HashMap<String, Stack<Fragment>> mStacks;
    Typeface face, face1;
    // LinearLayout ll_filter;
    FontChangeCrawler fontChanger;
    TextView txtTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        ButterKnife.bind(this);
        initializeView();

        fontChanger = new FontChangeCrawler(getAssets());
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        pushFragments(new FragmentHome(), false, true, null, "");

    }

    private void initializeView() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(fragment_navigation_drawer);
        drawerFragment.setUp(fragment_navigation_drawer, (DrawerLayout) findViewById(drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);

        // toolbarTitle = (ImageView) findViewById(R.id.txtToolbarTitle);
        mStacks = new HashMap<String, Stack<Fragment>>();
        mStacks.put("MNGL", new Stack<Fragment>());

        imgNotification = findViewById(R.id.imgNotification);
        imgSearch = findViewById(R.id.imgSearch);
        txtTitle = findViewById(R.id.txtTitle);

        imgSearch.setOnClickListener(this);
        imgNotification.setOnClickListener(this);

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

       displayView(position);
    }

    private void displayView(int position) {
        switch (position) {
            case 0:
                txtTitle.setText("Home");
                pushFragments(new FragmentHome(), false, true, null, "");
                break;
            case 1:
                txtTitle.setText("Profile");
                pushFragments(new FragmentProfile(), false, true, null, "");
                break;
            case 2:
                txtTitle.setText("revisiting_customers");
                pushFragments(new FragmentRevisitingCustomers(), false, true, null, "");
                break;
            case 3:
                txtTitle.setText("History");
                pushFragments(new FragmentHistory(), false, true, null, "");
                break;
            case 4:
                txtTitle.setText("Ofline");
                pushFragments(new UploadDataFragment(), false, true, null, "");
                break;
            case 5:
                txtTitle.setText("Monthly record");
                pushFragments(new FragmentMonthlyRecord(), false, true, null, "");
                break;
            case 6:
                showLogoutDialog();
                break;
        }
    }

    @SuppressLint("ResourceAsColor")
    private void showLogoutDialog() {
        face = Typeface.createFromAsset(getAssets(), "lato_regular.ttf");
        face1 = Typeface.createFromAsset(getAssets(), "lato_bold.ttf");
        final Dialog dialog = new Dialog(DrawerActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        dialog.setContentView(R.layout.popup_exit);

        dialog.setCancelable(false);

        TextView txtTitle = (TextView) dialog.findViewById(R.id.txt_message);
        TextView txtMsg = (TextView) dialog.findViewById(R.id.txt_message);
        TextView txtUpdate = (TextView) dialog.findViewById(R.id.btn_yes);
        TextView txtCancel = (TextView) dialog.findViewById(R.id.btn_no);

        txtMsg.setText("Are you sure you want to logout?");
        txtMsg.setTypeface(face);
        txtUpdate.setTypeface(face1);
        txtCancel.setTypeface(face1);

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // if button is clicked, close the custom dialog
        txtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SharedPref.writeString(SharedPref.LOG_IN, "no");
                startActivity(new Intent(DrawerActivity.this, ActivityLogin.class));


            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {

        if (mStacks.get("MNGL").size() == 1) {
            AlertClass.exitDialog(DrawerActivity.this, getResources().getString(R.string.popup_txt));
        } else {
            popFragments();
        }

    }

    public void pushFragments(Fragment fragment, boolean shouldAnimate, boolean shouldAdd, Bundle bundle, String title) {

        bundleFrag = bundle;
        if (shouldAdd)
            mStacks.get("MNGL").push(fragment);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        fragment.setArguments(bundleFrag);
        if (shouldAnimate)
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        ft.replace(R.id.container_body, fragment);

        ft.commit();

        if (title != null) {
            if (!title.equalsIgnoreCase(""))
                getSupportActionBar().setTitle(title);
        }

    }

    /*
     *    Select the second last fragment in current tab's stack..
	 *    which will be shown after the fragment transaction given below
	 */
    public void popFragments() {
        Fragment fragment = mStacks.get("MNGL").elementAt(mStacks.get("MNGL").size() - 2);
        /*pop current fragment from stack.. */
        mStacks.get("MNGL").pop();

		/* We have the target fragment in hand.. Just show it.. Show a standard navigation animation*/
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        ft.replace(R.id.container_body, fragment);
        ft.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgNotification:
                Constant.isFilter = false;
                Toast.makeText(DrawerActivity.this, "User will see his notifications", Toast.LENGTH_SHORT).show();
                break;
            case R.id.imgSearch:
                Constant.isFilter = false;
                Toast.makeText(DrawerActivity.this, "User can search from here", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
