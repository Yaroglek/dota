package com.Dota2048.Skills;

import android.content.Context;
import android.graphics.Point;

import com.Dota2048.Activity.MainActivity;
import com.Game.Dota2048.test.R;

public class NetherSwap extends Skill {
    private int distance;
    public NetherSwap(Context context, MainActivity ma)
    {
        super(context,"NetherSwap",ma);
        SkillId=4;
        image= R.drawable.skill_4;
        CoolDown=45;
        Refresh();
        CurrentCool=s.getInt("currentCool",CoolDown);
        clickNum=2;
    }
    private void RefreshD()
    {
        switch (SkillLevel)
        {
            case 0: distance=0;break;
            case 1: distance=1;break;
            case 2: distance=2;break;
            case 3: distance=4;break;
        }
    }
    @Override
    public boolean ClickOn(){
        if(!clickable)return false;
        if(waitForClick)
        {
            cancelWait();
            parent.showTextToast("技能取消");
            clearPoints();
            return false;
        }
        if(CurrentCool==0){
            waitForClick();
            parent.showTextToast("点击两个方块来交换");
            return true;
        }
        else{
            parent.showTextToast("技能还在冷却，剩余"+CurrentCool+"次移动");
            return false;
        }
    }
    @Override
    public void InvokeSkill()
    {
        Point p1 = clicks.get(0);
        Point p2 = clicks.get(1);
        if(parent.ExchangeBlock(p1,p2,distance))
        {
            CurrentCool=CoolDown;
            parent.setCoolImage();
            write("currentCool",CurrentCool);
            parent.playSkillVoice();
        }
        clearPoints();
    }
    @Override
    public int getVoice()
    {
        return R.raw.voice_attack_4;
    }
    @Override
    public String getSkillName(){return "移形换位";}
    @Override
    public void init()
    {
        CurrentCool=0;
    }
    @Override
    protected void Refresh()
    {
        RefreshD();
        initPriceString();
        initIntroString();
        clickable=SkillLevel!=0;
    }
    @Override
    protected void initIntroString()
    {
        RefreshD();
        if(SkillLevel==0)IntroString ="你没有学习此技能";
        else IntroString="当前等级为"+SkillLevel+"，交换曼哈顿距离"+distance+"以内的两块。冷却时间为"+CoolDown+"步。";
    }
}
