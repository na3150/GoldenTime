package com.example.probonoapp;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
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



            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("body");
            String click_action = remoteMessage.getData().get("click_action");
            String contentText = "응급상황발생";

            Intent intent = new Intent(this,Push_emergency_button.class); //defualt: 응급 버튼

            if (click_action.equals("activity_spentTimeInToiletMoreThan30"))
            {
                intent = new Intent(this, activity_spentTimeInToiletMoreThan30.class); //푸시알림 눌렀을 때 이동하는 페이지
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                contentText = "경고: 화장실 이용시간 30분 초과";
            }
            else if (click_action.equals("SpentTimeInToiletMoreThan60")){
                intent = new Intent(this, SpentTimeInToiletMoreThan60.class); //푸시알림 눌렀을 때 이동하는 페이지
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
            }

            //else if (click_action.equals(""))
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
            //특정 activity로 이동
            PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(pendingIntent);

           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //버전 체크
                notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
            }*/
            notificationManager.notify(notificationId, builder.build()); //알림 생성

        } catch (NullPointerException nullException) {
            Toast.makeText(getApplicationContext(), "알림에 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            Log.e("error Notify", nullException.toString());
        }

        //title,body등으로 30분, 60분 구분
        //카운트 다운
       Intent serviceIntent = new Intent(this,CountDownService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(serviceIntent);
        }
        else {
            this.startService(serviceIntent);
        }

        //startService(new Intent(getApplication(),CountDownService.class)); //알림 전송 후 5분 카운트 다운
        //다른 activity의 버튼 값 가져오기.... 아 버튼 아이디를 다 통일해??

    }
}


