package com.example.probonoapp;

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

public class CompleteAlarmActivity extends AppCompatActivity {

    //노약자 성명 가져오기위한 참조
    private FirebaseUser user;
    private DatabaseReference reference ,ref;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completealarm);

        final TextView getoldNameTextView = (TextView)findViewById(R.id.et_getOldName);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("UserAccount");
        userID = user!= null? user.getUid() : null;

        reference.child(userID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                getoldNameTextView.setText("\'"+dataSnapshot.child("노약자 성함").getValue(String.class)+"님\'의 자택으로");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}