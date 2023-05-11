package com.example.andypowell.apclockapp;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.RequiresPermission;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import com.example.andypowell.apclockapp.JsonHelpers;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private Typeface typefaceOpenSans;
    private Typeface typefaceMercury;
    private Typeface typefaceMonoMed;
    private Typeface typefaceAcademy;
    private TextClock textClock;
    private TextView textView;
    private TextView textSadTourney;
    private TextView textViewLeaderboard;
    private ImageView sadJordan;
    private TextView textViewCountdownLabel;
    private TextView timerTextView;
    private ImageView imageViewLogo;
    private View root;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private JsonHelpers jsonHelpers;
    private GeneralHelpers helpers;
    private CountdownTimer countdownTimer;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        timerTextView = (TextView)findViewById((R.id.countdown));
        root = findViewById(R.id.flo);
        jsonHelpers = new JsonHelpers();
        helpers = new GeneralHelpers();


        typefaceOpenSans = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Condensed-Regular.ttf");
        typefaceMercury = Typeface.createFromAsset(getAssets(), "fonts/Houston Regular.otf");
        typefaceAcademy = Typeface.createFromAsset(getAssets(), "fonts/Academy.ttf");
        typefaceMonoMed = Typeface.createFromAsset(getAssets(),"fonts/AlmaMono-Regular.ttf");
        textClock = (TextClock)findViewById(R.id.textClock);
        textView =  (TextView)findViewById((R.id.fullscreen_content));
        textSadTourney = (TextView)findViewById(R.id.sadTourney);
        textViewLeaderboard = (TextView)findViewById(R.id.fullscreen_content_leaderboard);
        sadJordan = (ImageView)findViewById(R.id.SJ);
        imageViewLogo = (ImageView)findViewById((R.id.ivLogo));
        textViewCountdownLabel =  (TextView)findViewById((R.id.countdownDaysLabel));

        final Handler handler = new Handler();

        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        updateView();
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask,500,3600000); //run every hour



        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
                //updateView();
            }
        });
        textClock.setOnClickListener(new TextClock.OnClickListener() {
            public void onClick(View v){

                updateView();

            }
        });

    }
    private void hideClock(){
        if(textClock.getVisibility()==View.GONE)
            textClock.setVisibility(View.VISIBLE);
        else if(textClock.getVisibility()==View.VISIBLE)
            textClock.setVisibility(View.GONE);
    }

    private void updateView(){
        try {
            Random r = new Random();

            Date today = new Date();
            String inputString = "07-11-2023";
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date seasonStart = null;
            try {
                seasonStart = dateFormat.parse(inputString);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            timerTextView.setTypeface(typefaceMonoMed);
            countdownTimer = new CountdownTimer(today.getTime(),seasonStart.getTime(),timerTextView);
            textSadTourney.setTypeface(typefaceOpenSans);
            textClock.setTypeface(typefaceAcademy);
            textViewCountdownLabel.setTypeface(typefaceMercury);
            textViewCountdownLabel.setText(Html.fromHtml("<b>New Season Countdown</b>"));
            textSadTourney.setTextColor(Color.parseColor("#c8c8c7"));

            InputStream is = getAssets().open("ukstats.json");
            JSONObject statJSON = jsonHelpers.changeStat(is);
            String statDescription = statJSON.getString("Description");
            String type = statJSON.getString("Type");
            if(type.equals("leaderboard")){
                textViewCountdownLabel.setTextColor(Color.parseColor("#1897d4"));
                timerTextView.setTextColor(Color.parseColor("#1897d4"));
                textViewLeaderboard.setText(Html.fromHtml(statDescription));
                textViewLeaderboard.setTypeface(typefaceOpenSans);
                textViewLeaderboard.setTextColor(Color.parseColor("#c8c8c7"));
                imageViewLogo.setImageResource(R.drawable.uk_logo);
                root.setBackgroundColor(Color.parseColor("#2c2a29"));
                helpers.hide(textView);
                helpers.hide(textSadTourney);
                helpers.hide(sadJordan);
                helpers.show(textViewLeaderboard);
            }else{
                textViewCountdownLabel.setTextColor(Color.parseColor("#a5acaf"));
                timerTextView.setTextColor(Color.parseColor("#a5acaf"));
                textView.setTypeface(typefaceOpenSans);
                imageViewLogo.setImageResource(R.drawable.uk_logo_white);
                if(statDescription.contains("{Sadface}")){
                    statDescription = statDescription.replace("{Sadface}","");
                    helpers.show(textSadTourney);
                    helpers.show(sadJordan);
                }else{
                    helpers.hide(textSadTourney);
                    helpers.hide(sadJordan);
                }
                textView.setText(Html.fromHtml(statDescription));
                root.setBackgroundColor(Color.parseColor("#0033a0"));
                textView.setTextColor(Color.parseColor("#c8c8c7"));
                helpers.hide(textViewLeaderboard);
                helpers.show(textView);

            }

        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);

    }
    @Override
    protected void onResume() {
        super.onResume();
        mVisible = true;
        toggle();

}


    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }
    private void hide() {
        // Hide UI first
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

}
