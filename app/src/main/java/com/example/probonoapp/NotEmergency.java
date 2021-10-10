package com.example.probonoapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotEmergency extends AppCompatActivity { //노약자가 화장실에 없는 경우

    @Override
    public void onBackPressed() { //뒤로가기
        super.onBackPressed();
        Intent intent = new Intent(NotEmergency.this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //사용자 정보 가져오기위한 참조
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_in_toilet);

        final TextView getoldNameTextView = (TextView) findViewById(R.id.textView32);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("UserAccount");
        userID = user != null ? user.getUid() : null;

        reference.child(userID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                getoldNameTextView.setText("\'" + dataSnapshot.child("노약자 성함").getValue(String.class) + "\'님은 안전합니다.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}