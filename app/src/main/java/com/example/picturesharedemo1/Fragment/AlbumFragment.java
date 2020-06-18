package com.example.picturesharedemo1.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.picturesharedemo1.Activity.LoginActivity;
import com.example.picturesharedemo1.Activity.MainActivity;
import com.example.picturesharedemo1.Activity.PostDetail;
import com.example.picturesharedemo1.Bean.Post;
import com.example.picturesharedemo1.Bean.User;
import com.example.picturesharedemo.R;

import org.apache.http.client.HttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static com.example.picturesharedemo1.Activity.PostDetail.saveImageToGallery;

public class AlbumFragment extends Fragment {
    /*  private ImageView[] img = new ImageView[16];
        private int[] imagePath = new int[]{
            R.drawable.ic_logo,R.drawable.ic_logo,R.drawable.ic_logo,R.drawable.ic_logo,
            R.drawable.ic_logo,R.drawable.ic_logo,R.drawable.ic_logo,R.drawable.ic_logo,
            R.drawable.ic_logo,R.drawable.ic_logo,R.drawable.ic_logo,R.drawable.ic_logo,
            R.drawable.ic_logo,R.drawable.ic_logo,R.drawable.ic_logo,R.drawable.ic_logo
    };*/
    private ImageView[] img;
    private Bitmap[] bitmap ; //图片转成Bitmap数组
    private GridLayout layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_album, container, false);
        layout=view.findViewById(R.id.layout);

        LayoutInflater inflater1 = LayoutInflater.from(getActivity());
        View imgEntryView = inflater1.inflate(R.layout.dialog_pic, null);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        ImageView image = imgEntryView.findViewById(R.id.large_Pic);

        Bmob.initialize(getActivity(), "8369765908b5b24d951f7a13eb151240");

        //显示当前用户所有发布照片
        BmobQuery<Post> bmobQuery1 = new BmobQuery<>();
        User bu = BmobUser.getCurrentUser(User.class);
        bmobQuery1.addWhereEqualTo("author",bu);
        bmobQuery1.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if (e==null){
                    img=new ImageView[list.size()];
                    bitmap = new Bitmap[list.size()];

                    for(int i=0;i<list.size();i++) {
                        img[i] = new ImageView(getActivity());
                        //img[i].setImageURI(uri);
                        //通过glide框架显示图片到imageview
                        Glide.with(getActivity())
                                .load(list.get(i).getImage().getUrl()).placeholder(R.drawable.load_fail)
                                .error(R.drawable.load_fail
                                ).into(img[i]);
                        //转为bitmap，便于后面保存
                        int k=i;
                        Glide.with(getActivity()).asBitmap()
                                .load(list.get(i).getImage().getUrl())
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        bitmap[k] = resource;
                                    }
                                });

                        img[i].setPadding(2, 2, 2, 2);
                        //图片宽度自适应为屏幕1/3
                        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                        Point point = new Point();
                        windowManager.getDefaultDisplay().getSize(point); //将获取到的屏幕的宽度，放到point中
                        int itemWidth = point.x / 3;
                        //显示图片
                        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(itemWidth, itemWidth);
                       // Log.i("bmob", "数组长度：" + img.length);
                        img[i].setScaleType(ImageView.ScaleType.FIT_XY);
                        img[i].setLayoutParams(params);
                        layout.addView(img[i]);
                        //Toast.makeText(getActivity(), "获取发布成功", Toast.LENGTH_SHORT).show();
                        int j=i;//i是外部变量，不能传递，转给j才可以传递
                        img[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View paramView) {
                                // 加载自定义的布局文件
                                Glide.with(getActivity()).load(list.get(j).getImage().getUrl()).into(image);
                                dialog.setView(imgEntryView); // 自定义dialog
                                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                                dialog.show();
                                // 点击大图关闭dialog
                                imgEntryView.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View paramView) {
                                        dialog.cancel();
                                    }
                                });

                                //长按dialog保存图片到本地
                                imgEntryView.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View view) {
                                        //弹出的“保存图片”的Dialog
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setItems(new String[]{"保存图片","删除图片"}, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case 0:
                                                        if (bitmap[j] != null) {
                                                            saveImageToGallery(getActivity(), bitmap[j]);
                                                        } else {
                                                            Toast.makeText(getActivity(), "bitmap为空", Toast.LENGTH_SHORT).show();
                                                        }
                                                        break;
                                                    case 1:
                                                        if (bitmap[j] != null) {
                                                            //首先，监听对话框里面的button点击事件
                                                            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    switch (which) {
                                                                        case AlertDialog.BUTTON_POSITIVE:// "确认"按钮删除图片
                                                                            String removeID=list.get(j).getObjectId();
                                                                            removeImageToGallery(getActivity(), bitmap[j],removeID);
                                                                            Log.i("删除发布，","该发布的ID："+removeID);
                                                                            break;
                                                                        case BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                                                                            break;
                                                                        default:
                                                                            break;
                                                                    }
                                                                }
                                                            };
                                                            // 创建删除对话框
                                                            AlertDialog isExit = new AlertDialog.Builder(getActivity()).create();
                                                            // 设置对话框标题
                                                            isExit.setTitle("提示");
                                                            // 设置对话框消息
                                                            isExit.setMessage("确定要删除该发布吗？");
                                                            // 添加选择按钮并注册监听
                                                            isExit.setButton(Dialog.BUTTON_POSITIVE,"确定", listener);
                                                            isExit.setButton2("取消",listener);
                                                            // 显示对话框
                                                            isExit.show();

                                                        } else {
                                                            Toast.makeText(getActivity(), "bitmap为空", Toast.LENGTH_SHORT).show();
                                                        }
                                                }
                                            }
                                        });
                                        builder.show();
                                        return true;
                                    }
                                });
                            }
                        });

                    }
                }
                else{
                    Toast.makeText(getActivity(),"查询发布失败",Toast.LENGTH_SHORT).show();
                    Log.e("添加失败，","原因：",e);
                }


            }



        });
        return view;
    }

    private void removeImageToGallery(Context context, Bitmap bitmap,String removeID) {
        //从后台数据表中删除
        Post p2 = new Post();
        p2.setObjectId(removeID);
        p2.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(getActivity(),"删除成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(),"删除失败",Toast.LENGTH_SHORT).show();
                    Log.e("删除发布失败，","原因：",e);
                }
            }

        });


    }

}
