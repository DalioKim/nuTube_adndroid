package com.example.youtube;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

public class LogInActivity extends AppCompatActivity {


    //저장할 사용자의 ID
    String id;

    //위젯선언
    EditText idEt, pwEt;
    Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        idEt =  findViewById(R.id.idEt);
        pwEt =  findViewById(R.id.pwEt);
        btn =  findViewById(R.id.btn);

        //getShared로 값을 가져와 String 에 저장 시킨다.
       // final SharedPreferences Shared = getSharedPreferences("Youtube", Activity.MODE_PRIVATE);
        //sharedd에 meeting 파일에 저장한 xml파일을 들고온다.
        //String member = Shared.getString("member", null);
        //아이디 비밀번호 입력후 로그인버튼을 클릭하면, 아이디가 저장이 되고 메인화면으로 넘어간다.

        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO : click event



               if(!idEt.getText().toString().equals(""))  {

                id = idEt.getText().toString();

                //아이디를 shared에 저장 후
                   final SharedPreferences Shared = getSharedPreferences("Youtube", Activity.MODE_PRIVATE); //SharedPreferences를 선언
                   SharedPreferences.Editor editor = Shared.edit();
                   editor.putString("id", id);
                   editor.commit();
                   Log.i("Login","저장된 아이디:"+Shared.getString("id", null));

                   //액티비티 이동

                   Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                   startActivity(intent);

                } else {
                   Toast.makeText(getApplicationContext(), "아이디 입력해주세요", Toast.LENGTH_LONG).show();


                }





            }
        });





    }
}
