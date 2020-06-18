package com.example.picturesharedemo1.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.picturesharedemo1.Bean.User;
import com.example.picturesharedemo.R;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {
    private EditText username,password;
    private CheckBox rem_pw;
    private SharedPreferences sp;
    private Button login;
    TextView regist;
    private  Boolean bPwdSwitch = false;

    private String uname,upassword;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //登录
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        regist = findViewById(R.id.regist);
        rem_pw = findViewById(R.id.cb_remember_pwd);
        sp=this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        //判断记住密码多选框状态
        if(sp.getBoolean("ISCHECK", false)){
            rem_pw.setChecked(true);
            username.setText(sp.getString("USER_NAME",""));
            password.setText(sp.getString("PASSWORD",""));
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.setUsername(username.getText().toString().trim());
                user.setPassword(password.getText().toString().trim());
                user.login(new SaveListener<User>() {
                    @Override
                    public void done(User o, BmobException e) {
                        if(e == null){
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            if(rem_pw.isChecked()){

                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("USER_NAME",username.getText().toString());
                                editor.putString("PASSWORD", password.getText().toString());
                                editor.commit();
                            }
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));


                        }else{
                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }

        });
        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegistActivity.class));
            }
        });
        final ImageView ivPwdSwitch = findViewById(R.id.iv_pwd_switch);
        //隐藏密码
        ivPwdSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bPwdSwitch = !bPwdSwitch;
                if(bPwdSwitch){
                    ivPwdSwitch.setImageResource(
                            R.drawable.ic_visibility_black_24dp);
                    password .setInputType(
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }else{

                    ivPwdSwitch.setImageResource(
                            R.drawable.ic_visibility_off_black_24dp);
                    password .setInputType(
                            InputType.TYPE_TEXT_VARIATION_PASSWORD |
                                    InputType.TYPE_CLASS_TEXT);
                    password .setTypeface(Typeface.DEFAULT);
                }
            }
        });
        //记住密码
        rem_pw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(rem_pw.isChecked()){
                    Toast.makeText(LoginActivity.this, "记住密码已选中", Toast.LENGTH_SHORT).show();
                    sp.edit().putBoolean("ISCHECK",true).commit();

                }else{
                    Toast.makeText(LoginActivity.this,"记住密码没有选中", Toast.LENGTH_LONG).show();
                    sp.edit().putBoolean("ISCHECK", false).commit();
                }

            }
        });



    }
}
