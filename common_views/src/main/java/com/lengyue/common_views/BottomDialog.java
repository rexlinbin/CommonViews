package com.lengyue.common_views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;


public class BottomDialog<T> extends Dialog {

    private Context mContext;
    private LinearLayout contentView;
    private OnDialogSureListener<T> onDialogSureListener;
    private WheelView<T> wheelView;

    public BottomDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
        initWindow();
        initView();
        setCanceledOnTouchOutside(true);
    }

    private void initView(){
        contentView = new LinearLayout(mContext);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        contentView.setLayoutParams(linearLayoutParams);
        contentView.setBackgroundColor(Color.parseColor("#ffffff"));
        contentView.setOrientation(LinearLayout.VERTICAL);

        RelativeLayout relativeLayout = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, dp2px(50));
        relativeLayout.setLayoutParams(relativeLayoutParams);

        TextView sureTextView = new TextView(mContext);
        RelativeLayout.LayoutParams sureLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        sureLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        sureLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        sureLayoutParams.rightMargin = dp2px(15);
        sureTextView.setLayoutParams(sureLayoutParams);
        sureTextView.setText("确定");
        sureTextView.setTextSize(16);
        sureTextView.setTextColor(Color.parseColor("#358EF1"));
        sureTextView.setOnClickListener(v -> {
            if (onDialogSureListener != null){
                onDialogSureListener.onSure(wheelView.getSelectedItemData(), wheelView.getSelectedItemPosition());
            }
            dismiss();
        });
        relativeLayout.addView(sureTextView);

        TextView cancelTextView = new TextView(mContext);
        RelativeLayout.LayoutParams cancelLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        cancelLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        cancelLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        cancelLayoutParams.leftMargin = dp2px(15);
        cancelTextView.setLayoutParams(cancelLayoutParams);
        cancelTextView.setText("取消");
        cancelTextView.setTextSize(16);
        cancelTextView.setTextColor(Color.parseColor("#358EF1"));
        cancelTextView.setOnClickListener(v -> dismiss());
        relativeLayout.addView(cancelTextView);

        contentView.addView(relativeLayout);

        View divider = new View(mContext);
        LinearLayout.LayoutParams dividerLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(0.5f));
        divider.setLayoutParams(dividerLayoutParams);
        divider.setBackgroundColor(Color.parseColor("#eeeeee"));
        contentView.addView(divider);


        wheelView = new WheelView<>(mContext);
        LinearLayout.LayoutParams wheelViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp2px(200));
        wheelView.setLayoutParams(wheelViewLayoutParams);
        wheelView.setTextSize(21, true);
        wheelView.setTextAlign(WheelView.TEXT_ALIGN_CENTER);
        wheelView.setDividerColor(Color.parseColor( "#358EF1"));
        wheelView.setDividerHeight(dp2px(0.5f));
        wheelView.setDividerType(WheelView.DIVIDER_TYPE_WRAP);
        wheelView.setSelectedItemPosition(0);
        wheelView.setSelectedItemTextColor(Color.parseColor("#358EF1"));
        wheelView.setShowDivider(true);
        wheelView.setVisibleItems(5);
        wheelView.setLineSpacing(10, true);
        contentView.addView(wheelView);
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
                BottomDialog.super.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        contentView.startAnimation(animation);
    }

    public void setData(List<T> data){
        wheelView.setData(data);
    }

    public void setSelectIndex(int index){
        if (index > 0) {
            wheelView.setSelectedItemPosition(index);
        }

    }

    public void setOnDialogSureListener(OnDialogSureListener<T> onDialogSureListener){
        this.onDialogSureListener = onDialogSureListener;
    }

    public interface OnDialogSureListener<T>{
        void onSure(T data, int position);
    }

    /*
     * dp 转 px
     * */
    private int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, mContext.getResources().getDisplayMetrics());
    }
}
