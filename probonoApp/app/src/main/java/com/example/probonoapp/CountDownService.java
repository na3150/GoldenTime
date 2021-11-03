package com.example.probonoapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CountDownService extends Service { //응급신고 발생 후 5분 후에 보호자 응답 체크 후 , button==false이면 응급문자 신고

    //카운트 다운 끝남과 동시에 문자 발송
    //사용자 정보 가져오기위한 참조
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    String oldName,OldGender, OldBirth, OldLocate; //119전송할 노약자 정보;
    String phonenumber; //119전송할 보호자 번호
    final String smsNumber ="01050313150"; //응급문자 보낼 번호
    String sms; //응급 문자 내용

    //한번에 주석처리: ctrl+ shift + /
    private static final String TAG = "CountDownService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        if (intent == null) {
            return START_NOT_STICKY;
        }

        CountDownTimer countDownTimer = new CountDownTimer(300000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                getData();
                Log.e(TAG,"CountDown: "+ millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                Log.e(TAG, "onFinish: ");
                if(isWantAlarm()){ //"응급상황이 아닙니다"버튼을 누르지 않았으면 신고
                makeSendMessage();
                createCompleteAlarm();
                }
            }
        }.start();
        return START_STICKY;
    }

    void makeSendMessage(){ //응급문자 보내기 함수

        Log.d(TAG,"oldName: " + oldName);
        Log.d(TAG,"oldGender: " + OldGender);
        Log.d(TAG,"oldBirth: "+ OldBirth);
        Log.d(TAG,"oldLocate: " + OldLocate);
        sms = "[안전바 응급호출 도우미]\n\n" + "노약자 \""+ oldName +"\"님에게 응급상황이 발생하였습니다. \n구조대 출동이 필요합니다.\n\n"+"자택주소: "+ OldLocate + "\n성별: "+ OldGender + "\n생년월일: " + OldBirth +"\n보호자 전화번호: "+ phonenumber;
        Log.d(TAG, "Sms = " + sms);
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> parts = smsManager.divideMessage(sms);
        smsManager.sendMultipartTextMessage(smsNumber,null, parts,null,null);
    }

    private void createCompleteAlarm(){ //응급신고 완료되었다는 팝업 알림 전송

        Intent intent = new Intent(this, CompleteAlarmActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        SharedPreferences sharedPreferences= getSharedPreferences("test", MODE_PRIVATE);    // test 이름의 기본모드 설정
        SharedPreferences.Editor editor= sharedPreferences.edit(); //sharedPreferences를 제어할 editor를 선언
        //카운트다운 끝났을 때 sharedPreferences로 응급상황 완료되었음을 저장
        editor.putBoolean("alarmComplete", true);
        editor.commit();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.drawable.icon_goldentime_round);
        builder.setContentTitle("[안전바 응급호출 도우미]");
        builder.setContentText( "저장된 노약자 정보로 응급신고가 완료되었습니다.");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        notificationManager.notify(1, builder.build());
    }

    private void getData()
    {
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("UserAccount");
        userID = user != null ? user.getUid() : null;

        //노약자 성명(사용자 정보)가져오기 위한 snapshot
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
    }

    //사용자가 '응급상황이 아닙니다' 버튼을 눌렀는지 확인(응급신고를 원하는지), true이면 신고
    private Boolean isWantAlarm(){
        SharedPreferences sharedPreferences= getSharedPreferences("test", MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        //하나라도 응급상황이면 응급신고
        if (sharedPreferences.getBoolean("fall_emergency",false)) return true;
        else if (sharedPreferences.getBoolean("button_emergency",false)) return true;
        else if (sharedPreferences.getBoolean("100%time_emergency",false)) return true;
        return false; //어떤 응급상황도 해당되지 않는 경우
    }
}
