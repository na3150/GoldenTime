package com.example.probonoapp;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class CountDownService extends Service {

    private static final String TAG = "CountDownService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId){
        CountDownTimer countDownTimer = new CountDownTimer(60000,1000) { //5분 카운트 다운 : test로 60초
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e(TAG,"CountDown: "+ millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                Log.e(TAG, "onFinish: ");
            }
        }.start();
        return START_STICKY;
    }
}
