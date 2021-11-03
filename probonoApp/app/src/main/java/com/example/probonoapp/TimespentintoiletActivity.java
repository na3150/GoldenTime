package com.example.probonoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

        //화장실에 머무른 시간이 100% 초과됐을 때 (현재는 default) if(time> = 60)
        //Intent intent = new Intent(TimespentintoiletActivity.this, NotEmergencyActivity.class);
        //startActivity(intent);
        //finish();

        SharedPreferences sharedPreferences= getSharedPreferences("test", MODE_PRIVATE);

        if (sharedPreferences.getBoolean("alarmComplete",true)){ //응급신고가 완료되었을 때, 값이 없으면 false 불러옴
            Intent intent = new Intent(TimespentintoiletActivity.this,CompleteAlarmActivity.class);
            startActivity(intent);
        }
        //50%도 true, 100%일 때도 true이면 100%로 이동하도록
        else if(sharedPreferences.getBoolean("100%time_emergency",true)) {
            Intent intent = new Intent(TimespentintoiletActivity.this, SpentTimeInToiletMoreThan_100Percent.class);
            startActivity(intent);
        }//50%만 true일 때
        else if(sharedPreferences.getBoolean("50%time_emergency",true)) {
            Intent intent = new Intent(TimespentintoiletActivity.this, activity_spentTimeInToiletMoreThanHalf_50Percent.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(TimespentintoiletActivity.this,NotEmergencyActivity.class);
            startActivity(intent);
        }

    }





}