package com.example.andypowell.apclockapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GeneralHelpers {
    public void hide(TextView tv){
        tv.setVisibility(View.GONE);
    }
    public void show(TextView tv){
        tv.setVisibility(View.VISIBLE);
    }
    public void hide(ImageView iv){
        iv.setVisibility(View.GONE);
    }
    public void show(ImageView iv){
        iv.setVisibility(View.VISIBLE);
    }

}
