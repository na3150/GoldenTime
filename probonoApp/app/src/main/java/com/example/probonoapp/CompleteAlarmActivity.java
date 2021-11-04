package com.example.probonoapp;

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

public class CompleteAlarmActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CompleteAlarmActivity.this, MenuActivity.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //인텐트 플래그 설정
        startActivity(intent);  //인텐트 이동
        finish();   //현재 액티비티 종료
    }

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
        Button btn_isSolved = (Button)findViewById(R.id.buttonSolved);
        btn_isSolved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = getLayoutInflater().inflate(R.layout.alertdialog_confirm_is_solved, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(dialogView);

                final AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();

                Button ok_btn = dialogView.findViewById(R.id.yesBtn2);
                ok_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "응급상황이 해결된 것으로 확인되었습니다.", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        //응급상황 초기화
                        SharedPreferences sharedPreferences= getSharedPreferences("test", MODE_PRIVATE);    // test 이름의 기본모드 설정
                        SharedPreferences.Editor editor= sharedPreferences.edit(); //sharedPreferences를 제어할 editor를 선언
                        editor.putBoolean("fall_emergency",false); //낙상 응급상황
                        editor.putBoolean("button_emergency",false); //응급호출 버튼 응급상황
                        editor.putBoolean("50%time_emergency",false); //화장실 응급시간 50% 초과
                        editor.putBoolean("100%time_emergency",false); //100% 초과
                        editor.putBoolean("alarmComplete", false); //응급신고 완료 여부
                        editor.commit();
                        //메뉴로 이동
                        Intent safeIntent = new Intent(CompleteAlarmActivity.this, MenuActivity.class);
                        startActivity(safeIntent);
                        finish();
                    }
                });
                Button cancle_btn = dialogView.findViewById(R.id.noBtn2);
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