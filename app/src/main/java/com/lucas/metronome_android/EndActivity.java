package com.lucas.metronome_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity {

    private TextView timer;
    private TextView stepCounter;
    private Button stopBtn;
    private CardView orangeArea;
    private LinearLayout contentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        timer = findViewById(R.id.timer);
        stepCounter = findViewById(R.id.stepCounter);
        stopBtn = findViewById(R.id.finishBtn);
        orangeArea = findViewById(R.id.startArea);
        contentLayout = findViewById(R.id.contentLayout);

        timer.setText(getIntent().getStringExtra("time"));
        stepCounter.setText(getIntent().getStringExtra("step"));

        ObjectAnimator orangeOut = ObjectAnimator.ofFloat(orangeArea, "translationY", dpToPx(-500));
        ObjectAnimator linearOut = ObjectAnimator.ofFloat(contentLayout, "translationY", dpToPx(-500));
        ObjectAnimator btnOut = ObjectAnimator.ofFloat(stopBtn, "alpha", 0);

        AnimatorSet outAnmi = new AnimatorSet();
        outAnmi.playTogether(orangeOut, linearOut, btnOut);
        outAnmi.setDuration(300);

        outAnmi.addListener(new AnimatorSet.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(EndActivity.this, MainActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(EndActivity.this).toBundle());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        stopBtn.setOnClickListener(v -> {
            outAnmi.start();
        });

    }

    public int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}