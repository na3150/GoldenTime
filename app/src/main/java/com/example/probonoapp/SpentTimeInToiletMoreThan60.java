package com.example.probonoapp;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SpentTimeInToiletMoreThan60 extends AppCompatActivity {

    boolean isAlarm = false; //default는 true로 한 뒤(현재는 일단 false로 해뒀습니다), 응급상황이 아니라는 버튼을 누르면 true로 변환
    int spentTime =0; //화장실에 머무른 시간
    String OldName,OldGender, OldBirth, OldLocate; //119전송할 노약자 정보
    String phonenumber; //119전송할 보호자 번호

    @Override
    public void onBackPressed() { //뒤로가기
        super.onBackPressed();
        Intent intent = new Intent(SpentTimeInToiletMoreThan60.this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //사용자 정보 가져오기위한 참조
    private FirebaseUser user;
    private DatabaseReference reference ,ref;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spent_timeintoilet_more_than60);

        final TextView getoldNameTextView = (TextView)findViewById(R.id.et_notify119);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("UserAccount");
        userID = user!= null? user.getUid() : null;

        //노약자 성명(사용자 정보)가져오기 위한 snapshot
        reference.child(userID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                getoldNameTextView.setText("\""+dataSnapshot.child("노약자 성함").getValue(String.class)+"\"님이 화장실에 머무르는 \n\n시간이 길어지고 있습니다. \n\n\""+dataSnapshot.child("노약자 성함").getValue(String.class)+"\"님의 상황을 \n\n신속하게 파악해주세요. ");
                OldName = dataSnapshot.child("노약자 성함").getValue(String.class);
                OldGender = dataSnapshot.child("노약자 성별").getValue(String.class);
                OldBirth = dataSnapshot.child("노약자 생년월일").getValue(String.class);
                OldLocate = dataSnapshot.child("노약자 자택주소").getValue(String.class);
                phonenumber = dataSnapshot.child("보호자 전화번호").getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //응급상황이 아니라는 버튼을 눌렀을 때
        Button notEmergency = (Button)findViewById(R.id.buttonNotEmergency);
        notEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAlarm = false; //false로 변경
            }
        });

        //응급신고 메세지를 전송할 번호, 일단 제 번호로 해뒀습니다.
        String smsNumber = "01050313150";
        //전송할 문자 내용
        String sms = "[안전바 응급호출 도우미]\n\n" + "노약자 \""+OldName+"\"님이 화장실에서 실신한 것(응급상황)으로 우려됩니다. \n구조대 출동이 필요합니다.\n\n"+
                "자택주소: "+OldLocate + "\n성별: "+OldGender + "\n생년월일: " +OldBirth+"\n보호자 전화번호: "+phonenumber;

        if (spentTime >= 65 && isAlarm) //isAlarm == true 이면 119문자 전송
        {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(smsNumber,null, sms,null,null);
            Toast.makeText(getApplicationContext(), "응급신고가 접수되었습니다!", Toast.LENGTH_LONG).show();
        }
    }

}