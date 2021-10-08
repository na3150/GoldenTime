package com.example.probonoapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//노약자가 안전바의 응급호출 버튼을 눌렀을 때
public class Emergency_getFall extends AppCompatActivity {

    @Override
    public void onBackPressed() { //뒤로가기
        super.onBackPressed();
        Intent intent = new Intent(Emergency_getFall.this, AlarmList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    //사용자 정보 가져오기위한 참조
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    String oldName,OldGender, OldBirth, OldLocate; //119전송할 노약자 정보;
    String phonenumber; //119전송할 보호자 번호
    boolean isAlarm = false; //default는 true로 한 뒤(현재는 일단 false로 해뒀습니다), 응급상황이 아니라는 버튼을 누르면 true로 변환
    int spentTime = 0; //응급호출버튼을 누르고 경과된 시간

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getfall);

        /*
         FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        */

        final TextView getoldNameTextView = (TextView)findViewById(R.id.et_notifyPushedEmergencyButton);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("UserAccount");
        userID = user!= null? user.getUid() : null;

        reference.child(userID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                oldName = dataSnapshot.child("노약자 성함").getValue(String.class);
                OldGender = dataSnapshot.child("노약자 성별").getValue(String.class);
                OldBirth = dataSnapshot.child("노약자 생년월일").getValue(String.class);
                OldLocate = dataSnapshot.child("노약자 자택주소").getValue(String.class);
                phonenumber = dataSnapshot.child("보호자 전화번호").getValue(String.class);
                getoldNameTextView.setText("\""+oldName+"\"님의 안전이 우려됩니다.\n\n\""+oldName+"\"님의 낙상이 감지된 상태이니 \n\n안전을 확인해주세요.");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        
        Button notEmergency = (Button)findViewById(R.id.buttonNotEmergency); //응급상황이 아니라는 버튼을 눌렀을 때
        notEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAlarm = false; //false로 변경
                View dialogView = getLayoutInflater().inflate(R.layout.alertdialog, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(dialogView);

                final AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();

                Button ok_btn = dialogView.findViewById(R.id.yesBtn);
                ok_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isAlarm = false;
                        Toast.makeText(getApplicationContext(), "응급상황이 아닌것으로 확인되었습니다.", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });
                Button cancle_btn = dialogView.findViewById(R.id.noBtn);
                cancle_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "응급상황 유지", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    private void createAlarmEmergencyButton(){ //핸드폰 팝업 알림 함수

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.drawable.logoclock);
        builder.setContentTitle("[안전바 응급호출 도우미]");
        builder.setContentText( "응급호출버튼");
        builder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText("노약자분이 안전바의 응급호출 버튼을 누르셨습니다.\n신속하게 안전을 확인해주세요."));
        //builder.setContentIntent(mPendingIntent);

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