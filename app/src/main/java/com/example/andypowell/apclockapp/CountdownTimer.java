package com.example.andypowell.apclockapp;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class CountdownTimer {

    private long startTime;
    private long endTime;
    private TextView timerTextView;
    private CountDownTimer countDownTimer;

    public CountdownTimer(long startTime, long endTime, TextView timerTextView) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.timerTextView = timerTextView;
        start();
    }

    public void start() {
        countDownTimer = new CountDownTimer(endTime - startTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                long hours = (millisUntilFinished - (days * 24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
                long minutes = (millisUntilFinished - (days * 24 * 60 * 60 * 1000) - (hours * 60 * 60 * 1000)) / (60 * 1000);
                long seconds = (millisUntilFinished - (days * 24 * 60 * 60 * 1000) - (hours * 60 * 60 * 1000) - (minutes * 60 * 1000)) / 1000;
                timerTextView.setText(String.format("%03dd:%02dh:%02dm:%02d", days, hours, minutes, seconds));
            }

            @Override
            public void onFinish() {
                timerTextView.setText("0:00");
            }
        };
        countDownTimer.start();
    }

    public void cancel() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}