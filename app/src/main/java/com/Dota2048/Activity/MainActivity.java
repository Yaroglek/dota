package com.Dota2048.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.Dota2048.Data.BestScore;
import com.Dota2048.Data.Gold;
import com.Dota2048.Service.BackgroundMusic;
import com.Dota2048.Skills.Hero;
import com.Dota2048.Skills.Skill;
import com.Dota2048.Skills.SkillFactory;
import com.Game.Dota2048.test.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.Dota2048.Skills.Hero.HeroNums;


public class MainActivity extends Activity {
    private final String fileDirPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/Dota2048/data/background";
    private final String[] fileName = {"menu.mp3", "game.mp3", "heroselect.wav", "battle.wav"};
    private final String[] levels = {"先锋", "卫士", "中军", "统帅", "传奇", "万古流芳", "超凡入圣", "冠绝一世"};
    private final int[] resource = {R.raw.menu, R.raw.game, R.raw.heroselect, R.raw.battle};
    private final int BackNum = 4;
    private final int MusicNum = 3;
    private final int storepages = (HeroNums-1) / 3 + 1;
    private final int enterpages = HeroNums/3 + 1;
    private final List<Point> points = new ArrayList<>();
    private final SoundPool mSoundPoll = new SoundPool(100, AudioManager.STREAM_MUSIC, 0);

    private final int[][] value = new int[4][4];
    private final boolean[][] shines = new boolean[4][4];
    private Animation[][] animes = new TranslateAnimation[4][4];
    private final LinearLayout[] enters = new LinearLayout[enterpages];
    private final LinearLayout[] stores = new LinearLayout[storepages];

    private void add()
    {
        SharedPreferences s = this.getSharedPreferences("pay",MODE_PRIVATE);
        if(!s.getBoolean("Gold1",false))
        {
            new Gold(this).addGold(5000);
            SharedPreferences.Editor e = s.edit();
            e.putBoolean("Gold1",true);
            e.apply();
        }
    }
    public TextView tvScore;//计分的
    public TextView tvBestScore;//最高分
    public TextView tvMaxLevel;
    public TextView tvLevel;
    public TextView tvIntro;
    public TextView tvPrice;
    public TextView tvGold;
    public TextView tvSkill;
    public TextView tvStore;

    private MediaPlayer mp = new MediaPlayer();
    public Handler mainHandler;
    private Toast toast;
    public ImageView sk;
    private int score;
    private int bestScores;
    private int contentView;
    private GameView gv;

    public Skill mySkill;
    private boolean move = true;
    private Resources res;
    private int currentPage = 0;
    private long exitTime;
    private boolean merge;
    private boolean battling;
    private boolean hardGame;
    private int num;
    protected ArrayList<Point> clicks = new ArrayList<>();
    private boolean isUseProp;
    private boolean isUseProp2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainHandler = new Handler();
        setContentView(R.layout.activity_menu);
        setMenuButtons();
        createFile();
        importMusic();
        mp.start();
        add();
        Gold g = new Gold(MainActivity.this);
        if(g.getGold()<5000) {
            g.addGold(1000);
        }
    }
    @Override
    protected void onStop() {
        Intent intent = new Intent(MainActivity.this, BackgroundMusic.class);
        stopService(intent);
        super.onStop();

    }
    @SuppressLint("WrongConstant")
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (contentView == R.layout.activity_enter || contentView == R.layout.activity_store || contentView == R.layout.activity_main) {
                Clear();
                if (contentView != R.layout.activity_store) switchMusic(0);
                setContentView(R.layout.activity_menu);
                setMenuButtons();
            } else {

                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    showTextToast("再按一次返回键退出");
                    exitTime = System.currentTimeMillis();
                } else {
                    this.finish();
                    System.exit(0);
                    Log.d("exit", "exit");
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    public void showTextToast(String msg) {
        if (toast != null) toast.cancel();
        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void createFile() {

        try {
            File dir = new File(fileDirPath);// 目录路径
            if (!dir.exists())dir.mkdirs();
            res = getResources();
            for (int i = 0; i < BackNum; ++i) {
                String filePath = fileDirPath + "/" + fileName[i];// 文件路径
                File file = new File(filePath);
                if (!file.exists()) {
                    InputStream ins = getResources().openRawResource(resource[i]);
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[8192];
                    int count;
                    while ((count = ins.read(buffer)) > 0) {
                        fos.write(buffer, 0, count);
                    }
                    fos.close();
                    ins.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearBest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("清除最高分记录");
        builder.setMessage("你确定要清除最高分吗");
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bestScores = 0;
                        new BestScore(MainActivity.this,hardGame).setBestScode(0);
                        showBestScore();
                        dialogInterface.dismiss();
                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void playSkillVoice() {
        if (mySkill.getSkillId() == 0) return;
        int id = mySkill.getSkillId() * 2 + MusicNum - 1;
        playMusic(id);
    }

    private void playAttackVoice() {
        if (mySkill.getSkillId() == 0) return;
        int id = mySkill.getSkillId() * 2 + MusicNum;
        playMusic(id);
    }

    private void importMusic() {
            mSoundPoll.load(this, R.raw.death, 1);
            mSoundPoll.load(this, R.raw.respawn, 1);
            mSoundPoll.load(this, R.raw.levelup, 1);
            res = getResources();
        for (int i = 1; i <= Hero.HeroNums; ++i) {
            mSoundPoll.load(this, res.getIdentifier("voice_skill_" + i, "raw", getPackageName()), 1);
            mSoundPoll.load(this, res.getIdentifier("voice_attack_" + i, "raw", getPackageName()), 1);
        }
        try {
            mp.setDataSource(fileDirPath + "/" + fileName[0]);
            mp.setLooping(true);
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setGameView() {
        setContentView(R.layout.activity_main);
        contentView = R.layout.activity_main;
        gv = findViewById(R.id.gameView);
        if (gv != null) {
            gv.set(this, value, shines);
            gv.setOnTouchListener(new View.OnTouchListener() {
                private float startX, startY;//初始的位置
                private float offsetX, offsetY; //偏移的值
                private boolean wait;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (!move) return true;
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startX = event.getX();
                            startY = event.getY();
                            break;
                        case MotionEvent.ACTION_UP:
                            if (isUseProp)
                            {
                                Point p=gv.getBlock(startX, startY);
                                if(DestroyBlock(p.x,p.y,10000)!=-1)
                                {
                                    playSkillVoice();
                                    Gold g = new Gold(MainActivity.this);
                                    g.buy(10);
                                    showGold();
                                }
                                isUseProp=false;
                            }
                            if (isUseProp2)
                            {
                                Point p=gv.getBlock(startX, startY);
                                if(ChangeToAnother(p.x,p.y)!=-1)
                                {
                                    playSkillVoice();
                                    Gold g = new Gold(MainActivity.this);
                                    g.buy(10);
                                    showGold();
                                }
                                isUseProp2=false;
                            }
                            if (mySkill.isWait()) {
                                //showTextToast("点击了");
                                mySkill.addClicks(gv.getBlock(startX, startY));
                                break;
                            }
                            offsetX = event.getX() - startX;
                            offsetY = event.getY() - startY;
                            Clear();
                            int limit = 100;
                            if (Math.abs(offsetX) > Math.abs(offsetY)) {//这个是防止斜
                                if (offsetX < -limit) {
                                    Log.d("move", "left");
                                    swipeLeft();

                                } else if (offsetX > limit) {
                                    Log.d("move", "right");
                                    swipeRight();
                                }
                            } else {
                                if (offsetY < -limit) {
                                    Log.d("move", "up");
                                    swipeUp();
                                } else if (offsetY > limit) {
                                    Log.d("move", "down ");
                                    swipeDown();
                                }
                            }
                            if (merge) {
                                playAttackVoice();
                                Log.d("voice", "true");
                                Cool();
                                gv.StartAnimes(animes);
                            }
                            break;
                    }
                    storeRecord();
                    return true;
                }
            });
        }
    }

    private void setView() {
        setButtons();
        setTexts();
        showScore();
        setCoolImage();
    }

    public void showGold() {
        Gold g = new Gold(MainActivity.this);
        if (tvGold != null) tvGold.setText(g.getGold() + "");
    }

    public int getScore() {
        return score;
    }

    public void clearScore() {
        score = 0;
    }

    private void showScore() {
        if (tvScore != null) tvScore.setText("当前天梯分："+score);
        if (tvLevel != null) tvLevel.setText(getLevel(score));
    }

    public void addScore(int s) {
        score += s;
        showScore();
        if (score > bestScores) {
            bestScores = score;
            new BestScore(this,hardGame).setBestScode(bestScores);
            showBestScore();
        }
    }

    private String getLevel(int s) {
        if (s <= 500) return levels[0];
        if (s <= 2000) return levels[1];
        if (s <= 5000) return levels[2];
        if (s <= 12000) return levels[3];
        if (s <= 25000) return levels[4];
        if (s <= 40000) return levels[5];
        if (s <= 60000) return levels[6];
        return levels[7];
    }

    private void Cool() {
        mySkill.Cool();
        setCoolImage();
    }

    private void storeRecord() {
        SharedPreferences s = this.getSharedPreferences("currentPosition", MODE_PRIVATE);
        SharedPreferences.Editor editor = s.edit();
        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 4; ++j) {
                editor.putInt("currentBlock" + i + ":" + j, value[i][j]);
            }
            editor.putBoolean("hardGame",hardGame);
        editor.apply();
        s = this.getSharedPreferences("Score", MODE_PRIVATE);
        editor = s.edit();
        editor.putInt("currentScore", score);
        editor.apply();
        new BestScore(this,hardGame).setBestScode(bestScores);
    }

    private void loadRecord() {
        SharedPreferences s = this.getSharedPreferences("currentPosition", MODE_PRIVATE);
        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 4; ++j) {
                value[i][j] = s.getInt("currentBlock" + i + ":" + j, 0);
            }
        mySkill = SkillFactory.CreateSkill(this, s.getInt("SkillUsing", 0), this);
        hardGame = s.getBoolean("hardGame",false);
        s = this.getSharedPreferences("Score", MODE_PRIVATE);
        score = s.getInt("currentScore", 100);
        BestScore BS = new BestScore(this,hardGame);
        bestScores = BS.getBestScode();
    }

    private void startGame() {
        loadRecord();
        num = 0;
        battling = false;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (value[i][j] > 0) num++;
        if (num == 0) Restart();
        setCoolImage();
        check();
        gv.setAll();
    }

    private void setButtons() {
        Button bt = findViewById(R.id.bt_cx);
        Button cl = findViewById(R.id.clear);
        Button hp = findViewById(R.id.help);
        ImageButton us = findViewById(R.id.US);
        sk = findViewById(R.id.Skill);
        if (bt != null) bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Restart();
                showScore();
                gv.setAll();
                setCoolImage();
                playMusic(2);
            }
        });
        if (cl != null) cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBest();
            }
        });
        if (hp != null) hp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUseProp=false;
                final Dialog dialog1 = new Dialog(MainActivity.this);

                View contentView1 = LayoutInflater.from(MainActivity.this).inflate(
                        R.layout.activity_help, null);
                dialog1.setContentView(contentView1);
                dialog1.setTitle("道具");
                WindowManager m = getWindowManager();
                Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
                WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
                p.height = (int) (d.getHeight() * 0.4); // 高度设置为屏幕的0.4
                p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.9
                Window dialogWindow = dialog1.getWindow();
                dialogWindow.setAttributes(p);
                dialog1.show();
                Button p1=contentView1.findViewById(R.id.prop1);
                Button p2=contentView1.findViewById(R.id.prop2);
                p1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Gold g = new Gold(MainActivity.this);
                        if(g.getGold()>=10) {
                            isUseProp = true;
                            dialog1.dismiss();
                        }
                        else
                        {

                            Toast.makeText(getApplicationContext(), "金币不足", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                p2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Gold g = new Gold(MainActivity.this);
                        if(g.getGold()>=5) {
                            isUseProp2 = true;
                            dialog1.dismiss();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "金币不足", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        if (us != null) us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gold g = new Gold(MainActivity.this);
                if (!mySkill.getSkillName().equals("default") && mySkill.getSkillLevel() < 3 && g.buy(mySkill.getPrice()))
                    mySkill.levelUp();
                showGold();
                showGold();
                if (tvPrice != null) {
                    tvPrice.setText(mySkill.getPriceString());
                }
                if (tvIntro != null) {
                    tvIntro.setText(mySkill.getIntroString());
                }
            }
        });
        if (sk != null) {
            sk.setImageResource(mySkill.getImage());
            sk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mySkill.ClickOn();
                    setCoolImage();
                }
            });
            sk.setClickable(true);
        }
    }


    private void setTexts() {
        tvBestScore = findViewById(R.id.maxscore);
        tvScore = findViewById(R.id.tvScore);
        tvLevel = findViewById(R.id.level);
        tvMaxLevel = findViewById(R.id.maxlevel);
        tvIntro = findViewById(R.id.intro);
        tvPrice = findViewById(R.id.Price);
        tvGold = findViewById(R.id.Gold);
        tvSkill = findViewById(R.id.SkillName);
        TextView gameMode = findViewById(R.id.GameMode);
        gameMode.setText(hardGame?"困难模式":"普通模式");
        if (tvSkill != null) tvSkill.setText(mySkill.getSkillName());
        showBestScore();
        if (tvIntro != null) {
            tvIntro.setText(mySkill.getIntroString());
        }
        if (tvPrice != null) {
            tvPrice.setText(mySkill.getPriceString());
        }
        if (tvGold != null) {
            Gold g = new Gold(MainActivity.this);
            tvGold.setText(g.getGold() + "");
        }
    }

    private void setAnimes(final int x, final int y, final int x1, final int y1) {
        int w = gv.getCardWitch();
        TranslateAnimation animation = new TranslateAnimation(0, (x1 - x) * w, 0, (y1 - y) * w);
        animation.setDuration(120);
        animation.setFillAfter(true);
        synchronized (this) {
            if (move) {
                move = false;
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        addRandomNum();
                        gv.setAll();
                        check();
                        move = true;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        }
        animes[x][y] = animation;
    }

    private void check() {
        if (num <= 16) {
                if (num >= 15 && !battling) {
                    switchMusic(3);
                    battling = true;
                } else if (num < 13 && battling) {
                    switchMusic(1);
                    battling = false;
                }
        }
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if (value[x][y] <= 0
                        || (x > 0 && value[x][y] == value[x - 1][y]) ||
                        (x < 3 && value[x][y] == (value[x + 1][y])) ||
                        (y > 0 && value[x][y] == value[x][y - 1]) ||
                        (y < 3 && value[x][y] == value[x][y + 1])) {

                    return;
                }
            }
        }
        showEnd();
    }

    public void Restart() {
        calculateGold();
        Clear();
        num = 0;
        battling = false;
        move = true;
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                value[x][y] = 0;
                shines[x][y] = false;
            }
        }
        clearScore();
        showGold();
        addRandomNum();
        addRandomNum();
        mySkill.init();
        storeRecord();
    }

    public void ReBegin() {
        loadRecord();
        Restart();
        showScore();
        setCoolImage();
        gv.setAll();
        storeRecord();
    }

    public int[][] Export() {
        int[][] values = new int[5][4];
        for (int i = 0; i < 4; ++i)
           System.arraycopy(value[i],0,values[i],0,4);
        values[4][0] = bestScores;
        values[4][1] = score;
        return values;
    }

    private void showEnd()
    {
        playMusic(1);
        mp.pause();
        Restart();
        AlertDialog.Builder b = new AlertDialog.Builder (MainActivity.this);
        b.setTitle("比赛结束").setMessage("你失败了").setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playMusic(2);
                gv.setAll();
                showScore();
                setCoolImage();
                mp.start();
            }
        });
        b.setNegativeButton("返回菜单", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                storeRecord();
                switchMusic(0);
                setContentView(R.layout.activity_menu);
                setMenuButtons();
            }
        });
        b.show();
    }

    public void Load(int[][] values) {
        num = 0;
        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 4; ++j) {
                Log.d(values[i][j] + "", value[i][j] + "");
                shines[i][j] = (values[i][j] > 0 || value[i][j] > 0);
                value[i][j] = values[i][j];
                if (value[i][j] > 0) num++;
            }

        bestScores = values[4][0];
        score = values[4][1];
        gv.SetAll(R.drawable.im_tl);
        storeRecord();
        showScore();
        check();
    }
    private void showBestScore()
    {
        if (tvBestScore != null) tvBestScore.setText("最高天梯分："+bestScores);
        if (tvMaxLevel != null) tvMaxLevel.setText(getLevel(bestScores));
    }
    private void addRandomNum() {
        points.clear();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if (value[x][y] <= 0) {
                    points.add(new Point(x, y));
                }
            }
        }
        if(points.size()==0)
        {
            showEnd();
            return;
        }
        Point p = points.remove((int) (Math.random() * points.size()));
        if(!hardGame)value[p.x][p.y] = (Math.random() > 0.1 ? 2 : 4);
        else
        {
            int s = 2;
            while(Math.random()>0.66)s*=2;
            value[p.x][p.y]=s;
        }
        num++;
        storeRecord();
    }

    public void setCoolImage() {
        int Brightness = -mySkill.getCurrentCool() * 5;
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(new float[]{1, 0, 0, 0, Brightness, 0, 1, 0, 0, Brightness, 0, 0, 1, 0, Brightness, 0, 0, 0, 1, 0});
        ColorMatrixColorFilter cmcf = new ColorMatrixColorFilter(matrix);
        if (sk != null) sk.setColorFilter(cmcf);
    }

    public void Clear() {
        for (int i = 0; i < 4; ++i) for (int j = 0; j < 4; ++j) shines[i][j] = false;
        animes = new Animation[4][4];
        merge = false;
    }

    private void swipeLeft() {
//      Toast.makeText(getContext(), "向左滑动了", 0).show();
        //以下两行for循环实现了一行一行的遍历，在向左滑动的时候
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {

                for (int x1 = x + 1; x1 < 4; x1++) {
                    //animes[x1][y].setDuration(20);
                    //这是在水平上固定了一个格子之后再继续在水平上遍历别的格子，且当格子有数的时候进行以下的操作
                    if (value[x1][y] > 0) {
                        //现在判断被固定的格子有没有数字，如果没有数字就进行以下的操作
                        if (value[x][y] <= 0) {
                            //把与被固定的格子的同一水平上的格子的数字赋给被固定的格子
                            value[x][y] = value[x1][y];
                            //cardsMap[x1][y].moveLine(cardsMap[x1][y].getX(),cardsMap[x][y].getX(),cardsMap[x1][y].getY(),cardsMap[x][y].getY());
                            //把值赋给被固定的格子后自己归零
                            value[x1][y] = 0;
                            setAnimes(x1, y, x, y);
                            //第二层循环，即同一层的不同列退一格继续循环，这样做的原因是为了继续固定这个格子而去检查与它同一水平上的别的格子的内容，因为其他格子是什么个情况还需要继续在第二层进行判断
                            x--;
                            Log.d("x",x+"");
                            //只要有移动就要加随机数
                            merge = true;
                            //animes[x1][y]=new TranslateAnimation(cardsMap[x1][y].getXp(),cardsMap[x1][y].getYp(),cardsMap[x][y].getXp(),cardsMap[x][y].getYp());
                            //animes[x1][y].setDuration(100);

                        } else if (value[x][y] == value[x1][y]) {//这层判断是判断相邻两个数相同的情况
                            value[x][y] *= 2;
                            value[x1][y] = 0;
                            double m = mySkill.getMultiple();
                            addScore((int) (value[x][y] * m));
                            setAnimes(x1, y, x, y);
                            shines[x][y] = m > 2;
                            num--;
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }
    }

    private void swipeUp() {
//      Toast.makeText(getContext(), "向上滑动了", 0).show();
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {

                for (int y1 = y + 1; y1 < 4; y1++) {
                    if (value[x][y1] > 0) {

                        if (value[x][y] <= 0) {
                            value[x][y] = value[x][y1];
                            value[x][y1] = 0;
                            setAnimes(x, y1, x, y);
                            y--;
                            //只要有移动就要加随机数
                            merge = true;
                        } else if (value[x][y] == value[x][y1]) {
                            value[x][y] *= 2;
                            value[x][y1] = 0;
                            double m = mySkill.getMultiple();
                            addScore((int) (value[x][y] * m));
                            setAnimes(x, y1, x, y);
                            shines[x][y] = m > 2;
                            merge = true;
                            num--;
                        }
                        break;
                    }
                }
            }
        }
    }

    private void swipeDown() {
        //Toast.makeText(getContext(), "向下滑动了", 0).show();
        for (int x = 0; x < 4; x++) {
            for (int y = 4 - 1; y >= 0; y--) {

                for (int y1 = y - 1; y1 >= 0; y1--) {
                    if (value[x][y1] > 0) {

                        if (value[x][y] <= 0) {
                            value[x][y] = value[x][y1];
                            value[x][y1] = 0;
                            setAnimes(x, y1, x, y);

                            y++;
                            //只要有移动就要加随机数
                            merge = true;
                        } else if (value[x][y] == value[x][y1]) {
                            value[x][y] *= 2;
                            value[x][y1] = 0;
                            double m = mySkill.getMultiple();
                            addScore((int) (value[x][y] * m));
                            merge = true;
                            setAnimes(x, y1, x, y);
                            shines[x][y] = m > 2;
                            num--;
                        }
                        break;

                    }
                }
            }
        }
    }

    private void swipeRight() {
//      Toast.makeText(getContext(), "向右滑动了", 0).show();
        for (int y = 0; y < 4; y++) {
            for (int x = 4 - 1; x >= 0; x--) {

                for (int x1 = x - 1; x1 >= 0; x1--) {
                    if (value[x1][y] > 0) {

                        if (value[x][y] <= 0) {
                            value[x][y] = value[x1][y];
                            value[x1][y] = 0;
                            setAnimes(x1, y, x, y);
                            x++;
                            merge = true;
                        } else if (value[x][y] == value[x1][y]) {
                            value[x][y] *= 2;
                            value[x1][y] = 0;
                            double m = mySkill.getMultiple();
                            addScore((int) (value[x][y] * m));
                            setAnimes(x1, y, x, y);
                            shines[x][y] = m > 2;
                            merge = true;
                            num--;
                        }
                        break;

                    }
                }
            }
        }
    }

    private void calculateGold() {
        int base = hardGame?55:75;
        Gold g = new Gold(this);
        if (score <= 3000) g.addGold(score / base);
        else if (score <= 6000) g.addGold(score / base + 5);
        else if (score <= 12000) g.addGold(score / base + 10);
        else if (score <= 24000) g.addGold(score / base + 15);
        else if (score <= 40000) g.addGold(score / (base-25) + 20);
        else if (score <= 60000) g.addGold(score / (base-25) + 25);
        else g.addGold(score / (base-50) + 30);
    }

    private void setMenuButtons() {
        contentView = R.layout.activity_menu;
        Button ld = findViewById(R.id.LoadGame);
        Button rs = findViewById(R.id.StartGame);
        Button hs = findViewById(R.id.HeroStore);
        Button hg = findViewById(R.id.HardGame);
        hs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_store);
                setStoreButtons();
            }
        });
        ld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGameView();
                startGame();
                setView();
                switchMusic(1);

            }
        });
        rs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor e = MainActivity.this.getSharedPreferences("currentPosition",MODE_PRIVATE).edit();
                e.putBoolean("hardGame",false);
                e.apply();
                setContentView(R.layout.activity_enter);
                setEnterButtons();
            }
        });
        hg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor e = MainActivity.this.getSharedPreferences("currentPosition",MODE_PRIVATE).edit();
                e.putBoolean("hardGame",true);
                e.apply();
                setContentView(R.layout.activity_enter);
                setEnterButtons();
                switchMusic(2);
            }
        });
    }

    private void setStoreButtons() {
        currentPage = 0;
        final Hero hero = new Hero(this);
        tvStore = findViewById(R.id.StoreGold);
        tvStore.setText("你拥有" + new Gold(this).getGold() + "金币");
        contentView = R.layout.activity_store;
        res = getResources();
        final Button[] buy = new Button[HeroNums];
        Button np = findViewById(R.id.nextPage);
        Button pp = findViewById(R.id.prePage);
        np.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.nextStore();
            }
        });
        pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.preStore();
            }
        });
        for (int i = 0; i < storepages; ++i) {
            int id = res.getIdentifier("store" + i, "id", getPackageName());
            stores[i] = findViewById(id);
        }
        for (int i = 1; i <= HeroNums; ++i) {
            int id = res.getIdentifier("buy" + i, "id", getPackageName());
            buy[i - 1] = findViewById(id);
            if (hero.isbought(i)) {
                buy[i - 1].setText(R.string.Bought);
                buy[i - 1].setClickable(false);
            } else {
                int pr = res.getIdentifier("price" + i, "string", getPackageName());
                buy[i - 1].setText(pr);
            }
            final int p = i;
            buy[p - 1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!hero.isbought(p) && new Gold(MainActivity.this).buy(hero.getPrice(p))) {
                        hero.bought(p);
                        buy[p - 1].setText(R.string.Bought);
                        buy[p - 1].setClickable(false);
                    } else if (hero.isbought(p)) showTextToast("你已购买");
                    else showTextToast("金钱不足");
                }
            });
        }
        LinearLayout store = findViewById(R.id.store);
        store.setOnTouchListener(new View.OnTouchListener() {
            private float startX, startY;//初始的位置
            private float offsetX, offsetY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        offsetX = event.getX() - startX;
                        offsetY = event.getY() - startY;
                        Clear();
                        int limit = 100;
                        if (Math.abs(offsetX) > Math.abs(offsetY)) {//防止斜着
                            if (offsetX < -limit) {
                                Log.d("move", "left");
                                nextStore();

                            } else if (offsetX > limit) {
                                Log.d("move", "right");
                                preStore();
                            }
                            break;
                        }
                }
                return true;
            }
        });

    }

    private void setEnterButtons() {
        currentPage = 0;
        res = getResources();
        final Hero hero = new Hero(this);
        contentView = R.layout.activity_enter;
        ImageView[] use = new ImageView[HeroNums + 1];
        for (int i = 0; i < enterpages; ++i) {
            int id = res.getIdentifier("enter" + i, "id", getPackageName());
            enters[i] = findViewById(id);
        }
        SharedPreferences s = this.getSharedPreferences("currentPosition", this.MODE_PRIVATE);
        final SharedPreferences.Editor e = s.edit();
        Button np = findViewById(R.id.nextPage1);
        Button pp = findViewById(R.id.prePage1);
        np.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.nextEnter();
            }
        });
        pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.preEnter();
            }
        });
        for (int i = 0; i <= HeroNums; i++) {
            final int p = i;
            int id = res.getIdentifier("Hero" + p, "id", getPackageName());
            use[p] = findViewById(id);
            use[p].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hero.isbought(p)) {
                        e.putInt("SkillUsing", p);
                        e.apply();
                        setGameView();
                        ReBegin();
                        setView();
                        switchMusic(1);
                    } else showTextToast("你没有这个英雄");
                }
            });
        }
        LinearLayout enter = findViewById(R.id.enter);
        enter.setOnTouchListener(new View.OnTouchListener() {
            private float startX, startY;//初始的位置
            private float offsetX, offsetY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        offsetX = event.getX() - startX;
                        offsetY = event.getY() - startY;
                        Clear();
                        int limit = 100;
                        if (Math.abs(offsetX) > Math.abs(offsetY)) {//这个是防止斜着化
                            if (offsetX < -limit) {
                                Log.d("move", "left");
                                nextEnter();

                            } else if (offsetX > limit) {
                                Log.d("move", "right");
                                preEnter();
                            }
                            break;
                        }
                }
                return true;
            }
        });
    }

    public void nextStore() {
        if (currentPage == storepages - 1) {
            showTextToast("已经最后一页了");
            return;
        }
        stores[currentPage].setVisibility(View.INVISIBLE);
        stores[++currentPage].setVisibility(View.VISIBLE);
    }

    public void preStore() {
        if (currentPage == 0) {
            showTextToast("已经第一页了");
            return;
        }
        stores[currentPage].setVisibility(View.INVISIBLE);
        stores[--currentPage].setVisibility(View.VISIBLE);
    }

    public void nextEnter() {
        if (currentPage == enterpages - 1) {
            showTextToast("已经最后一页了");
            return;
        }
        enters[currentPage].setVisibility(View.INVISIBLE);
        enters[++currentPage].setVisibility(View.VISIBLE);
    }

    public void preEnter() {
        if (currentPage == 0) {
            showTextToast("已经第一页了");
            return;
        }
        enters[currentPage].setVisibility(View.INVISIBLE);
        enters[--currentPage].setVisibility(View.VISIBLE);
    }

    public int ChangeToAnother(int x, int y)
    {
        if (x >= 4 || y >= 4) return -1;
        int s = value[x][y];
        if (s == 0) {
            showTextToast("这是一个空格子");
            return -1;
        }
        num--;
        Random random = new Random();
        int number = random.nextInt(6)%(5-1+1) + 1;
        value[x][y] = (int)Math.pow(2,number);
        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 4; ++j)
                shines[i][j] = false;
        shines[x][y] = true;
        gv.SetAll(R.drawable.im_lb);
        check();
        return 1;
    }

    public int DestroyBlock(int x, int y, int damage) {
        //Log.v("lagunaBlade",x+":"+y);
        if (x >= 4 || y >= 4) return -1;
        int s = value[x][y];
        if (s > damage) {
            showTextToast("方块过大，无法摧毁");
            return -1;
        }
        if (s == 0) {
            showTextToast("这是一个空格子");
            return -1;
        }
        int p = s;
        int ss = 0;
        while ((p >>= 1) > 1) {
            ss += s;
        }
        num--;
        value[x][y] = 0;
        addScore(ss);
        showTextToast("摧毁成功，获得" + ss + "分");
        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 4; ++j)
                shines[i][j] = false;
        shines[x][y] = true;
        gv.SetAll(R.drawable.im_lb);
        check();
        return ss;
    }

    public boolean ExchangeBlock(Point p1, Point p2, int distance) {
        int x1 = p1.x;
        int x2 = p2.x;
        int y1 = p1.y;
        int y2 = p2.y;
        int v1 = value[x1][y1];
        int v2 = value[x2][y2];
        if (v1 <= 0 || v2 <= 0) {
            showTextToast("不能和空格交换");
            return false;
        }
        if (x1 == x2 && y1 == y2) {
            showTextToast("不能和自己交换");
            return false;
        }
        if (Math.abs(x1 - x2) + Math.abs(y1 - y2) > distance) {
            showTextToast("超出距离限制");
            return false;
        }
        value[x1][y1] = v2;
        value[x2][y2] = v1;
        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 4; ++j)
                shines[i][j] = false;
        shines[x1][y1] = true;
        shines[x2][y2] = true;
        gv.SetAll(R.drawable.im_ns);
        return true;
    }

    public void playMusic(int id) {
        mSoundPoll.play(id, 0.6f, 0.6f, 0, 0, 1);
    }

    public void brighterBlock(Point p) {
        gv.Brighter(p.x, p.y, 40);
    }

    public void resetBlock(Point p) {
        gv.Brighter(p.x, p.y, 0);
    }

    public void switchMusic(int id) {
        try {
            mp.reset();
            mp.setDataSource(fileDirPath + "/" + fileName[id]);
            mp.setLooping(true);
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}