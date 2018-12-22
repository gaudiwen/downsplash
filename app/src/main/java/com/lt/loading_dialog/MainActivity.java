package com.lt.loading_dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Context context =MainActivity.this;
    View view;
    private float mPosX = 0;
    private float mPosY = 0;
    private float mCurPosX = 0;
    private float mCurPosY = 0;
    private GestureHandler gestureHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        setContentView(R.layout.activity_main);
        initView();
        //initListener();
    }

    private void initView() {

        view = (View) findViewById(R.id.touchview);
        gestureHandler = new GestureHandler();
    }

    private void initListener() {

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // TODO Auto-generated method stub
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mPosX = event.getX();
                        mPosY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurPosX = event.getX();
                        mCurPosY = event.getY();

                        break;
                    case MotionEvent.ACTION_UP:
                        if (mCurPosY - mPosY > 0
                                && (Math.abs(mCurPosY - mPosY) > 25)) {
                            //向下滑動

                        } else if (mCurPosY - mPosY < 0
                                && (Math.abs(mCurPosY - mPosY) > 25)) {
                            //向上滑动
                            //collapse();
                        }

                        break;
                }
                return true; //这里返回false的话 无法响应MOVE和UP。
            }
        });

    }

    //从dispatch拦截事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return gestureHandler.doEventFling(ev) || super.dispatchTouchEvent(ev);
    }


    class GestureHandler {

        //屏幕宽高
        int sWidth = DisplayUtils.getScreenWidth(context);
        int sHeight = DisplayUtils.getScreenHeight(context);

        //按下的点
        PointF down;
        //Y轴滑动的区间
        float minY, maxY;
        //按下时的时间
        long downTime;
        //边缘判定距离，
        double margin = sWidth * 0.035;
        //Y轴最大区间范围，即Y轴滑动超出这个范围不触发事件
        double height = sHeight * 0.2;
        //X轴最短滑动距离 X轴滑动范围低于此值不触发事件
        double width = sWidth * 0.5;
        //是否处于此次滑动事件
        boolean work = false;

        public boolean doEventFling(MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    //记录下按下的点
                    downTime = System.currentTimeMillis();
                    down = new PointF(event.getX(), event.getY());
                    minY = maxY = down.y;
                    //判定是否处于边缘侧滑
                    if (down.x < margin || (sWidth - down.x) < margin) work = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    //记录滑动Y轴区间
                    if (work)
                        if (event.getY() > down.y) {
                            maxY = event.getY();
                        } else {
                            minY = event.getY();
                        }
                    break;
                case MotionEvent.ACTION_UP:
                    if (work) {
                        handle(new PointF(event.getX(), event.getY()));
                        work = false;
                        return true;
                    }
                    work = false;
            }
            //如果当前处于边缘滑动判定过程中，则消费掉此事件不往下传递。
            return work;
        }

        public boolean handle(PointF up) {
            long upTime = System.currentTimeMillis();
            float tWidth = Math.abs(down.x - up.x);
            if (maxY - minY < height && tWidth > width && (upTime - downTime) / tWidth < 2.5) {
                //起点在左边
                if (down.x < margin) {
                    left();
                    return true;
                }
                //起点在右边
                if ((sWidth - down.x) < margin) {
                    right();
                    return true;
                }
            }
            return false;
        }


        public void left() {
            //处理左边缘滑动事件
            Toast.makeText(getApplicationContext(), "处理左滑事件", Toast.LENGTH_SHORT).show();

        }


        public void right() {

        }
    }


    public void login(View view) {
        LoadingDialog loadingDialog = new LoadingDialog(this, "正在登录...", R.mipmap.ic_dialog_loading);
        loadingDialog.show();
    }

    public void showIosDialog(View v) {
        IosPopupWindow iosPopupWindow = new IosPopupWindow(this);
        iosPopupWindow.showAtScreenBottom(v);
    }
}
