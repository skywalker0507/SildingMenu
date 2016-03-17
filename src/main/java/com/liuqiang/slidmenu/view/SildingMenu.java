package com.liuqiang.slidmenu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.liuqiang.slidmenu.R;
import com.nineoldandroids.view.ViewHelper;


/**
 * Created by liuqiang on 2016/3/10.
 */
public class SildingMenu extends HorizontalScrollView {

    private LinearLayout wapper;
    private ViewGroup menu;
    private ViewGroup content;
    private int screenWidth;
    private int menuRightPadding;
    private int menuWidth;
    private boolean once = false;
    private int isMenu;
    private boolean isOpen;

    public SildingMenu(Context context) {
        this(context, null);
    }

    public SildingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SildingMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SildingMenu, defStyle, 0);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
                case R.styleable.SildingMenu_rightPadding:
                    menuRightPadding = array.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                                    context.getResources().getDisplayMetrics()));
            }
        }

        array.recycle();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
       /* menuRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources()
                .getDisplayMetrics());*/
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!once) {
            wapper = (LinearLayout) getChildAt(0);
            menu = (ViewGroup) wapper.getChildAt(0);
            content = (ViewGroup) wapper.getChildAt(1);
            Log.e("width1---width2",menu.getLayoutParams().width+"==="+ (screenWidth - menuRightPadding));
            menuWidth = menu.getLayoutParams().width = screenWidth - menuRightPadding;
            Log.e("again",""+menu.getLayoutParams().width);
            content.getLayoutParams().width = screenWidth;
            once = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            this.scrollTo(menuWidth, 0);
            once = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        int scrollX = getScrollX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isMenu = scrollX;
                break;
            case MotionEvent.ACTION_UP:
//                Log.e("ACTION_UP",""+scrollX);
                if (isMenu == 0) { //menu在屏幕上
                    isOpen = true;
                    if (scrollX > menuWidth / 4) {
                        //显示content内容
                        this.smoothScrollTo(menuWidth, 0);
                    } else {
                        //显示menu内容
                        this.smoothScrollTo(0, 0);
                    }
                } else {
                    isOpen = false;
                    if (scrollX < menuWidth * 3 / 4) {
                        this.smoothScrollTo(0, 0);
                    } else {
                        this.smoothScrollTo(menuWidth, 0);
                    }
                }

                return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        float scale = l * 1.0f / menuWidth; // 1 ~ 0


        float rightScale = 0.7f + 0.3f * scale;
        float leftScale = 1.0f - scale * 0.3f;
        float leftAlpha = 0.6f + 0.4f * (1 - scale);

        ViewHelper.setTranslationX(menu, menuWidth * scale * 0.8f);

        ViewHelper.setScaleX(menu, leftScale);
        ViewHelper.setScaleY(menu, leftScale);
        ViewHelper.setAlpha(menu, leftAlpha);
        ViewHelper.setPivotX(content, 0);
        ViewHelper.setPivotY(content, content.getHeight() / 2);
        ViewHelper.setScaleX(content, rightScale);
        ViewHelper.setScaleY(content, rightScale);
    }

    public void openMenu() {
        if (isOpen) return;
        this.smoothScrollTo(0, 0);
        isOpen = true;
    }

    public void closeMenu() {
        if (!isOpen) return;
        this.smoothScrollTo(menuWidth, 0);
        isOpen = false;
    }

    public void toggle() {
        if (isOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }
}
