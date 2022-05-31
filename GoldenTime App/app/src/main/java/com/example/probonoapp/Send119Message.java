package com.example.probonoapp;

import android.telephony.SmsManager;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Send119Message {

    //사용자 정보 가져오기위한 참조
    private FirebaseUser user;
    private DatabaseReference reference ,ref;
    private String userID;

    String oldName,OldGender, OldBirth, OldLocate; //119전송할 노약자 정보;
    String phonenumber; //119전송할 보호자 번호
    String smsNumber; //응급문자 보낼 번호
    String sms; //응급 문자 내용

    
    //api를 호출해서 lambda -> aws sns -> sms 전송
    void setSmsNumber(String smsNumber){
        this.smsNumber = smsNumber;
    }

    void sms(String sms){
        this.sms = sms;
    }

    void makeSendMessage(){ //응급문자 보내기 함수

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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(smsNumber,null, sms,null,null);
    }

}
