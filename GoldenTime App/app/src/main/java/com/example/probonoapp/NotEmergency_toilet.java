package com.example.probonoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class NotEmergency_toilet extends AppCompatActivity {

    @Override
    public void onBackPressed() { //뒤로가기
        super.onBackPressed();
        Intent intent = new Intent(NotEmergency_toilet.this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_emergency_toilet);

        //화장실 응급상황 초기화 응급상황 초기화
        SharedPreferences sharedPreferences= getSharedPreferences("test", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("50%time_emergency",false).apply();
        sharedPreferences.edit().putBoolean("100%time_emergency",false).apply();
        sharedPreferences.edit().putBoolean("alarmComplete",false).apply();

    }
}