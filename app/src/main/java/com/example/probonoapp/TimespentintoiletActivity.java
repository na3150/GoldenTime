package com.example.probonoapp;

import static com.example.probonoapp.R.color.less30color;
import static com.example.probonoapp.R.color.more30color;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class TimespentintoiletActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TimespentintoiletActivity.this, MenuActivity.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //인텐트 플래그 설정
        startActivity(intent);  //인텐트 이동
        finish();   //현재 액티비티 종료
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_timespentintoilet);

        //dataSnapshot으로 센서값 가져와서 화장실 입장 'enter'가 false이면 '화장실에 없습니다' 띄우기


        TextView txt_toilet_time;
        txt_toilet_time = (TextView) findViewById(R.id.et_toiletTime2);
        int less30Color = ContextCompat.getColor(getApplicationContext(), less30color);
        int more30Color = ContextCompat.getColor(getApplicationContext(),more30color);

        //화장실에 머무른 시간이 100% 초과됐을 때 (현재는 default) if(time> = 60)
        //createAlarmToilet(60); //팝업 알림
        Intent intent = new Intent(TimespentintoiletActivity.this, SpentTimeInToiletMoreThan60.class);
        startActivity(intent);
        finish();

        //화장실이 머무른 시간이 50% 초과되었을 때
        //createAlarmToilet(30);
        //txt_toilet_time.setTextColor(more30Color); //text 색상 변경
        //Intent intent1 = new Intent(TimespentintoiletActivity.this, activity_spentTimeInToiletMoreThan30.class);
        //startActivity(intent1);
        //finish();

        //화장실에 없는 경우 : 'enter'가 false
        //Intent intent3 = new Intent(activity_timespentintoilet.this, activity_notInToilet.class);
        //startActivity(intent3);
        //finish();
    }

    /*private void createAlarmToilet(int time){ //핸드폰 팝업 알림 함수

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.drawable.icon_goldentime_round);
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
    }*/

}