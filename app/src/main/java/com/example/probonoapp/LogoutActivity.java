package com.example.probonoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class LogoutActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private Intent intent;
    //private RestartService restartService;

    @Override
    public void onBackPressed() { //뒤로가기
        super.onBackPressed();
        Intent intent = new Intent(LogoutActivity.this, activity_menu.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //인텐트 플래그 설정
        startActivity(intent);  //인텐트 이동
        finish();   //현재 액티비티 종료
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("MainActivity","onDestroy");
       // unregisterReceiver(restartService);
    }

    private void initData(){
        //restartService = new RestartService();
        //intent = new Intent(LogoutActivity.this, PersistentService.class);

        IntentFilter intentFilter = new IntentFilter("com.example.probonoapp.PersistentService");
        //registerReceiver(restartService,intentFilter);
        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);


        mFirebaseAuth = FirebaseAuth.getInstance();

        Button btn_logout  = findViewById(R.id.btn_logout);




        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그아웃
                mFirebaseAuth.signOut();
                Intent intent = new Intent(LogoutActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //initData();
    }
}