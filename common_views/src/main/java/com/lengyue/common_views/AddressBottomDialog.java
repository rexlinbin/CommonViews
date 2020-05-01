package com.lengyue.common_views;

import android.app.Dialog;
import android.content.Context;
import android.content.res.XmlResourceParser;
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

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AddressBottomDialog extends Dialog {

    private Context mContext;
    private LinearLayout contentView;
    private OnDialogSureListener onDialogSureListener;
    private List<String> addressList;
    private List<Province> provinceList;
    private List<City> cityList;
    private List<District> districtList;
    private List<WheelView> wheelViewList;

    public AddressBottomDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
        initData();
        initWindow();
        initView();
        setCanceledOnTouchOutside(true);
    }

    private void initData() {
        addressList = new ArrayList<>();
        addressList.add("");
        addressList.add("");
        addressList.add("");
        provinceList = getProvincesInfo();
        cityList = getCitiesInfo();
        districtList = getDistrictsInfo();

        wheelViewList = new ArrayList<>();
        wheelViewList.add(getWheelView(0));
        wheelViewList.add(getWheelView(1));
        wheelViewList.add(getWheelView(2));
    }

    private ArrayList<Province> getProvincesInfo() {
        //设置xml解析器，为读取的数据创建ArrayList对象
        XmlResourceParser xrp = mContext.getResources().getXml(R.xml.provinces);
        ArrayList<Province> provinces = new ArrayList<>();
        try {
            int eventType = xrp.getEventType();
            while (eventType != XmlResourceParser.END_DOCUMENT) {
                //当内容标签开始时，读取相应标签的属性
                if (eventType == XmlResourceParser.START_TAG && xrp.getName().equals("Province")) {
                    String id = xrp.getAttributeValue(null, "ID");
                    String name = xrp.getAttributeValue(null, "ProvinceName");
                    provinces.add(new Province(Integer.parseInt(id), name));
                }
                eventType = xrp.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        xrp.close();
        return provinces;
    }

    private ArrayList<City> getCitiesInfo() {
        //设置xml解析器，为读取的数据创建ArrayList对象
        XmlResourceParser xrp = mContext.getResources().getXml(R.xml.cities);
        ArrayList<City> cities = new ArrayList<>();
        try {
            int eventType = xrp.getEventType();
            while (eventType != XmlResourceParser.END_DOCUMENT) {
                //当内容标签开始时，读取相应标签的属性
                if (eventType == XmlResourceParser.START_TAG && xrp.getName().equals("City")) {
                    String id = xrp.getAttributeValue(null, "ID");
                    String name = xrp.getAttributeValue(null, "CityName");
                    String pId = xrp.getAttributeValue(null, "PID");
                    cities.add(new City(Integer.parseInt(id), name, Integer.parseInt(pId)));
                }
                eventType = xrp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        xrp.close();
        return cities;
    }

    private ArrayList<District> getDistrictsInfo() {
        //设置xml解析器，为读取的数据创建ArrayList对象
        XmlResourceParser xrp = mContext.getResources().getXml(R.xml.districts);
        ArrayList<District> districts = new ArrayList<>();
        try {
            int eventType = xrp.getEventType();
            while (eventType != XmlResourceParser.END_DOCUMENT) {
                //当内容标签开始时，读取相应标签的属性
                if (eventType == XmlResourceParser.START_TAG && xrp.getName().equals("District")) {
                    String id = xrp.getAttributeValue(null, "ID");
                    String name = xrp.getAttributeValue(null, "DistrictName");
                    String cId = xrp.getAttributeValue(null, "CID");
                    districts.add(new District(Integer.parseInt(id), name, Integer.parseInt(cId)));
                }
                eventType = xrp.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        xrp.close();
        return districts;
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
                onDialogSureListener.onSure(addressList);
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

        LinearLayout wheelLayout = new LinearLayout(mContext);
        LinearLayout.LayoutParams wheelLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        wheelLayout.setLayoutParams(linearLayoutParams);
        wheelLayout.setOrientation(LinearLayout.HORIZONTAL);

        wheelLayout.addView(wheelViewList.get(0));
        wheelLayout.addView(wheelViewList.get(1));
        wheelLayout.addView(wheelViewList.get(2));

        addressList.set(0, provinceList.get(0).getName());
        wheelViewList.get(0).setData(provinceList);
        List<City> cities = new ArrayList<>();
        for (City c : cityList) {
            if (c.pId == 1){
                cities.add(c);
            }
        }
        addressList.set(1, cities.get(0).getName());
        wheelViewList.get(1).setData(cities);
        List<District> districts = new ArrayList<>();
        for (District d : districtList) {
            if (d.cId == 1){
                districts.add(d);
            }
        }
        addressList.set(2, districts.get(0).getName());
        wheelViewList.get(2).setData(districts);

        contentView.addView(wheelLayout);
        setContentView(contentView);
    }

    private WheelView getWheelView(int index){
        WheelView wheelView = new WheelView<>(mContext);
        LinearLayout.LayoutParams wheelViewLayoutParams = new LinearLayout.LayoutParams(0, dp2px(200));
        wheelViewLayoutParams.weight = 1;
        wheelView.setLayoutParams(wheelViewLayoutParams);
        wheelView.setTextSize(18, true);
        wheelView.setTextAlign(WheelView.TEXT_ALIGN_CENTER);
        wheelView.setAutoFitTextSize(true);
        wheelView.setDividerColor(Color.parseColor( "#358EF1"));
        wheelView.setDividerHeight(dp2px(0.5f));
        wheelView.setDividerType(WheelView.DIVIDER_TYPE_WRAP);
        wheelView.setSelectedItemPosition(0);
        wheelView.setSelectedItemTextColor(Color.parseColor("#358EF1"));
        wheelView.setShowDivider(true);
        wheelView.setVisibleItems(5);
        wheelView.setLineSpacing(10, true);
        wheelView.setOnItemSelectedListener(new WheelView.OnItemSelectedListener<BaseAddress>() {
            @Override
            public void onItemSelected(WheelView<BaseAddress> wheelView, BaseAddress data, int position) {
                addressList.set(index, data.getName());
                if (index == 0){
                    List<City> cities = new ArrayList<>();
                    for (City c : cityList) {
                        if (c.pId == data.getId()){
                            cities.add(c);
                        }
                    }
                    if (cities.size() > 0) {
                        addressList.set(1, cities.get(0).getName());
                    }else{
                        addressList.set(1, "");
                    }
                    wheelViewList.get(1).setData(cities);
                    wheelViewList.get(1).setSelectedItemPosition(0);

                    List<District> districts = new ArrayList<>();
                    for (District d : districtList) {
                        if (d.cId == cities.get(0).getId()){
                            districts.add(d);
                        }
                    }
                    if (districts.size() > 0) {
                        addressList.set(2, districts.get(0).getName());
                    }else{
                        addressList.set(2, "");
                    }
                    wheelViewList.get(2).setData(districts);
                    wheelViewList.get(2).setSelectedItemPosition(0);
                }else if (index == 1){
                    List<District> districts = new ArrayList<>();
                    for (District d : districtList) {
                        if (d.cId == data.getId()){
                            districts.add(d);
                        }
                    }
                    if (districts.size() > 0) {
                        addressList.set(2, districts.get(0).getName());
                    }else{
                        addressList.set(2, "");
                    }
                    wheelViewList.get(2).setData(districts);
                    wheelViewList.get(2).setSelectedItemPosition(0);
                }
            }
        });

        return wheelView;
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
                AddressBottomDialog.super.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        contentView.startAnimation(animation);
    }


    public void setOnDialogSureListener(OnDialogSureListener onDialogSureListener){
        this.onDialogSureListener = onDialogSureListener;
    }

    public interface OnDialogSureListener{
        void onSure(List<String> addressList);
    }

    /*
     * dp 转 px
     * */
    private int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, mContext.getResources().getDisplayMetrics());
    }

    private class Province extends BaseAddress{
        Province(int id, String name) {
            super(id, name);
        }

        @NonNull
        @Override
        public String toString() {
            return getName();
        }
    }

    private class City extends BaseAddress{
        private int pId;

        City(int id, String name, int pId) {
            super(id, name);
            this.pId = pId;
        }

        public int getpId() {
            return pId;
        }

        public void setpId(int pId) {
            this.pId = pId;
        }

        @NonNull
        @Override
        public String toString() {
            return getName();
        }
    }

    private class District extends BaseAddress{
        private int cId;

        District(int id, String name, int cId) {
            super(id, name);
            this.cId = cId;
        }

        public int getcId() {
            return cId;
        }

        public void setcId(int cId) {
            this.cId = cId;
        }

        @NonNull
        @Override
        public String toString() {
            return getName();
        }
    }

    class BaseAddress{
        private int id;
        private String name;
        BaseAddress(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
