package com.example.rainblocks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.SurfaceView;

import androidx.core.content.ContextCompat;

public class GameView extends SurfaceView implements Runnable {

    int screenX, screenY, score = 0;
    int lifes = 200;
    public static float screenRatioX, screenRatioY;
    private boolean isPlaying;
    private Block[] blocks;
    private Catcher catcher;
    private Paint paint;
    private Thread thread;
    BackGround background;
    private  GameActivity activity;
    private SharedPreferences prefs;
    private boolean isGameOver = false;

    Sensor gyroscopeSensor;
    SensorManager sensorManager;


    public GameView(Context ctx, GameActivity activity, final int screenX, int screenY){
        super(activity);

        this.activity = activity;

        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);

        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);

        background = new BackGround(screenX, screenY, getResources());

        catcher = new Catcher(getResources());
        blocks = new Block[20];

        for(int i = 0; i < blocks.length; i++){
            Block block = new Block(getResources());
            blocks[i] = block;
        }

        catcher.x = (screenX / 2) - catcher.width / 2;
        catcher.y = screenY - catcher.height * 2;
        
        SensorManager sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        SensorEventListener gyroscopeSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if(sensorEvent.values[1] > 0.1f){
                    catcher.x += sensorEvent.values[1] * 50f;
                    if(catcher.x > screenX - catcher.width){
                        catcher.x = ((screenX / 2) - (catcher.width / 2));
                        //catcher.x = screenX;
                    }
                }else if(sensorEvent.values[1] < -0.1f){
                    catcher.x += sensorEvent.values[1] * 50f;
                    if(catcher.x < 0){
                        //catcher.x = 0;
                        catcher.x = ((screenX / 2) - (catcher.width / 2));
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        sensorManager.registerListener(gyroscopeSensorListener, gyroscopeSensor, sensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void run() {
        while(isPlaying){
            update();
            draw();
            sleep();
        }
    }
    public void update(){
        int x = 0;
        int y = 20;
        for(Block block : blocks){
            block.x = x;
            x+= 55;
            if(x < 550){
                y -= 10;
                block.y += y;
                y *= 1.25;
                y += 10;
            }
            else{
                y -= 10;
                block.y += y;
                y /= 1.25;
                y += 10;
            }

            if(block.y > screenY){
                block.y -= screenY;
                lifes--;
            }
            if(Rect.intersects(block.getCollisionShape(), catcher.getCollisionShape())){
                block.y -= screenY;
                score += 50;
            }
        }

        if(lifes < 0){
            isGameOver = true;
        }
    }
    private void draw(){
        if(getHolder().getSurface().isValid()){
            Canvas canvas = getHolder().lockCanvas();

            canvas.drawBitmap(background.backGround, background.x, background.y, paint);

            canvas.drawText(score + "", (screenX / 2f) - (paint.measureText(Integer.toString(score)) / 2f), 164, paint);
            for(Block block : blocks){
                canvas.drawBitmap(block.block,block.x,block.y,paint);
            }

            if(isGameOver){
                isPlaying = false;
                getHolder().unlockCanvasAndPost(canvas);
                SaveIfHighScore();
                waitBeforeExiting();
                return;

            }
            canvas.drawBitmap(catcher.block, catcher.x, catcher.y, paint);

            getHolder().unlockCanvasAndPost(canvas);
        }
    }
    private void SaveIfHighScore() {
        if(prefs.getInt("highscore", 0) < score){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highscore", score);
            editor.apply();
        }
    }
    public void sleep(){
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void resume(){
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }
    public void pause(){
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void waitBeforeExiting() {
        try {
            Thread.sleep(3000);
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(event.getX() < screenX / 2){
                    catcher.x += 100;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(event.getX() > screenX / 2){
                    catcher.x -= 100;
                }
                break;
        }

        return true;
    }*/
}
