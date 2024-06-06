package com.lucas.metronome_android.MyViews;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class Selecter extends View {

    private static String TAG = "Selector";
    private Paint paint;
    private int disappearDistance;
    private String[] optionArray = {"60 步/min", "65 步/min", "70 步/min", "75 步/min", "80 步/min", "85 步/min", "90 步/min", "95 步/min", "100 步/min", "105 步/min", "110 步/min", "115 步/min", "120 步/min", "125 步/min", "130 步/min", "135 步/min", "140 步/min", "145 步/min", "150 步/min", "155 步/min", "160 步/min", "165 步/min", "170 步/min", "175 步/min", "180 步/min", "185 步/min", "190 步/min", "195 步/min", "200 步/min", "205 步/min", "210 步/min", "215 步/min", "220 步/min"};
    private int[] valueArray = {60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115, 120, 125, 130, 135, 140, 145, 150, 155, 160, 165, 170, 175, 180, 185, 190, 195, 200, 205, 210, 215, 220};






    private float initialY;
    private float initialOffsetY;
    private float offsetY = 0;
    private float overallAlpha = 0;
    private int currentIndex = 0;
    private OnValueChangeListener onValueChangeListener;
    private ObjectAnimator alignAnimator = ObjectAnimator.ofFloat(this, "OffsetY", 0);
    private ObjectAnimator appearAnimator = ObjectAnimator.ofFloat(this, "OverallAlpha", 1);
    private ObjectAnimator disappearAnimator = ObjectAnimator.ofFloat(this, "OverallAlpha", 0);
    private AnimatorSet actionUpAnimator = new AnimatorSet();

    public Selecter(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(50);
        paint.setAntiAlias(true);

        // Setup animator parameters
        alignAnimator.setDuration(200);
        alignAnimator.setInterpolator(new DecelerateInterpolator());
        disappearAnimator.setStartDelay(1000);
        actionUpAnimator.play(alignAnimator).before(disappearAnimator);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int offset = (int) offsetY;
        int yVal;
        int distFactor;

        for (int i = Math.max(0, currentIndex-2); i < currentIndex; i++){
            yVal = height / 2 - 90 * (currentIndex - i) + offset + 20;
            distFactor = Math.abs(yVal - height / 2 - 20);
            if (distFactor*1.3 < 255) {
                paint.setAlpha((int) ((255-distFactor*1.3)*overallAlpha));
            } else {
                paint.setAlpha(0);
            }
            canvas.drawText(optionArray[i], width / 2, yVal, paint);
        }

        yVal = height / 2 + offset + 20;
        distFactor = Math.abs(yVal - height / 2 - 20);
        paint.setAlpha((int) (255-distFactor*1.3));
        canvas.drawText(optionArray[currentIndex], width / 2, yVal, paint);

        for (int i = currentIndex + 1; i < Math.min(valueArray.length, currentIndex+3); i++){
            yVal = height / 2 - 90 * (currentIndex - i) + offset + 20;
            distFactor = Math.abs(yVal - height / 2 - 20);
            if (distFactor*1.3 < 255) {
                paint.setAlpha((int) ((255-distFactor*1.3)*overallAlpha));
            } else {
                paint.setAlpha(0);
            }
            canvas.drawText(optionArray[i], width / 2, yVal, paint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialY = event.getY();
                initialOffsetY = this.offsetY;
                doActionDown();
                return true;
            case MotionEvent.ACTION_MOVE:
                float moveOffset = event.getY() - initialY;
                if (initialOffsetY+moveOffset > 80){
                    if (currentIndex == 0) {
                        setOffsetY(80);
                    } else {
                        currentIndex --;
                        initialY = initialY + 90;
                        setOffsetY(initialOffsetY+moveOffset - 90);
                        if (onValueChangeListener != null) {
                            onValueChangeListener.onValueChange(valueArray[currentIndex]);
                        }
                    }
                } else if (initialOffsetY+moveOffset < -80) {
                    if (currentIndex == optionArray.length-1) {
                        setOffsetY(-80);
                    } else {
                        currentIndex ++;
                        initialY = initialY - 90;
                        setOffsetY(initialOffsetY+moveOffset + 90);
                        if (onValueChangeListener != null) {
                            onValueChangeListener.onValueChange(valueArray[currentIndex]);
                        }
                    }
                } else {
                    setOffsetY(initialOffsetY+moveOffset);
                }
                return true;
            case MotionEvent.ACTION_UP:
                doActionUp();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    public void setOnValueChangeListener(OnValueChangeListener listener) {
        this.onValueChangeListener = listener;
    }

    public interface OnValueChangeListener {
        void onValueChange(int value);
    }

    private void doActionUp(){
        alignAnimator.setupStartValues();
        disappearAnimator.setupStartValues();
        actionUpAnimator.start();
    }

    private void doActionDown(){
        actionUpAnimator.cancel();
        appearAnimator.setupStartValues();
        appearAnimator.start();
    }


    private void setOffsetY(float offsetY){
        this.offsetY = offsetY;
        invalidate();
    }

    private void setOverallAlpha(float overallAlpha){
        this.overallAlpha = overallAlpha;
        invalidate();
    }

    public float getOffsetY(){
        return this.offsetY;
    }

    public float getOverallAlpha(){
        return this.overallAlpha;
    }

    public void setValues(String[] optionArray, int[] valueArray){
        if(optionArray.length == valueArray.length){
            this.valueArray = valueArray;
            this.optionArray = optionArray;
            invalidate();
        }
    }

    public void setIndexByValue(int value){
        for (int i = 0; i < valueArray.length; i++) {
            if (valueArray[i] == value) {
                this.currentIndex = i;
                invalidate();
            }
        }
    }

}
