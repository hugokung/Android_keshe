package com.example.picturesharedemo1.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.picturesharedemo1.Bean.User;
import com.example.picturesharedemo.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class My_EditPwdActivity extends AppCompatActivity {
    private EditText oldpwd,newpwd,renewpwd;
    private Button msave;
    private ImageView mback;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_editpwd);

        oldpwd=findViewById(R.id.et_oldpwd);
        newpwd=findViewById(R.id.et_newpwd);
        renewpwd=findViewById(R.id.et_renewpwd);
        msave=findViewById(R.id.bt_save);
        mback=findViewById(R.id.iv_back);

        //返回个人主页面
        mback.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
        //更改密码
        msave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                User user = new User();
              /*
              //判断密码输入是否正确
              BmobUser bu = BmobUser.getCurrentUser(BmobUser.class);
                String Id = bu.getObjectId();
                BmobQuery<User> bmobQuery = new BmobQuery<>();
                bmobQuery.getObject(Id, new QueryListener<User>() {
                    @Override
                    public void done(User user, BmobException e) {
                        if (e==null){
                            if(oldpwd.getText().toString().equals())

                            Toast.makeText(My_EditPwdActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(My_EditPwdActivity.this,"密码正确",Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/
                user.setPassword(newpwd.getText().toString().trim());
                user.setPassword(renewpwd.getText().toString().trim());

                if(oldpwd.getText().toString().equals(newpwd.getText().toString())){
                    Toast.makeText(My_EditPwdActivity.this, "新密码不得与旧密码相同", Toast.LENGTH_SHORT).show();
                }else if(newpwd.getText().toString().equals("")){
                    Toast.makeText(My_EditPwdActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }else if(!renewpwd.getText().toString().equals(newpwd.getText().toString())){
                    Toast.makeText(My_EditPwdActivity.this, "两次密码不同", Toast.LENGTH_SHORT).show();
                }else{
                    BmobUser.updateCurrentUserPassword(oldpwd.getText().toString(),newpwd.getText().toString().trim(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(My_EditPwdActivity.this,"更新密码成功",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(My_EditPwdActivity.this,"更新密码失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }
}
