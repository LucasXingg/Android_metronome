package com.lucas.metronome_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.lucas.metronome_android.MyViews.Selecter;

import java.util.Timer;

public class MainActivity2 extends AppCompatActivity {

    /**
     * Helper variables
     */

    private static String TAG = "MainActivity2";
    private DataHelper dataHelper;

    /**
     * Define views
     */
    private CardView settingCard;
    private ImageView gifImg;
    private CardView startArea;
    private TextView timer;
    private TextView stepCounter;
    private Button pauseBtn;
    private Button stopBtn;
    private View topSpace;
    private View settingTrigger;
    private LinearLayout btnContainer;
    private Selecter selLeft;
    private Selecter selRight;

    /**
     * Sound related init
     */
    private Runnable beatRunnable;
    private boolean isPlaying = false;
    private SoundPool soundPool;
    private int soundId;
    private Handler handler;
    private int playInterval;
    private boolean playNext = true;

    /**
     * Variable Define
     */
    private int stepCount = 0;
    private long startTime;
    private long elapsedTimeSeconds = 0;
    private Runnable timerRunnable;
    SharedPreferences sharedPreferences;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        settingCard = findViewById(R.id.settingBar);
        gifImg = findViewById(R.id.gifImg);
        startArea = findViewById(R.id.startArea);
        timer = findViewById(R.id.timer);
        stepCounter = findViewById(R.id.stepCounter);
        pauseBtn = findViewById(R.id.pauseBtn);
        stopBtn = findViewById(R.id.stopBtn);
        topSpace = findViewById(R.id.topSpace);
        btnContainer = findViewById(R.id.btnContainer);
        settingTrigger = findViewById(R.id.settingTrigger);
        selLeft = findViewById(R.id.setTextLeft);
        selRight = findViewById(R.id.setTextRight);

        selRight.setValues(DataHelper.stepOptionArray, DataHelper.stepvalueArray);

        handler = new Handler();

        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);

        updateSettingText();


        /**
         * Define animations
         */
        // Phase one
        ObjectAnimator settingSlideOut = ObjectAnimator.ofFloat(settingCard, "translationY", 0, dpToPx(100));
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(startArea, "scaleX", 1f, 2f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(startArea, "scaleY", 1f, 3f);
        ObjectAnimator marginImg = ObjectAnimator.ofFloat(gifImg, "translationX", dpToPx(90));
        ObjectAnimator alphaTimer = ObjectAnimator.ofFloat(timer, "alpha", 0, 1);
        ObjectAnimator marginTimer = ObjectAnimator.ofFloat(timer, "translationX", dpToPx(-100), dpToPx(-10));

        AnimatorSet anmiP1 = new AnimatorSet();
        anmiP1.playTogether(scaleX, scaleY, settingSlideOut, marginImg, alphaTimer, marginTimer);
//        anmiP1.setStartDelay(200);
        anmiP1.setDuration(300);
        anmiP1.addListener(new AnimatorSet.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                startArea.setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                settingCard.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        // Phase two
        // Initial and target heights
        ValueAnimator moveUp = ValueAnimator.ofFloat(1f, 0.5f);
        moveUp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ((LinearLayout.LayoutParams) topSpace.getLayoutParams()).weight = (float) animation.getAnimatedValue();
                topSpace.requestLayout();
            }
        });
        ObjectAnimator alphaBtnContainer = ObjectAnimator.ofFloat(btnContainer, "alpha", 1);
        ObjectAnimator alphaStepCounter = ObjectAnimator.ofFloat(stepCounter, "alpha", 1);

        AnimatorSet anmiP2 = new AnimatorSet();
        anmiP2.playTogether(moveUp, alphaStepCounter, alphaBtnContainer);
//        anmiP2.setStartDelay(200);
        anmiP2.setDuration(600);
        anmiP2.addListener(new AnimatorSet.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                startMetronome();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet startAnmi = new AnimatorSet();
        startAnmi.playTogether(anmiP1, anmiP2);

        /**
         * Exit view animation
         */
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int imgMoveOutDist = displayMetrics.widthPixels/2 + 50;
        ObjectAnimator outImgAnmi = ObjectAnimator.ofFloat(gifImg, "translationX", dpToPx(90), imgMoveOutDist);
        ObjectAnimator outTimerAnmi = ObjectAnimator.ofFloat(timer, "translationX", dpToPx(-10), 0);
        ObjectAnimator outBtnPause = ObjectAnimator.ofFloat(pauseBtn, "alpha", 1, 0);
        ObjectAnimator outBtnStop = ObjectAnimator.ofFloat(stopBtn, "alpha", 1, 0);
        AnimatorSet outAnmi = new AnimatorSet();
        outAnmi.playTogether(outBtnPause, outBtnStop, outImgAnmi, outTimerAnmi);
        outAnmi.setDuration(1000);

        outAnmi.addListener(new AnimatorSet.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                handler.removeCallbacks(beatRunnable);
                handler.removeCallbacks(timerRunnable);
                Intent intent = new Intent(MainActivity2.this, EndActivity.class);
                intent.putExtra("time", (String) timer.getText());
                intent.putExtra("step", (String) stepCounter.getText());
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity2.this,
                        Pair.create(startArea, "orangeArea"),
                        Pair.create(timer, "timer"),
                        Pair.create(stepCounter, "stepCounter")
                );
                startActivity(intent, options.toBundle());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });



        /**
         * Set Callback
         */
        settingTrigger.setOnClickListener(v -> {
            timer.setTranslationX(800);
            stepCounter.setTranslationX(800);

            Intent intent = new Intent(MainActivity2.this, SettingActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity2.this,
                    Pair.create(settingCard, "settingArea"),
                    Pair.create(timer, "timer"),
                    Pair.create(stepCounter, "stepCounter")
            );
            startActivity(intent, options.toBundle());
        });

        startArea.setOnClickListener(v -> {
            updateStepCounter();
            playInterval = 60000 / sharedPreferences.getInt("BPM", 120);
            // Load the GIF using Glide
            Glide.with(this).asGif().load(R.drawable.walk).into(gifImg);
            startAnmi.start();
        });

        pauseBtn.setOnClickListener(v -> {
            if (isPlaying) {
                stopMetronome();
            } else {
                startMetronome();
            }
        });

        stopBtn.setOnClickListener(v -> {
            outAnmi.start();
            if (isPlaying) {
                stopMetronome();
            } else {
                startMetronome();
            }
        });

        /**
         * Sound related Define
         */
        // Initialize SoundPool
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();

        // Load the metronome sound into SoundPool
        soundId = soundPool.load(this, R.raw.click1, 1);

        beatRunnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, playInterval);
                if (isPlaying) {
                    if (playNext){
                        soundPool.play(soundId, 1, 1, 0, 0, 1);
                    }
                    stepCount ++;
                    updateStepCounter();
                    if (stepCount % sharedPreferences.getInt("step", 1) == 0) {
                        playNext = true;
                    } else {
                        playNext = false;
                    }
                }
            }
        };

        /**
         * Timer runnable
         */
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                // Re-post this task every second
                handler.postDelayed(this, 1000);
                if (isPlaying) {
                    // Calculate the elapsed time in seconds
                    long elapsed = System.currentTimeMillis() - startTime;
                    long Seconds = elapsed / 1000 + elapsedTimeSeconds;
                    // Update the TextView
                    updateTimer(Seconds);
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(beatRunnable);
        handler.removeCallbacks(timerRunnable);
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        stepCounter.setTranslationX(0);
        // Update the username when returning from SettingsActivity
        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        updateSettingText();
    }

    private void startMetronome() {
        isPlaying = true;
        pauseBtn.setText("❚❚");
        startTime = System.currentTimeMillis();
        handler.post(beatRunnable);
        handler.post(timerRunnable);
    }

    private void stopMetronome() {
        isPlaying = false;
        elapsedTimeSeconds = (System.currentTimeMillis() - startTime) / 1000;
        pauseBtn.setText("▶");
        handler.removeCallbacks(beatRunnable);
        handler.removeCallbacks(timerRunnable);
    }

    public int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void updateStepCounter(){
        stepCounter.setText(String.valueOf(sharedPreferences.getInt("BPM", 120)+"步/分钟 | "+String.valueOf(stepCount)));
    }

    private void updateTimer(long seconds){
        long min = seconds / 60;
        long sec = seconds % 60;
        String preTime;
        String aftTime;
        if((min / 10) == 0) {
            preTime = "0"+ min;
        } else {
            preTime = String.valueOf(min);
        }
        if((sec / 10) == 0) {
            aftTime = "0"+ sec;
        } else {
            aftTime = String.valueOf(sec);
        }
        timer.setText(preTime+":"+aftTime);
    }

    private void updateSettingText(){
        selLeft.setIndexByValue(sharedPreferences.getInt("BPM", 120));
        selRight.setIndexByValue(sharedPreferences.getInt("step", 1));
    }
}