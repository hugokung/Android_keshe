package com.example.picturesharedemo1.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.picturesharedemo.R;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

public class Srendipity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_srendipity);
        //延时操作
        Timer timer = new Timer();
        timer.schedule(timetask,2000);
        //id 8369765908b5b24d951f7a13eb151240
        Bmob.initialize(this,"8369765908b5b24d951f7a13eb151240");

    }
    TimerTask timetask = new TimerTask() {
        @Override
        public void run() {
            //startActivity(new Intent(Srendipity.this, MainActivity.class));
            //如果登录就跳转到主界面
            BmobUser bmobUser =  BmobUser.getCurrentUser(BmobUser.class);
            startActivity(new Intent(Srendipity.this, LoginActivity.class));
            /*if(bmobUser!=null)
            {
                startActivity(new Intent(Srendipity.this, MainActivity.class));
                finish();
            }else {
                //没有登录跳转到登录界面
                startActivity(new Intent(Srendipity.this, Login.class));
                finish();
            }*/
        }
    };
}
