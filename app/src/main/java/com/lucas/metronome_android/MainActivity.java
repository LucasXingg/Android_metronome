package com.lucas.metronome_android;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.lucas.metronome_android.MyViews.Selecter;

import java.security.PrivateKey;

public class MainActivity extends AppCompatActivity {

    private CardView orangeArea;
    private CardView settingBar;
    private TextView title;
    private ImageView gifImg;
    private Selecter selLeft;
    private Selecter selRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        orangeArea = findViewById(R.id.startArea);
        title = findViewById(R.id.title);
        settingBar = findViewById(R.id.settingBar);
        gifImg = findViewById(R.id.gifImg);
        selLeft = findViewById(R.id.setTextLeft);
        selRight = findViewById(R.id.setTextRight);

        selRight.setValues(DataHelper.stepOptionArray, DataHelper.stepvalueArray);

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        orangeArea.setTranslationX(displayMetrics.widthPixels);
        gifImg.setTranslationX(displayMetrics.widthPixels);

        ObjectAnimator titleAnmi = ObjectAnimator.ofFloat(title, "alpha", 1f);
        titleAnmi.setDuration(600);

        ObjectAnimator settingAnmi = ObjectAnimator.ofFloat(settingBar, "translationY", 0);
        ObjectAnimator orangeAnmi = ObjectAnimator.ofFloat(orangeArea, "translationX", 0);
        ObjectAnimator imgAnmi = ObjectAnimator.ofFloat(gifImg, "translationX", 0);

        AnimatorSet secAnmi = new AnimatorSet();
        secAnmi.playTogether(settingAnmi, orangeAnmi, imgAnmi);
        secAnmi.setDuration(300);

        AnimatorSet Anmi = new AnimatorSet();
        Anmi.playTogether(titleAnmi, secAnmi);

        Anmi.start();

        Anmi.addListener(new AnimatorSet.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        /**
         * Init preference
         */
        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);

        // Check if first time open app
        if (!sharedPreferences.contains("firstOpen")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstOpen", true);
            //Set default setting
            editor.putInt("BPM", 120);
            editor.putInt("step", 1);
            editor.apply();
        }

        updateSettingText();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void updateSettingText(){
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        selLeft.setIndexByValue(sharedPreferences.getInt("BPM", 120));
        selRight.setIndexByValue(sharedPreferences.getInt("step", 1));
    }
}
