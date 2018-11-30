package com.ali.permissiondemotwo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    //相机权限
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final int REQUEST_CAMERA_PERMISSION_CODE = 100;
    private static final int GO_TO_SETTING_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //去打开相机
    public void openCamera(View view) {
        //checkSelfPermission 检测是否有对应的权限
        //PackageManager.PERMISSION_GRANTED 常量  代表已经授权
        if (ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            //请求权限
            ActivityCompat.requestPermissions(this, new String[]{CAMERA_PERMISSION}, REQUEST_CAMERA_PERMISSION_CODE);
        }
    }

    //跳转到相机页面
    public void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    //请求权限，接受回调的地方
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION_CODE:
                //如果授权结果码为0,说明被用户授权
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera();
                } else {
                    //用户已经拒绝权限
                    //todo 解释为什么需要用这个权限
                    //用户是否勾选了不再提醒，如果勾选不再提醒的话，系统不会再弹出授权窗口
                    //但是这样的话，用户是不是没有机会再使用这个功能，而且用户点击没有效果
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA_PERMISSION)) {
                            //用户没勾选,解释权限，再次请求权限
                            showPermissionTip();
                    } else {
                        //当用户勾选了不再提示，会回调到这里，在这里我们弹窗引导用户跳转到设置页面，打开权限
                        //引导用户打开设置页面
                        showPermissionSettingTip();
                    }

                }
                break;


        }

    }

    /**
     * 引导用户去设置页面打开相机权限
     */
    private void showPermissionSettingTip() {
        new AlertDialog.Builder(this)
                .setTitle("帮助")
                .setMessage("当前应用缺少【相机】权限\n\n请点击\"设置\"-\"应用权限\"去打开所需权限")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //打开设置页面
                        goToSettingPage();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 跳转到设置页面
     */
    private void goToSettingPage() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, GO_TO_SETTING_REQUEST_CODE);
    }

    /**
     * 解释为什么要使用相机权限
     */
    private void showPermissionTip() {
        new AlertDialog.Builder(this)
                .setTitle("说明")
                .setMessage("美颜功能需要【相机】权限")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //请求权限
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{CAMERA_PERMISSION}, REQUEST_CAMERA_PERMISSION_CODE);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    //跳转带结果的页面，接受回调的地方
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //当用户从设置页面返回的时候
            case GO_TO_SETTING_REQUEST_CODE:
                //检测用户是否打开了设置页面的授权，如果同意直接打开相机，否则不做处理
                if (ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                    startCamera();
                }
                break;
        }
    }
}
