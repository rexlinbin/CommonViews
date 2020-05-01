package com.lengyue.common_views.picturepicker;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.lengyue.common_views.R;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.List;


public class PicturePicker extends Dialog {

    private Context mContext;
    private View contentView;
    public final static int REQUEST_CODE_PICK = 10001;
    public final static int REQUEST_CODE_CAPTURE = 10002;

    private static String mPath;
    private static Uri mUri;

    public PicturePicker(@NonNull Context context) {
        super(context);
        this.mContext = context;
        initWindow();
        initView();
        setCanceledOnTouchOutside(true);
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.view_picturepicker, null);

        TextView take = contentView.findViewById(R.id.takePicture_textView);
        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPermissions(true);
            }
        });
        TextView pick = contentView.findViewById(R.id.pickPicture_textView);
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPermissions(false);
            }
        });
        TextView cancel = contentView.findViewById(R.id.cancel_textView);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setContentView(contentView);
    }

    /**
     * 初始化window参数
     */
    private void initWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 取消标题
        Window dialogWindow = getWindow();
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);
        // 设置显示动画
//        dialogWindow.setWindowAnimations(R.style.main_menu_animstyle);
    }

    @Override
    public void show() {
        super.show();
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(350);
        contentView.setAnimation(animation);
    }

    @Override
    public void dismiss() {
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(350);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                PicturePicker.super.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        contentView.startAnimation(animation);
    }

    private void takePicture() {
        MediaStoreCompat mMediaStoreCompat = new MediaStoreCompat((Activity) mContext);
        mMediaStoreCompat.setCaptureStrategy(new CaptureStrategy(true, mContext.getPackageName()));
        mMediaStoreCompat.dispatchCaptureIntent(mContext, REQUEST_CODE_CAPTURE);
        mUri = mMediaStoreCompat.getCurrentPhotoUri();
        mPath = mMediaStoreCompat.getCurrentPhotoPath();
    }

    private void pickPicture() {
        Matisse.from((Activity) mContext)
                //选择图片
                .choose(MimeType.ofImage())
                //是否只显示选择的类型的缩略图，就不会把所有图片视频都放在一起，而是需要什么展示什么
                .showSingleMediaType(true)
                //这两行要连用 是否在选择图片中展示照相 和适配安卓7.0 FileProvider
                .capture(false)
                .captureStrategy(new CaptureStrategy(true, mContext.getPackageName()))
                //有序选择图片 123456...
                .countable(true)
                //最大选择数量为9
                .maxSelectable(1)
                //选择方向
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                //界面中缩略图的质量
                .thumbnailScale(0.8f)
                //蓝色主题
                .theme(R.style.Matisse_Zhihu)
                //Glide加载方式
                .imageEngine(new GlideEngine())
                //请求码
                .forResult(REQUEST_CODE_PICK);
    }

    private void initPermissions(boolean isTake) {
        final RxPermissions rxPermissions = new RxPermissions((FragmentActivity) mContext);
        rxPermissions
                .requestEachCombined(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(permission -> {
                    if (permission.granted) {
                        if (isTake) {
                            takePicture();
                        } else {
                            pickPicture();
                        }
                        dismiss();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        Toast.makeText(getContext(), "权限未被授权", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "权限被禁止", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
    }

    public static void getResult(int requestCode, int resultCode, Intent data, List<String> list) {
        if (requestCode == REQUEST_CODE_CAPTURE) {
            //图片路径 同样视频地址也是这个 根据requestCode
//            if (mUri != null) {
//                list.add(mUri);
//            }
            if (mPath != null) {
                list.add(mPath);
            }
        } else if (requestCode == REQUEST_CODE_PICK) {
            //图片路径 同样视频地址也是这个 根据requestCode
            List<String> pathList = Matisse.obtainPathResult(data);
            list.addAll(pathList);
        }
    }
}

