package com.example.probonoapp;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;

public class UserActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() { //뒤로가기
        super.onBackPressed();
        Intent intent = new Intent(UserActivity.this, MenuActivity.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //인텐트 플래그 설정
        startActivity(intent);  //인텐트 이동
        finish();   //현재 액티비티 종료
    }

    private FirebaseUser user;
    private DatabaseReference reference ,ref;
    private String userID, ID;
    Button btn_myinfo;
    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증처리
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스 (서버연동)



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);


        TextView oldNameTextView = (TextView)findViewById(R.id.et_oldName);
        TextView oldGenderTextView = (TextView)findViewById(R.id.et_oldGender);
        TextView oldBirthTextView = (TextView)findViewById(R.id.et_oldBirth);
        TextView oldPhonenumberTextView = (TextView)findViewById(R.id.et_oldPhonenumber);
        TextView oldLocateTextView = (TextView)findViewById(R.id.et_oldLocate);
        TextView phonenumberTextView = (TextView)findViewById(R.id.et_Phonenumber);
        TextView idTextView = (TextView)findViewById(R.id.et_email);

        mFirebaseAuth = FirebaseAuth.getInstance(); //Auth
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(); //데이터베이스  //앱 이름을 보통 사용


        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("UserAccount");
        userID = user!= null? user.getUid() : null;


        reference.child(userID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idTextView.setText("어서오세요 "+ dataSnapshot.child("ID").getValue(String.class)+" 님! \uD83D\uDE00");
                oldNameTextView.setText(dataSnapshot.child("노약자 성함").getValue(String.class));
                oldGenderTextView.setText(dataSnapshot.child("노약자 성별").getValue(String.class));
                oldBirthTextView.setText(dataSnapshot.child("노약자 생년월일").getValue(String.class));
                oldPhonenumberTextView.setText(dataSnapshot.child("노약자 전화번호").getValue(String.class));
                oldLocateTextView.setText(dataSnapshot.child("노약자 자택주소").getValue(String.class));
                phonenumberTextView.setText(dataSnapshot.child("보호자 전화번호").getValue(String.class));
                ID = dataSnapshot.child("ID").getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserActivity.this, "something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });

        btn_myinfo = (Button)findViewById(R.id.btn_myinfo);
        btn_myinfo.setOnClickListener((new View.OnClickListener() { //수정하기 버튼을 눌렀을 때
            @Override
            public void onClick(View v) {

                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); //현재로그인한 계정 가져옴

                String strOldname = oldNameTextView.getText().toString().trim();
                String strPhonenumber = phonenumberTextView.getText().toString().trim();
                String strGender = oldGenderTextView.getText().toString( ).trim();
                String strLocate = oldLocateTextView.getText().toString().trim();
                String strOldphonenumber = oldPhonenumberTextView.getText().toString().trim();
                String strBirth = oldBirthTextView.getText().toString().trim();

                HashMap<Object,String> result = new HashMap();
                result.put("idToken",firebaseUser.getUid());
                result.put("ID", ID);
                result.put("노약자 성함", strOldname);
                result.put("노약자 전화번호", strOldphonenumber);
                result.put("노약자 성별", strGender);
                result.put("노약자 생년월일", strBirth);
                result.put("노약자 자택주소", strLocate);
                result.put("보호자 전화번호", strPhonenumber);

                //setValue : 데이터베이스에 삽입
                mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(result);
                Toast.makeText(UserActivity.this,"수정 완료!",Toast.LENGTH_SHORT).show();


            }
        }));

    }
}
