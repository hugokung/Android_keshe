package com.example.picturesharedemo1.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.picturesharedemo1.Bean.User;
import com.example.picturesharedemo.R;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.Calendar;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class My_EditInfoActivity extends AppCompatActivity {
    private static final int IMAGE_REQUEST_CODE = 0;
    private EditText mbirth,msex,mnickname,memail,mselfIntroduction,mphone;
    private ImageView mback,mpassword,mavatar;
    private String[] sexArry = new String[]{"男", "女","保密", };// 性别选择
    private Button msave;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);//需要在setContentView之前初始化
        this.setContentView(R.layout.activity_editinfo);

        Bmob.initialize(this, "8369765908b5b24d951f7a13eb151240");

        mavatar=findViewById(R.id.iv_avatar);
        mnickname=findViewById(R.id.et_nickname);
        msex=findViewById(R.id.et_sex);
        mbirth = findViewById(R.id.et_birth);
        mselfIntroduction=findViewById(R.id.et_selfintroduction);
        mpassword=findViewById(R.id.iv_pwd_change);
        mphone=findViewById(R.id.et_phone);
        memail=findViewById(R.id.et_email);

        mback=findViewById(R.id.iv_back);
        msave=findViewById(R.id.bt_save);

        //返回
        mback.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        initView();//显示数据库中信息
        newView();//显示更新信息
    }

    private void initView() {
        mphone.setInputType(InputType.TYPE_CLASS_PHONE);//电话
        memail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        BmobUser u = BmobUser.getCurrentUser(BmobUser.class);
        String Id = u.getObjectId();
        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(Id, new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e==null){
                    mnickname.setText(user.getNickname());
                    mselfIntroduction.setText(user.getSelfintroduction());
                    msex.setText(user.getGender());
                    mbirth.setText(user.getBirth());

                    memail.setText(user.getEmail());
                    mphone.setText(user.getMobilePhoneNumber());
                }
                else{
                    Toast.makeText(My_EditInfoActivity.this,"加载个人信息失败",Toast.LENGTH_SHORT).show();
                    Log.e("更新失败","失败原因",e);

                }
            }
        });
    }
    private void newView() {
      /*  String nickname=mnickname.getText().toString().trim();
        String selfIntroduction=mselfIntroduction.getText().toString().trim();
        String sex=msex.getText().toString().trim();
        String birth=mbirth.getText().toString().trim();
        String phone=mphone.getText().toString().trim();
        String email=memail.getText().toString().trim();*/
        mavatar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(My_EditInfoActivity.this, My_EditAratActivity.class);
                startActivity(intent);
            }
        });

        //更改密码
        mpassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(My_EditInfoActivity.this, My_EditPwdActivity.class);
                startActivity(intent);
            }
        });

        //生日选择
        mbirth.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showDatePickDlg();
                    return true;
                }
                return false;
            }
        });
        mbirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickDlg();
                }
            }
        });
        //性别选择
        msex.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showSexChooseDlg();
                    return true;
                }
                return false;
            }
        });
        msex.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showSexChooseDlg();
                }
            }
        });

        msave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if ((!mphone.getText().toString().equals("")) && (mphone.getInputType()!=InputType.TYPE_CLASS_PHONE)){
                    mphone.setError("请输入正确的手机号");
                }else if (!memail.getText().toString().equals("") && memail.getInputType()!=InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS){
                    memail.setError("请输入正确的电子邮箱");
                }else{
                    User newuser = new User();
                    BmobUser bu = BmobUser.getCurrentUser(BmobUser.class);
                    String Id = bu.getObjectId();

                    newuser.setObjectId(Id);
                    newuser.setNickname(mnickname.getText().toString());
                    newuser.setSelfintroduction(mselfIntroduction.getText().toString());
                    newuser.setBirth(mbirth.getText().toString());
                    newuser.setGender(msex.getText().toString());
                    newuser.setEmail(memail.getText().toString());
                    newuser.setMobilePhoneNumber(mphone.getText().toString());

                    newuser.update(Id,new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Toast.makeText(My_EditInfoActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(My_EditInfoActivity.this,"更新失败",Toast.LENGTH_SHORT).show();
                                Log.e("更新失败","失败原因",e);
                            }
                        }
                    });
                }


            }
        });
    }

    //选择生日
    private void showDatePickDlg() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(My_EditInfoActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int month =0;
                month=monthOfYear+1;
                String birth=year + "-" + month + "-" + dayOfMonth;
                My_EditInfoActivity.this.mbirth.setText(birth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    //选择性别
    private void showSexChooseDlg() {
        AlertDialog.Builder builder3 = new AlertDialog.Builder(this);// 自定义对话框
        builder3.setSingleChoiceItems(sexArry, 0, new DialogInterface.OnClickListener() {// 2默认的选中

            @Override
            public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
                // showToast(which+"");
                msex.setText(sexArry[which]);
                dialog.dismiss();// 随便点击一个item消失对话框，不用点击确认取消
            }
        });
        builder3.show();// 让弹出框显示
    }

}
