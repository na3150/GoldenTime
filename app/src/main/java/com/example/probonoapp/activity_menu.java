package com.example.probonoapp;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class activity_menu extends AppCompatActivity {

    Button buttonLogout; //로그아웃버튼
    Button buttonIntroduce; //어플리케이션 소개 버튼
    Button buttonAccount; //계정 버튼
    Button buttonToilet; //TimeSpentInToilet 버튼
    Button buttonAlarmList; ////응급상황 알림목록 버튼
    Button buttonble; ////블루투스 버튼

    private static final String TAG = "activity_menu";
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(activity_menu.this, LoginActivity.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //인텐트 플래그 설정
        startActivity(intent);  //인텐트 이동
        finish();   //현재 액티비티 종료
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button logTokenButton = findViewById(R.id.msg_token_fmt);
        logTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                    return;
                                }

                                // Get new FCM registration token
                                String token = task.getResult();

                                // Log and toast
                                String msg = getString(R.string.msg_token_fmt, token);
                                Log.d(TAG, msg);
                                Toast.makeText(activity_menu.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        buttonLogout = (Button)findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener((new View.OnClickListener() { //로그아웃 버튼을 눌렀을 때
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_menu.this,MainActivity.class); //화면 이동
                startActivity(intent);
                finish(); // 현재 액티비티 파괴
            }
        }));

        buttonIntroduce =(Button)findViewById(R.id.buttonIntroduce);
        buttonIntroduce.setOnClickListener((new View.OnClickListener() { //어플리케이션 소개 버튼을 눌렀을 때
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(activity_menu.this, activity_introduce.class); //화면 이동
                startActivity(intent2);
                finish(); // 현재 액티비티 파괴
            }
        }));

        buttonAccount = (Button)findViewById(R.id.buttonAccount);
        buttonAccount.setOnClickListener((new View.OnClickListener() { //계정관리 버튼을 눌렀을 떄
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(activity_menu.this, activity_user.class); //화면 이동
                startActivity(intent3);
                finish(); // 현재 액티비티 파괴
            }
        }));

        buttonToilet = (Button)findViewById(R.id.buttonToilet); //TimeSpentInToilet 버튼을 눌렀을 때
        buttonToilet.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(activity_menu.this, activity_timespentintoilet.class);
                startActivity(intent4);
                finish();
            }
        }));

        buttonAlarmList = (Button)findViewById(R.id.buttonEmergency); //응급상황 알림목록 버튼을 눌렀을 때
        buttonAlarmList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent5 = new Intent(activity_menu.this, activity_alarmList.class);
                startActivity(intent5);
                finish();
            }
        });

        buttonble = (Button)findViewById(R.id.buttonBle); //블루투스 버튼을 눌렀을 때
        buttonble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent6 = new Intent(activity_menu.this, DeviceScanActivity.class);
                startActivity(intent6);
                finish();
            }
        });
    }
}