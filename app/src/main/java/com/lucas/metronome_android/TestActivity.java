package com.lucas.metronome_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;

import com.lucas.metronome_android.MyViews.Selecter;

public class TestActivity extends AppCompatActivity {

    private Selecter myWhaleView;
    private int num;

    private static String TAG = "TestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        myWhaleView = findViewById(R.id.myWhaleView1);

        myWhaleView.setIndexByValue(80);

        myWhaleView.setOnValueChangeListener(new Selecter.OnValueChangeListener() {
            @Override
            public void onValueChange(int bpm) {
                Log.i(TAG, "onValueChange: "+bpm);
            }
        });


    }
}