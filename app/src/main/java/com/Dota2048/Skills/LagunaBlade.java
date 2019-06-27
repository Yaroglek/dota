package com.Dota2048.Skills;

import android.content.Context;

import com.Dota2048.Activity.MainActivity;
import com.Game.Dota2048.test.R;

public class LagunaBlade extends Skill {
    private int damage;
    public LagunaBlade(Context context,MainActivity ma)
    {
        super(context,"LagunaBlade",ma);
        SkillId=3;
        image= R.drawable.skill_3;
        Refresh();
        CurrentCool=s.getInt("currentCool",CoolDown);
        clickNum=1;
    }
    private void RefreshD()
    {
        switch (SkillLevel)
        {
            case 0: damage=0;break;
            case 1: damage=64;break;
            case 2: damage=256;break;
            case 3: damage=1024;break;
        }
    }
    @Override
    public boolean ClickOn(){
        if(!clickable)return false;
        if(waitForClick)
        {
            cancelWait();
            parent.showTextToast("技能取消");
            return false;
        }
        if(CurrentCool==0){
            waitForClick();
            parent.showTextToast("点击一个方块来摧毁");
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
        int x = clicks.get(0).x;
        int y = clicks.get(0).y;
        if(parent.DestroyBlock(x,y,damage)>=0)
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
        return R.raw.voice_attack_3;
    }
    @Override
    public String getSkillName(){return "神灭斩";}
    @Override
    public void init()
    {
        CurrentCool=0;
    }
    @Override
    protected void Refresh()
    {
        CoolDown=80-10*SkillLevel;
        initPriceString();
        initIntroString();
        clickable=SkillLevel!=0;
    }
    @Override
    protected void initIntroString()
    {
        RefreshD();
        if(SkillLevel==0)IntroString ="你没有学习此技能";
        else IntroString="当前等级为"+SkillLevel+"，造成"+damage+"伤害。冷却时间为"+CoolDown+"步。";
    }
}
