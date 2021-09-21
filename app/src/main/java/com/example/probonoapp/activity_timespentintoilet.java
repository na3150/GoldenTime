package com.example.probonoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

public class activity_timespentintoilet extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(activity_timespentintoilet.this, activity_menu.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //인텐트 플래그 설정
        startActivity(intent);  //인텐트 이동
        finish();   //현재 액티비티 종료
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_timespentintoilet);



        //화장실에 머무른 시간이 60분 초과됐을 때 (현재는 default) if(time> = 60)
        createAlarmToilet(60); //팝업 알림
        Intent intent = new Intent(activity_timespentintoilet.this, activity_notify119.class);
        startActivity(intent);
        finish();

        //화장실이 머무른 시간이 30-59분 else if(time >= 30)
        //createAlarmToilet(30);
        //Intent intent1 = new Intent(activity_timespentintoilet.this, activity_spentTimeInToiletMoreThan30.class);
        //startActivity(intent1);
        //finish();

        //화장실이 머무른 시간이 0-30분 else if(time > 0)
        //Intent intent2 = new Intent(activity_timespentintoilet.this, activitiy_spentTimeIntoiletLessThan20.class);
        //startActivity(intent2);
        //finish();

        //화장실에 없는 경우 else
        //Intent intent3 = new Intent(activity_timespentintoilet.this, activity_notInToilet.class);
        //startActivity(intent3);
        //finish();
    }

    private void createAlarmToilet(int time){ //핸드폰 팝업 알림 함수

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.drawable.logoclock);
        builder.setContentTitle("[안전바 응급호출 도우미]");
        builder.setContentText( "TimeSpentInToilet");
        //30분 이상 화장실에서 나오지 않았을 때
        if (time==30)
        {builder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText("노약자가 30분 넘게 화장실에서 나오지 않고 있습니다. \n안전을 확인해주세요."));}
        //60분 이상 화장실에서 나오지 않았을 때
        else{ builder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText("노약자가 60분 넘게 화장실에서 나오지 않고 있습니다. 신속하게 안전을 확인해주세요.\n5분 이내에 보호자 응답이 없으면 자동으로 응급신고가 접수됩니다."));
        }
        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());
    }

}