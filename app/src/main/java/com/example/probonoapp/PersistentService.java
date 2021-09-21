package com.example.probonoapp;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.probonoapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class PersistentService extends Service {

    private static final int MILLISINFUTURE = 1000 * 1000;
    private static final int COUNT_DOWN_INTERVAL = 1000;

    private CountDownTimer countDownTimer;

    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증처리
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스 (서버연동)

    //사용자 정보 가져오기위한 참조
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID, emergency;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        unregisterRestartAlarm();
        super.onCreate();

        initData();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { //백그라운드에서 실행되는 동작들이 들어가는 곳

        startForeground(1, new Notification());

        /**
         * startForeground 를 사용하면 notification 을 보여주어야 하는데 없애기 위한 코드
         */
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle("")
                    .setContentText("")
                    .build();

        } else {
            notification = new Notification(0, "", System.currentTimeMillis());
            //notification.setLatestEventInfo(getApplicationContext(), "", "", null);
        }

        makeEmergencyAlarm();
        nm.notify(startId, notification);
        nm.cancel(startId);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() { //서비스가 종료될 때 실행되는 함수가 들어가는 곳
        super.onDestroy();

        Log.i("PersistentService", "onDestroy");
        countDownTimer.cancel();

        /**
         * 서비스 종료 시 알람 등록을 통해 서비스 재 실행
         */
        registerRestartAlarm();
    }

    /**
     * 데이터 초기화
     */
    private void initData() {


        countDownTimer();
        countDownTimer.start();
    }

    public void countDownTimer() {

        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {

                Log.i("PersistentService", "onTick");
            }

            public void onFinish() {

                Log.i("PersistentService", "onFinish");
            }
        };
    }
    /**
     * 알람 매니져에 서비스 등록
     */
    private void registerRestartAlarm() {

        Log.i("000 PersistentService", "registerRestartAlarm");
        Intent intent = new Intent(PersistentService.this, RestartService.class);
        intent.setAction("ACTION.RESTART.PersistentService");
        PendingIntent sender = PendingIntent.getBroadcast(PersistentService.this, 0, intent, 0);

        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 1 * 1000;

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        /**
         * 알람 등록
         */
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 1 * 1000, sender);

    }

    /**
     * 알람 매니져에 서비스 해제
     */
    private void unregisterRestartAlarm() {

        Log.i("000 PersistentService", "unregisterRestartAlarm");

        Intent intent = new Intent(PersistentService.this, RestartService.class);
        intent.setAction("ACTION.RESTART.PersistentService");
        PendingIntent sender = PendingIntent.getBroadcast(PersistentService.this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        /**
         * 알람 취소
         */
        alarmManager.cancel(sender);


    }

    private void makeEmergencyAlarm(){
        mFirebaseAuth = FirebaseAuth.getInstance(); //Auth
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(); //데이터베이스  //앱 이름을 보통 사용

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("UserAccount");
        userID = user!= null? user.getUid() : null;


        //노약자 성명(사용자 정보)가져오기 위한 snapshot
        reference.child(userID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                emergency = dataSnapshot.child("emergency").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        if (emergency == "1"){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

            builder.setSmallIcon(R.drawable.logoclock);
            builder.setContentTitle("[안전바 응급호출 도우미]");
            builder.setContentText( "TimeSpentInToilet");
            //30분 이상 화장실에서 나오지 않았을 때
            builder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText("노약자가 30분 넘게 화장실에서 나오지 않고 있습니다. \n안전을 확인해주세요."));
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
            }
            // 정의해야하는 각 알림의 고유한 int값
            notificationManager.notify(2, builder.build());
        }
        }
    }