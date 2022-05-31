package com.example.probonoapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Push_emergency_button extends AppCompatActivity {

    @Override
    public void onBackPressed() { //뒤로가기
        super.onBackPressed();
        Intent intent = new Intent(Push_emergency_button.this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    boolean isAlarm = false; //default는 true로 한 뒤(현재는 일단 false로 해뒀습니다), 응급상황이 아니라는 버튼을 누르면 true로 변환
    int spentTime = 0; //화장실에 머무른 시간
    String OldName,OldGender, OldBirth, OldLocate; //119전송할 노약자 정보
    String phonenumber; //119전송할 보호자 번호

    //백그라운드 시간 계산: lambda에서 보낸 시간 데이터와 현재시간 데이터를 1초 단위로 받아와서 5분 이상이 되면 sms 전송하도록 구현?


       //사용자 정보 가져오기위한 참조
        private FirebaseUser user;
        private DatabaseReference reference;
        private String userID;
        private String emergencyTime; //응급버튼을 누른시각
        Button notEmergency;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_push_emergency_button);

            //사용자가 푸시알림을 눌렀을 때 실행되도록
            Intent intent = getIntent();
            if(intent != null) {//푸시알림을 선택해서 실행한것이 아닌경우 예외처리
                String notificationData = intent.getStringExtra("test");
                if(notificationData != null)
                    Log.d("FCM_TEST", notificationData);
            }


            final TextView emergencyTextView = (TextView) findViewById(R.id.txt_emergency);

            user = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("UserAccount");
            userID = user != null ? user.getUid() : null;

            //노약자 성명(사용자 정보)가져오기 위한 snapshot
            reference.child(userID).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    emergencyTextView.setText("\"" + dataSnapshot.child("노약자 성함").getValue(String.class) + "\"님의 안전이 우려됩니다. \n\n\"" + dataSnapshot.child("노약자 성함").getValue(String.class) + "\"님이 응급상황버튼을 \n\n 누른 상태이니 안전을 확인해주세요. ");
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
            notEmergency = (Button)findViewById(R.id.buttonNotEmergency);

            notEmergency.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                            Intent safeIntent = new Intent(Push_emergency_button.this, NotEmergencyActivity.class);
                            startActivity(safeIntent);
                            finish();
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

}
