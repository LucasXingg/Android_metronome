package com.lucas.metronome_android;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.media.JetPlayer;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lucas.metronome_android.MyViews.Selecter;

public class SettingActivity extends AppCompatActivity {

    private Selecter selLeft;
    private Selecter selRight;
    private ImageView grayBar;
    private LinearLayout btnLayout;
    private Button confirmBtn;
    private Button voiceBtn;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        selLeft = findViewById(R.id.setTextLeft);
        selRight = findViewById(R.id.setTextRight);
        grayBar = findViewById(R.id.grayBar);
        btnLayout = findViewById(R.id.btnLayout);
        confirmBtn = findViewById(R.id.confirmBtn);

        selRight.setValues(DataHelper.stepOptionArray, DataHelper.stepvalueArray);

        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);

        updateSettingText();

        ObjectAnimator alphaGray = ObjectAnimator.ofFloat(grayBar, "alpha", 0, 0.5f);
        ObjectAnimator moveBtn = ObjectAnimator.ofFloat(btnLayout, "translationY", 100, 0);
        ObjectAnimator alphaBtn = ObjectAnimator.ofFloat(btnLayout, "alpha", 0, 1);


        AnimatorSet btnAnmi = new AnimatorSet();
        btnAnmi.playTogether(moveBtn, alphaBtn);
        btnAnmi.setStartDelay(130);

        AnimatorSet startAnmi = new AnimatorSet();
        startAnmi.playTogether(alphaGray, btnAnmi);
        startAnmi.start();

        /**
         * Set callback
         */

        selLeft.setOnValueChangeListener(new Selecter.OnValueChangeListener() {
            @Override
            public void onValueChange(int value) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("BPM", value);
                editor.apply();
            }
        });

        selRight.setOnValueChangeListener(new Selecter.OnValueChangeListener() {
            @Override
            public void onValueChange(int value) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("step", value);
                editor.apply();
            }
        });

        confirmBtn.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ObjectAnimator alphaGrayInverse = ObjectAnimator.ofFloat(grayBar, "alpha", 0.5f, 0);
        ObjectAnimator alphaBtnInverse = ObjectAnimator.ofFloat(btnLayout, "alpha", 1, 0);
        ObjectAnimator moveBtnInverse = ObjectAnimator.ofFloat(btnLayout, "translationY", 0, 100);
        AnimatorSet backAnmi = new AnimatorSet();
        backAnmi.playTogether(alphaBtnInverse, alphaGrayInverse, moveBtnInverse);
        backAnmi.start();
    }

    private void updateSettingText(){
        selLeft.setIndexByValue(sharedPreferences.getInt("BPM", 120));
        selRight.setIndexByValue(sharedPreferences.getInt("step", 1));
    }

}