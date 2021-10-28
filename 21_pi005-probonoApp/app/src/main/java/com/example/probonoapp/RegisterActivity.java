package com.example.probonoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증처리
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스 (서버연동)
    private EditText oldName, mEtLocate, oldPhonenumber, mEtGender, mEtBirth; //노약자 정보
    private EditText mEtName, EmtPwd, mEtPwd2,  mETPhonenumber;  // 아이디 비번+ 보호자 정보
    private Button mBtnRegister;  //회원가입 버튼
    private String emergency ="0";
    String strGender = " "; //노약자 성별
    String strEmergency_time = " "; //60,90,120 중 노약자가 위험상황이라고 판단한 시간
    String strHalfEmergency_time = " "; //50% 시간
    RadioGroup getGender, getEmergency_time;
    RadioButton radio_women, radio_men, radio_sixty, radio_ninety, radio_hundred_twenty;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //인텐트 플래그 설정
        startActivity(intent);  //인텐트 이동
        finish();   //현재 액티비티 종료
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance(); //Auth
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(); //데이터베이스  //앱 이름을 보통 사용

        oldName = (EditText)findViewById(R.id.et_oldName); //노약자 성함
        oldPhonenumber = (EditText)findViewById(R.id.et_oldPhonenumber); //노약자 전화번호
        mEtName = (EditText)findViewById(R.id.et_email); //아이디
        EmtPwd = (EditText)findViewById(R.id.et_pwd); //비밀번호
        mEtPwd2 = (EditText)findViewById(R.id.et_pwd2); //비밀번호 확인
        mEtLocate = (EditText)findViewById(R.id.et_locate); //노약자 자택주소
        mETPhonenumber = (EditText)findViewById(R.id.et_phonenumber);
        mEtBirth = (EditText)findViewById(R.id.et_birth); //노약자 생년월일
        mBtnRegister = (Button)findViewById(R.id.btn_register);



        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 버튼 누를시

                String strOldname = oldName.getText().toString().trim();
                String strOldphonenumber = oldPhonenumber.getText().toString().trim();
                String strEmail = mEtName.getText().toString().trim(); //trim(): 공백제거
                String strPwd = EmtPwd.getText().toString().trim();
                String strPwd2 = mEtPwd2.getText().toString().trim();
                //String strGender = mEtGender.getText().toString( ).trim();
                String strLocate = mEtLocate.getText().toString().trim();
                String strPhonenumber = mETPhonenumber.getText().toString().trim();
                String strBirth = mEtBirth.getText().toString().trim();

                radio_women = (RadioButton)findViewById(R.id.radio_women);
                radio_men  = (RadioButton)findViewById(R.id.radio_men);
                radio_sixty = (RadioButton)findViewById(R.id.radio_sixty);
                radio_ninety = (RadioButton)findViewById(R.id.radio_ninety);
                radio_hundred_twenty = (RadioButton)findViewById(R.id.radio_hundred_twenty);

                getGender = (RadioGroup)findViewById(R.id.get_gender);
                getEmergency_time = (RadioGroup)findViewById(R.id.get_emergency_time);


                if (radio_women.isChecked()) strGender = "여자";
                else if (radio_men.isChecked()) strGender = "남자";

                if (radio_sixty.isChecked()) strEmergency_time = "60";
                if (radio_ninety.isChecked()) strEmergency_time = "90";
                if (radio_hundred_twenty.isChecked()) strEmergency_time = "120";


                if (oldName.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"노약자 이름을 입력하세요!",Toast.LENGTH_SHORT).show();
                    mEtName.requestFocus();
                    return;
                }

                if (strGender == " "){
                    Toast.makeText(getApplicationContext(),"노약자 성별을 선택해주세요!",Toast.LENGTH_SHORT).show();
                    getGender.requestFocus();
                    return;
                }

                if (mEtLocate.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"노약자 자택주소를 입력하세요!",Toast.LENGTH_SHORT).show();
                    mEtName.requestFocus();
                    return;
                }


                if (mETPhonenumber.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"보호자 전화번호를 입력하세요!",Toast.LENGTH_SHORT).show();
                    mEtName.requestFocus();
                    return;
                }

                if (mEtName.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"아이디를 입력하세요!",Toast.LENGTH_SHORT).show();
                    mEtName.requestFocus();
                    return;
                }

                if (strEmergency_time == " "){
                    Toast.makeText(getApplicationContext(),"노약자분이 화장실에서 몇분 경과시 응급상황으로 판단할건지 선택해주세요!",Toast.LENGTH_SHORT).show();
                    getEmergency_time.requestFocus();
                    return;
                }

                if(!EmtPwd.getText().toString().equals(mEtPwd2.getText().toString())){
                    Toast.makeText(getApplicationContext(),"비밀번호가 일치하지 않습니다!",Toast.LENGTH_SHORT).show();
                    EmtPwd.setText("");
                    mEtPwd2.setText("");
                    EmtPwd.requestFocus();
                    return;
                }

                //firebase Auth 진행
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail,strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); //현재로그인한 계정 가져옴

                            HashMap<Object,String> result = new HashMap();
                            result.put("idToken",firebaseUser.getUid());
                            result.put("ID", strEmail);
                            result.put("PassWord",strPwd);
                            result.put("노약자 성함",strOldname);
                            result.put("노약자 전화번호",strOldphonenumber);
                            result.put("노약자 성별", strGender);
                            result.put("노약자 생년월일", strBirth);
                            result.put("노약자 자택주소", strLocate);
                            result.put("보호자 전화번호", strPhonenumber);
                            result.put("emergency",emergency);
                            result.put("emergency_time", strEmergency_time);

                            strHalfEmergency_time = Integer.toString(Integer.parseInt(strEmergency_time)/2);

                            //sharedPreferences에 emergencyTime 저장 -> 앱이 꺼져도 유지되도록
                            SharedPreferences sharedPreferences= getSharedPreferences("test", MODE_PRIVATE);    // test 이름의 기본모드 설정
                            SharedPreferences.Editor editor= sharedPreferences.edit(); //sharedPreferences를 제어할 editor를 선언
                            editor.putString("emergency_time", strEmergency_time); // key,value 형식으로 저장
                            editor.putString("half_emergency_time",strHalfEmergency_time);
                            //초기 응급상황 저장 -> 응급상황 아님, boolean 값으로 저장
                            editor.putBoolean("fall_emergency",false); //낙상 응급상황
                            editor.putBoolean("button_emergency",false); //응급호출 버튼 응급상황
                            editor.putBoolean("50%time_emergency",false); //화장실 응급시간 50% 초과
                            editor.putBoolean("100%time_emergency",false); //100% 초과

                            editor.commit();


                            //setValue : 데이터베이스에 삽입
                            mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(result);

                            Toast.makeText(RegisterActivity.this,"회원가입 성공!",Toast.LENGTH_SHORT).show();

                            Intent intent2 = new Intent(RegisterActivity.this, SignCompleteActivity.class);
                            startActivity(intent2); //가입 완료 페이지(3초)
                            finish();
                        }
                        else{
                            Toast.makeText(RegisterActivity.this,"회원가입 실패!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


}