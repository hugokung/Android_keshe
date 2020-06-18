package com.example.picturesharedemo1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.picturesharedemo1.Bean.User;
import com.example.picturesharedemo.R;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegistActivity extends AppCompatActivity {
    private EditText username,password,repassword;
    private Button register;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        username = findViewById(R.id.register_edit_number);
        password = findViewById(R.id.register_edit_password);
        repassword = findViewById(R.id.register_edit_repassword);
        register = findViewById(R.id.button_register);
        //注冊
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.setUsername(username.getText().toString().trim());
                user.setPassword(password.getText().toString().trim());
                user.setPassword(repassword.getText().toString().trim());
                if(username.getText().toString().equals("")){
                    Toast.makeText(RegistActivity.this, "账号不能为空", Toast.LENGTH_SHORT).show();
                }else if(password.getText().toString().equals("")){
                    Toast.makeText(RegistActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }else if(!repassword.getText().toString().equals(password.getText().toString())){
                    Toast.makeText(RegistActivity.this, "两次密码不同", Toast.LENGTH_SHORT).show();
                }
                else {
                    user.signUp(new SaveListener<User>() {
                        @Override
                        public void done(User user, BmobException e) {
                            //注册成功
                            if(e==null){
                                Toast.makeText(RegistActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegistActivity.this, MainActivity.class);
                                startActivity(intent);
                            }else {
                                Toast.makeText(RegistActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }

            }
        });
    }

}
