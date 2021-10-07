package com.example.probonoapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

        //응급신고 메세지를 전송할 번호, 일단 제 번호로 해뒀습니다.
        String smsNumber = "01050313150";
        //전송할 문자 내용
        String sms = "[안전바 응급호출 도우미]\n\n" + "노약자 \""+oldName+"\"님에게 응급상황이 발생하였습니다. \n구조대 출동이 필요합니다.\n\n"+
                "자택주소: "+OldLocate + "\n성별: "+OldGender + "\n생년월일: " +OldBirth+"\n보호자 전화번호: "+phonenumber;
        
        //메세지 전송 테스트 => 현재 권한 문제가 좀 있음.
        //SmsManager smsManager = SmsManager.getDefault();
        //smsManager.sendTextMessage(smsNumber,null, sms,null,null);
        
        Button notEmergency = (Button)findViewById(R.id.buttonNotEmergency); //응급상황이 아니라는 버튼을 눌렀을 때
        notEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAlarm = false; //false로 변경
            }
        });
        if (spentTime>5 && isAlarm){ //응급호출 버튼을 누른이후 5분 경과+응급상황이 아니라는 버튼을 누르지 않았을 때
            //SmsManager smsManager1 = SmsManager.getDefault();
           // smsManager.sendTextMessage(smsNumber,null, sms,null,null);
            Toast.makeText(getApplicationContext(), "응급신고가 접수되었습니다!", Toast.LENGTH_LONG).show();
        }

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