package com.example.probonoapp;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
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
        makeNotification(remoteMessage); //포그라운드일 때

        Log.d(TAG, "From: " + remoteMessage.getFrom());


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }


        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }


    private void makeNotification(RemoteMessage remoteMessage) { //포그라운드 처리
        try {
            int notificationId = 1;
            Context mContext = getApplicationContext();

            Intent intent = new Intent(this, Push_emergency_button.class); //푸시알림 눌렀을 때 이동하는 페이지
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            String title = "[안전바 응급호출 도우미]";
            String message = "노약자의 응급상황이 감지되었습니다. 신속하게 안전을 확인해주세요. 5분 이내에 응답이 없으면 119응급신고가 접수됩니다.";
            String topic = remoteMessage.getFrom();

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "10001");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                builder.setVibrate(new long[] {200, 100, 200});
            }

            builder.setSmallIcon(R.drawable.logoclock)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setContentTitle(title)
                    .setContentText("응급상황발생: 응급호출 버튼")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(pendingIntent);
            notificationManager.notify(notificationId, builder.build()); //알림 생성

        } catch (NullPointerException nullException) {
            Toast.makeText(getApplicationContext(), "알림에 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            Log.e("error Notify", nullException.toString());
        }
    }

    }


