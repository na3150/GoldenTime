package com.example.probonoapp;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class CountDownService extends Service { //응급신고 발생 후 5분 후에 보호자 응답 체크 후 , button==false이면 응급문자 신고

    //한번에 주석처리: ctrl+ shift + /
    private static final String TAG = "CountDownService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        if (intent == null) {
            return START_NOT_STICKY;
        }

        CountDownTimer countDownTimer = new CountDownTimer(10000,1000) { //5분 카운트 다운 : test로 10초
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
