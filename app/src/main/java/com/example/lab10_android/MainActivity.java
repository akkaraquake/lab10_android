package com.example.lab10_android;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Runnable {
    TextView pressStartLabel;
    ProgressBar progressBar;
    Button startButton;
    boolean isFirstClick = true;
    TextView resultLabel;
    RatingBar ratingBar;
    ImageButton likeButton;
    ValueAnimator animator;
    Chronometer chronometer;
    float spentTime;
    Thread background;
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable updateTime = new Runnable() {
        @Override
        public void run() {
            spentTime = (float) (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;
            String result = "Result: " + spentTime + "s";
            resultLabel.setText(result);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pressStartLabel = (TextView) findViewById(R.id.press_start_label);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        startButton = (Button) findViewById(R.id.start_button);
        resultLabel = (TextView) findViewById(R.id.result_label);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        likeButton = (ImageButton) findViewById(R.id.like_button);

        startButton.setOnClickListener(this);
        likeButton.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        int btnId = v.getId();
        if (btnId == R.id.start_button) {
            if (startButton.getText().equals("START!")) {

                new CountDownTimer(2999, 1000) {

                    public void onTick(long millisUntilFinished) {
                        long seconds = millisUntilFinished / 1000 + 1;
                        // Обновление UI с текущим значением обратного отсчета
                        pressStartLabel.setText(String.valueOf(seconds));
                        startButton.setEnabled(false);
                    }

                    public void onFinish() {
                        // Действия, которые нужно выполнить по завершении обратного отсчета
                        pressStartLabel.setText("GO!");
                        startButton.setText("CLICK!");
                        startButton.setBackgroundColor(getResources().getColor(R.color.green));
                        startButton.setEnabled(true);
                    }
                }.start();


            }
            else if (startButton.getText() == "CLICK!") {
                System.out.println("click");
                if (isFirstClick) {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    background = new Thread(this);
                    isFirstClick = false;
                }
                run();

                progressBar.setProgress(progressBar.getProgress() + 10);

                int startValue = progressBar.getProgress(); // Начальное значение ProgressBar
                int endValue = 0; // Конечное значение ProgressBar
                animator = ValueAnimator.ofInt(startValue, endValue);
                animator.setDuration(850); // Продолжительность анимации в миллисекундах (1 секунда)

                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int animatedValue = (int) animation.getAnimatedValue();
                        progressBar.setProgress(animatedValue);
                    }
                });
                animator.start();

                if (progressBar.getProgress() == 100) {
                    chronometer.stop();
                    startButton.setText("START!");
                    startButton.setBackgroundColor(getResources().getColor(R.color.gray));
                    startButton.setEnabled(false);
                    pressStartLabel.setText("DONE!");
                    resultLabel.setVisibility(View.VISIBLE);
                    ratingBar.setVisibility(View.VISIBLE);
                    likeButton.setVisibility(View.VISIBLE);

                    ratingBar.setRating(8 - spentTime);

                }

            }
        }
        else if (btnId == R.id.like_button) {
            chronometer.setBase(SystemClock.elapsedRealtime());
            isFirstClick = true;
            startButton.setEnabled(true);
            pressStartLabel.setText("PRESS START!");
            resultLabel.setVisibility(View.INVISIBLE);
            ratingBar.setVisibility(View.INVISIBLE);
            likeButton.setVisibility(View.INVISIBLE);


        }
    }

    @Override
    public void run() {
        handler.post(updateTime);
    }
}