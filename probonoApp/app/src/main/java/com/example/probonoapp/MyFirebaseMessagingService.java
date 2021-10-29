package com.example.probonoapp;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String emergencyTime, half_emergencyTime;
    //int eT, halfeT; //emergencyTime을 int로 변환


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //emergency_time 가져오기
        SharedPreferences sharedPreferences= getSharedPreferences("test", MODE_PRIVATE);    // test 이름의 기본모드 설정, 만약 test key값이 있다면 해당 값을 불러옴.
        emergencyTime = sharedPreferences.getString("emergency_time",""); //값 가져오기
        half_emergencyTime = sharedPreferences.getString("half_emergency_time","");

        Log.d(TAG, "emergencyTime: " + emergencyTime);
        Log.d(TAG, "halfTime: " + half_emergencyTime);

        super.onMessageReceived(remoteMessage);

        //화면 깨우기
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE );
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock = pm.newWakeLock( PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG" );
        wakeLock.acquire(3000);

        //푸시알림 전송
        makeNotification(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

    private void makeNotification(RemoteMessage remoteMessage) {
        try {
            int notificationId = 1;
            Context mContext = getApplicationContext();

            SharedPreferences sharedPreferences= getSharedPreferences("test", MODE_PRIVATE);

            boolean getfall_emergency = sharedPreferences.getBoolean("fall_emergency",true);
            boolean pushbutton_emergency = sharedPreferences.getBoolean("button_emergency",true);
            boolean time50_emergency = sharedPreferences.getBoolean("50%time_emergency",true);
            boolean time100_emergency = sharedPreferences.getBoolean("100%time_emergency",true);

            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("body");
            String click_action = remoteMessage.getData().get("click_action");
            String limitTime = remoteMessage.getData().get("time"); //data로 오는시간
            String contentText = "응급상황발생";


            Intent intent = new Intent(this,Push_emergency_button.class); //defualt: 응급 버튼

            if (limitTime.equals(half_emergencyTime)) //50%일 때
            {
                intent = new Intent(this, activity_spentTimeInToiletMoreThanHalf_50Percent.class);
                contentText = "경고: 화장실 이용시간 "+ half_emergencyTime + "분 초과";
                //50% 상황을 sharedPreferences에 저장·수정
                sharedPreferences.edit().putBoolean("50%time_emergency",true).apply(); //화장실 응급시간 50% 초과 -> true로 변경
            }
            else if (limitTime.equals(emergencyTime)){ //100%일 때
                intent = new Intent(this, SpentTimeInToiletMoreThan_100Percent.class);
                sharedPreferences.edit().putBoolean("100%time_emergency",true).apply(); //100% 초과 -> true로 변경
            }
            else if (click_action.equals("Emergency_getFall")){ //낙상사고 일때
                intent = new Intent(this, Emergency_getFall.class);
                sharedPreferences.edit().putBoolean("fall_emergency",true).apply(); //낙상 응급상황 -> true로 변경
            }
            else{ //defualt인 응급상황 버튼을 누른경우
                sharedPreferences.edit().putBoolean("button_emergency",true).apply(); //응급호출 버튼 응급상황 -> true로 변경
            }


            //Log로 값 확인
            Log.d(TAG, "fall_emergency = "+ getfall_emergency);
            Log.d(TAG, "button_emergency = "+ pushbutton_emergency);
            Log.d(TAG, "50%time_emergency = "+ time50_emergency);
            Log.d(TAG, "100%time_emergency = "+ time100_emergency);

            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "10001");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                builder.setVibrate(new long[] {200, 100, 200});
            }

            builder.setSmallIcon(R.drawable.icon_goldentime_round)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(contentText)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(pendingIntent);


            //알림 생성: 낙상,응급호출 버튼, time 50%,100%일 때만 알림 생성
            if (limitTime.equals("0")|| limitTime.equals(half_emergencyTime) || limitTime.equals(emergencyTime))
                notificationManager.notify(notificationId, builder.build());


            //카운트 다운
            //회원가입시 입력한 limitTime과 동일할 때 혹은 낙상,응급호출 버튼일 때만 카운트 다운 실행
            if (emergencyTime.equals(limitTime) || limitTime.equals("0"))
            {
                Intent serviceIntent = new Intent(this,CountDownService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this.startForegroundService(serviceIntent);
                }
                else {
                    this.startService(serviceIntent);
                }
            }

        } catch (NullPointerException nullException) {
            Toast.makeText(getApplicationContext(), "알림에 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            Log.e("error Notify", nullException.toString());
        }

    }
}
