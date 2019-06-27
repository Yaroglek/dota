package com.Dota2048.Skills;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;

import com.Dota2048.Activity.MainActivity;
import com.Game.Dota2048.test.R;

import java.util.ArrayList;

public class Skill {
    protected SharedPreferences s;
    protected int SkillLevel;
    protected int Price;
    protected String SkillName;

    public int getSkillId() {
        return SkillId;
    }

    protected int SkillId;
    protected int CoolDown;
    protected int CurrentCool;
    protected int image= R.drawable.lg_0;
    protected int clickNum=0;
    protected String PriceString;
    protected String IntroString;
    protected MainActivity parent;
    protected boolean waitForClick=false;
    protected ArrayList<Point> clicks = new ArrayList<>();
    public boolean clickable=false;
    protected int music;
    public Skill(Context context,String name,MainActivity ma)
    {
        SkillName=name;
        s = context.getSharedPreferences(SkillName,context.MODE_PRIVATE);
        SkillLevel = s.getInt("skillLevel1",0);
        Price = (SkillLevel+1)*(SkillLevel+1)*100;
        parent=ma;
        if(name.equals("default"))Refresh();
    }
    public int getVoice(){
        return 0;
    }
    public void levelUp()
    {
        SkillLevel++;
        Price = (SkillLevel+1)*(SkillLevel+1)*100;
        SharedPreferences.Editor editor = s.edit();
        editor.putInt("skillLevel1",SkillLevel);
        editor.apply();
        Refresh();
        parent.playMusic(3);
    }
    public String getSkillName(){return "无技能";}
    public int getSkillLevel(){return SkillLevel;}
    public int getImage() {
        return image;
    }
    public int getPrice(){return Price;}

    public double getMultiple(){return 1.0;}
    public boolean ClickOn(){
        if(CurrentCool==0){
            CurrentCool=CoolDown;
            return true;
        }
        else parent.showTextToast("技能还在冷却，剩余"+CoolDown+"次移动");
        return false;
    }
    protected void write(String name,int value)
    {
        SharedPreferences.Editor editor = s.edit();
        editor.putInt(name,value);
        editor.apply();
    }
    public void init(){
    }
    protected void Refresh()
    {
        initPriceString();
        initIntroString();
    }
    public String getPriceString()
    {
       return PriceString;
    }
    public String getIntroString()
    {
        return IntroString;
    }
    public void Cool()
    {
        if(CurrentCool>0)CurrentCool--;
        write("currentCool",CurrentCool);
    }
    protected void initPriceString()
    {
        if(SkillName.equals("default"))
        {
            PriceString="不可升级";
            return;
        }
        if(SkillLevel==3)PriceString="技能已升至满级";
        else PriceString="升级需要："+Price+"金币";
    }
    protected void initIntroString()
    {
        IntroString="你没有使用技能";
    }
    public int getCoolDown() {
        return CoolDown;
    }

    public int getCurrentCool() {
        return CurrentCool;
    }
    public void waitForClick(){waitForClick=true;}
    public void cancelWait(){waitForClick=false;}
    public boolean isWait(){return waitForClick;}
    public void addClicks(Point p)
    {
        clicks.add(p);
        parent.brighterBlock(p);
        if(clicks.size()==clickNum)
        {
            cancelWait();
            InvokeSkill();
        }
    }
    public void clearPoints(){
        for(Point p:clicks)
        {
            parent.resetBlock(p);
        }
        clicks.clear();
    }
    public void InvokeSkill(){}
}
