package com.example.administrator.updateicondemo;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.icon)
    CircleImageView icon;
    @BindView(R.id.photo)
    CircleImageView photo;
    @BindView(R.id.relative)
    RelativeLayout relativeLayout;
    protected static Uri tempUri;
    Uri uri;
    private File picture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
    @OnClick({R.id.update,R.id.icon,R.id.relative})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.icon:
                //预览
                relativeLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.update:
                //更改头像
                showPop();
                break;
            case R.id.relative:
                relativeLayout.setVisibility(View.GONE);
                break;
        }
    }

    private void showPop() {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        View view = View.inflate(this, R.layout.photo_item, null);
        TextView takePic = view.findViewById(R.id.take_pic);
        TextView choosePic = view.findViewById(R.id.choose_pic);
        TextView cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomDialog.dismiss();
            }
        });
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture(); // 拍照
                bottomDialog.dismiss();
            }
        });
        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();// 相册()
                bottomDialog.dismiss();
            }
        });
        bottomDialog.setContentView(view);
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.setCancelable(true);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        view.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();
    }

    /**
     * 拍照
     */
    public void takePicture() {
        File file = new File(getExternalCacheDir(), "img.jpg");
        tempUri = Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), "image.jpg"));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    FileProvider.getUriForFile(this, "com.example.administrator.updateicondemo.fileprovider", file));
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        }
        Log.e("1233","222");
        startActivityForResult(intent, 1);
    }
    /**
     * 选择图片
     */
    public void choosePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 2);
    }
    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 160);
        intent.putExtra("outputY", 160);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param
     * @param
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            icon.setImageBitmap(bitmap);
            photo.setImageBitmap(bitmap);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case 1:
                    if (Build.VERSION.SDK_INT > 23) {
                        picture = new File(getExternalCacheDir() + "/img.jpg");
                        uri = FileProvider.getUriForFile(this, "com.example.administrator.rjproject.fileprovider", picture);
                        //裁剪照片
                        startPhotoZoom(uri);
                    } else {
                        startPhotoZoom(tempUri);// 开始对图片进行裁剪处理
                    }
                    break;
                case 2:
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;
                case 3:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }

}
