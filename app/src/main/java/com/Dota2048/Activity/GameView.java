package com.Dota2048.Activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.Game.Dota2048.test.R;

public class GameView extends FrameLayout {

    private Card[][] cardsMap = new Card[4][4];
    private int[][]value;
    private GridLayout BackGround;
    private GridLayout FrontGround;
    LayoutParams lp = new LayoutParams(-1,-1);
    public void set(MainActivity p,int[][] v,boolean[][] s) {
        value = v;
        shines = s;
        for(int i=0;i<4;++i)
            for(int j =0;j<4;++j)
                cardsMap[i][j].setParent(p);
    }

    private boolean[][] shines;
    public GameView(Context context) {
        super(context);
        initGameView();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGameView();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initGameView();
    }
    public void StartAnimes(Animation[][] animes)
    {
        for(int x =0;x<4;++x)
            for(int y=0;y<4;++y)
                if(animes[x][y]!=null)
                    if(cardsMap[x][y].getNum()>0)cardsMap[x][y].startAnimation(animes[x][y]);
    }
    /**
     * 初始化界面
     */
    private void initGameView(){
        FrontGround = new GridLayout(getContext());
        BackGround = new GridLayout(getContext());
        BackGround.setColumnCount(4);
        FrontGround.setColumnCount(4);
        BackGround.setBackgroundColor(Color.BLACK);
        addCards(getCardWitch(),getCardWitch());
        FrontGround.setClipChildren(false);
        FrontGround.setClipToPadding(false);
        addView(BackGround);
        addView(FrontGround);
    }
    private void addCards(int cardWidth,int cardHeight){
        Card c;
        lp.setMargins(8,8,8,8);
        for(int y = 0;y< 4;y++){
            for(int x = 0;x < 4;x++){
                c = new Card(getContext());
                c.setNum(0);
                //Log.d("233","3");
                FrameLayout fr = new FrameLayout(getContext());
                ImageView im = new ImageView(getContext());
                im.setColorFilter(Color.parseColor("#11ffffff"));
                im.setImageResource(R.drawable.lg_0);
                fr.addView(im,lp);
                BackGround.addView(fr,cardWidth,cardHeight);
                FrontGround.addView(c,cardWidth,cardHeight);
                //Log.d("233","4");
                cardsMap[x][y] = c;
            }
        }
    }
    
    public void setAll()
    {
        for(int x =0;x<4;++x)
            for(int y =0;y<4;++y)
            {
                if(shines[x][y])
                {
                    cardsMap[x][y].Shine(value[x][y],R.drawable.im_re);
                }
                else cardsMap[x][y].setNum(value[x][y]);
                cardsMap[x][y].Reset();
            }
    }
    public int getCardWitch(){
        //Log.d("233","5");
        DisplayMetrics displayMetrics;
        displayMetrics = getResources().getDisplayMetrics();
        int carWitch;
        carWitch = displayMetrics.widthPixels;

        return (carWitch-10)/4;
    }

    public void SetAll(int im)
    {
        for(int i=0;i<4;++i)
            for(int j=0;j<4;++j)
                if(shines[i][j])cardsMap[i][j].Shine(value[i][j],im);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        // 高大小
        int heightSize;
        // 只有宽的值是精确的才对高做精确的比例校对
        heightSize = widthSize;
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    public Point getBlock(float x, float y)
    {
        int w = getCardWitch();
        int xx = ((int)(x/w));
        int yy = ((int)(y/w));
        return new Point(xx,yy);
    }
    public void Brighter(int x,int y,int Brightness)
    {
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(new float[]{1, 0, 0, 0, Brightness, 0, 1, 0, 0, Brightness, 0, 0, 1, 0, Brightness, 0, 0, 0, 1, 0});
        ColorMatrixColorFilter cmcf = new ColorMatrixColorFilter(matrix);
        cardsMap[x][y].setColorFilter(cmcf);
    }
}