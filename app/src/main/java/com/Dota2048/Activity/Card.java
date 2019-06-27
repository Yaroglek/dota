package com.Dota2048.Activity;

import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.view.animation.*;

import com.Game.Dota2048.test.R;

import java.util.Timer;
import java.util.TimerTask;


public class Card extends FrameLayout{
    //private TextView label;  //呈现的文字
    private ImageView label;
    private MainActivity parent;
    //ColorMatrix cMatrix = new ColorMatrix();
    LayoutParams lp=null;
    LayoutParams la;
    //private ImageView Back;
    private int num = 0;     //绑定的编号
    private int lg;
    // 设置背景色

    static int[] images={R.drawable.lg_0,R.drawable.lg_1,R.drawable.lg_2,R.drawable.lg_3,R.drawable.lg_4,R.drawable.lg_5,R.drawable.lg_6,R.drawable.lg_7,R.drawable.lg_8,R.drawable.lg_9,R.drawable.lg_10,R.drawable.lg_11,R.drawable.lg_12,R.drawable.lg_13,R.drawable.lg_14,R.drawable.lg_15,R.drawable.lg_16,R.drawable.lg_17};

    public void setParent(MainActivity m){parent=m;}
    public Card(Context context) {
        super(context);
        setClipChildren(false);
        setClipToPadding(false);
        label = new ImageView(getContext());
        label.setImageResource(R.drawable.lg_0);
        la = new LayoutParams(-1,-1);  //创建个布局，填充满整个父局容器
        la.setMargins(8,8,8,8);
        addView(label,la);   //然后扔进去
        setNum(0);
    }

    public int getNum() {
        return num;
    }

    public void setNum(int x) {
        this.num = x;
        lg=0;
        while(x>1)
        {
            x>>=1;
            lg++;
        }
        setImage();
    }
    public void setImage()
    {
        if(lg==0)label.setVisibility(INVISIBLE);
        else
        {
            label.setImageResource(images[lg]);
            label.setVisibility(VISIBLE);
        }
    }

    public void Shine(int x,int im)
    {
        label.setImageResource(im);
        label.setVisibility(VISIBLE);
        this.num = x;
        lg=0;
        while(x>1)
        {
            x>>=1;
            lg++;
        }
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                parent.mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        /*label.setImageResource(images[lg]);*/
                        setImage();
                    }
                });
            }
        },120);
    }
    public boolean equals(Card o) {
        return getNum()==o.getNum();
    }
    @Override
    public void startAnimation(Animation animation)
    {
        int width = label.getWidth();
        int height = label.getHeight();
        lp=new LayoutParams((int)(width*0.95),(int)(height*0.95));
        lp.gravity = Gravity.CENTER;
        label.bringToFront();
        label.setLayoutParams(lp);
        label.startAnimation(animation);
    }
    public void Reset()
    {
        int left = label.getLeft();
        int top = label.getTop();
        int width = label.getWidth();
        int height = label.getHeight();
        label.clearAnimation();
        label.setLayoutParams(la);
        label.layout(left, top, left+width, top+height);
    }
    public void setColorFilter(ColorMatrixColorFilter cf){label.setColorFilter(cf);}
}

