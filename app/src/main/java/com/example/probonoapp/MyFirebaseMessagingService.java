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

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    String emergencyTime, half_emergencyTime;
    int eT, halfeT; //emergencyTime을 int로 변환


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("UserAccount");
        userID = user != null ? user.getUid() : null;


        //사용자 지정 응급 시간 가져오기기
        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                emergencyTime = dataSnapshot.child("emergency_time").getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

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
            String limitTime = remoteMessage.getData().get("time"); //data로 오는시간
            String contentText = "응급상황발생";

            if (!limitTime.equals("0")) {
                Log.d(TAG, "emergencyTime: " + emergencyTime);
                halfeT = Integer.parseInt(emergencyTime) / 2; //50% 시간
                Log.d(TAG, "halfTime: " + halfeT);
                half_emergencyTime = Integer.toString(halfeT); //50%시간 문자열 변환
            }

            Intent intent = new Intent(this,Push_emergency_button.class); //defualt: 응급 버튼

            if (limitTime.equals(half_emergencyTime)) //50%일 때
            {
                intent = new Intent(this, activity_spentTimeInToiletMoreThanHalf_50Percent.class); //푸시알림 눌렀을 때 이동하는 페이지
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                contentText = "경고: 화장실 이용시간 "+ Integer.toString(halfeT)+ "분 초과";
            }
            else if (limitTime.equals(emergencyTime)){ //100%일 때
                intent = new Intent(this, SpentTimeInToiletMoreThan_100Percent.class); //푸시알림 눌렀을 때 이동하는 페이지
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
            }
            else if (click_action.equals("Emergency_getFall")){ //낙상사고 일때
                intent = new Intent(this, Emergency_getFall.class); //푸시알림 눌렀을 때 이동하는 페이지
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
            }

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
