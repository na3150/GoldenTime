package com.example.probonoapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

public class SpentTimeInToiletMoreThan_100Percent extends AppCompatActivity {

    final private String TAG = "spenttimeintoiletmorethan100percent";

    boolean isAlarm = false; //default는 true로 한 뒤(현재는 일단 false로 해뒀습니다), 응급상황이 아니라는 버튼을 누르면 true로 변환
    int spentTime =0; //화장실에 머무른 시간
    String OldName,OldGender, OldBirth, OldLocate; //119전송할 노약자 정보
    String phonenumber; //119전송할 보호자 번호

    @Override
    public void onBackPressed() { //뒤로가기
        super.onBackPressed();
        Intent intent = new Intent(SpentTimeInToiletMoreThan_100Percent.this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //사용자 정보 가져오기위한 참조
    private FirebaseUser user;
    private DatabaseReference reference ,ref;
    private String userID, emergencyTime;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spent_timeintoilet_more_than_100percent);


        final TextView getoldNameTextView = (TextView)findViewById(R.id.et_notify119);
        final TextView tv_emergencyTime = (TextView)findViewById(R.id.et_toiletTime);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("UserAccount");
        userID = user!= null? user.getUid() : null;


        //emergency_time 가져오기
        SharedPreferences sharedPreferences= getSharedPreferences("test", MODE_PRIVATE);    // test 이름의 기본모드 설정, 만약 test key값이 있다면 해당 값을 불러옴.
        emergencyTime = sharedPreferences.getString("emergency_time",""); //값 가져오기
        tv_emergencyTime.setText(emergencyTime+"분");

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
                        Intent safeIntent = new Intent(SpentTimeInToiletMoreThan_100Percent.this, NotEmergencyActivity.class);
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