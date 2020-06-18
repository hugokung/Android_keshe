package com.example.picturesharedemo1.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.picturesharedemo1.Activity.PostDetail;
import com.example.picturesharedemo1.Bean.Post;
import com.example.picturesharedemo1.Bean.User;
import com.example.picturesharedemo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static com.example.picturesharedemo1.Activity.PostDetail.saveImageToGallery;

public class CollectFragment extends Fragment {
    private ImageView[] img;
    private Bitmap[] bitmap ; //图片转成Bitmap数组
    private GridLayout layout;
    private List<Post> collect_data = new ArrayList<>();
    private AlertDialog dialog;
    View imgEntryView;
    ImageView image;

    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect, container, false);
        layout=view.findViewById(R.id.layout);

        LayoutInflater inflater1 = LayoutInflater.from(getActivity());
        imgEntryView = inflater1.inflate(R.layout.dialog_pic, null);
        dialog = new AlertDialog.Builder(getActivity()).create();
        image = imgEntryView.findViewById(R.id.large_Pic);

        Bmob.initialize(getActivity(), "8369765908b5b24d951f7a13eb151240");
        //获取当前用户收藏总数及照片
        init_collect();
        return view;
    }
    private void init_collect() {
        BmobUser this_user = BmobUser.getCurrentUser(BmobUser.class);
        BmobQuery<Post> query_like = new BmobQuery<>();
        User currentUser = new User();
        currentUser.setObjectId(this_user.getObjectId());
        query_like.addWhereRelatedTo("user_collect",new BmobPointer(currentUser));
        query_like.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if(e==null){
                    img=new ImageView[list.size()];
                    bitmap = new Bitmap[list.size()];

                    collect_data.clear();
                    collect_data = list;
                    for(int i=0;i<list.size();i++) {
                        img[i] = new ImageView(getActivity());
                        //通过glide框架显示图片到imageview
                        Glide.with(getActivity())
                                .load(list.get(i).getImage().getUrl()).placeholder(R.drawable.load_fail)
                                .error(R.drawable.load_fail
                                ).into(img[i]); //通过glide框架显示图片到imageview
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
                        img[i].setScaleType(ImageView.ScaleType.FIT_XY);
                        img[i].setLayoutParams(params);
                        layout.addView(img[i]);
                        //Log.i("smile", "地址：" + list.get(i).getImage().getUrl());
                        //点击查看大图并保存
                        int j=i;
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
                                        builder.setItems(new String[]{"保存图片","取消收藏"}, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case 0:
                                                        if (bitmap[j] != null) {
                                                            saveImageToGallery(getActivity(), bitmap[j]);
                                                        } else {
                                                            Toast.makeText(getActivity(), "bitmap为空", Toast.LENGTH_SHORT).show();
                                                        }
                                                    case 1:
                                                        if (bitmap[j] != null) {
                                                            //首先，监听对话框里面的button点击事件
                                                            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    switch (which) {
                                                                        case AlertDialog.BUTTON_POSITIVE:// "确认"按钮删除图片
                                                                            String removeID=list.get(j).getObjectId();
                                                                            removeImageToGallery(getActivity(), bitmap[j],removeID);
                                                                            Log.i("取消收藏，","该发布的ID："+removeID);
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
                                                            isExit.setMessage("确定取消收藏该图片吗？");
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
                   // Toast.makeText(getActivity(), "收藏个数"+list.size(), Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), "获取收藏信息失败"+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void removeImageToGallery(Context context, Bitmap bitmap,String removeID) {
        //从后台数据表中删除
        Post po = new Post();
        po.increment("collection_num", -1);
        po.update(removeID, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                //Toast.makeText(getActivity(), "ok2", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "收藏数更新失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Post uncollect_po = new Post();
        uncollect_po.setObjectId(removeID);
        BmobRelation relation = new BmobRelation();
        relation.remove(uncollect_po);

        User currentUser=BmobUser.getCurrentUser(User.class);
        User user = new User();
        user.setObjectId(currentUser.getObjectId());
        user.setUser_collect(relation);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(getActivity(), "取消收藏成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "取消失败" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
