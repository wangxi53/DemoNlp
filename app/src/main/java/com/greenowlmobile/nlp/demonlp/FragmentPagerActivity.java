package com.greenowlmobile.nlp.demonlp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;

import com.greenowlmobile.nlp.demonlp.utilities.Constants;
import com.greenowlmobile.nlp.demonlp.utilities.Utilities;

public class FragmentPagerActivity extends FragmentActivity {
    //Declare the total pages in the adapter
    static final int NUM_ITEMS = 2;

    private MyAdapter mAdapter;
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Use this function to keep the users screen on
        Utilities.keepScreenOn(this);

        setContentView(R.layout.fragment_pager);

        mAdapter = new MyAdapter(getSupportFragmentManager());

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        //start from the last page of the fragment list
        mPager.setCurrentItem(NUM_ITEMS - 2);

    }

    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    Fragment mapFragment = new MapFragement();
                    return mapFragment;

                case 1:
                    Fragment buttonSelectionFragment = new ButtonSelectionFragment();
                    return buttonSelectionFragment;

                default:
                    return null;
            }
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public void onBackPressed(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Shut Down");

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you want to shut down NLP Demo App?")
                .setCancelable(false)
                .setPositiveButton("Shut Down", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                setResult(RESULT_OK);

                                Constants.noInternetFlag = true;
                                Constants.callToServerFlag = true;
                                Constants.mps3PlayedMarker.clear();

                                finish();

                            }
                        }

                )
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        }

                );

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
