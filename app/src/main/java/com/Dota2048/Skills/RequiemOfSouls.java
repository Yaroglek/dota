package com.Dota2048.Skills;

import android.content.Context;

import com.Dota2048.Activity.MainActivity;
import com.Game.Dota2048.test.R;

import java.util.Locale;

public class RequiemOfSouls extends Skill{

    public RequiemOfSouls(Context context, MainActivity ma){
        super(context,"RequiemOfSouls",ma);
        SkillId=1;
        CoolDown=0;
        image= R.drawable.skill_1;
        init();
        Refresh();
    }
    @Override
    public String getSkillName(){return "恩赐解脱";}
    @Override
    public double getMultiple()
    {
        double multiple =  (SkillLevel==0||Math.random()>=0.15)?1.0:(1.2+SkillLevel*1.1);
        if(multiple>2)parent.playSkillVoice();
        return multiple;
    }
    @Override
    public int getVoice()
    {
        return R.raw.voice_attack_1;
    }
    @Override
    protected void initIntroString()
    {
        if(SkillLevel==0)IntroString =  "你没有学习此技能";
        else IntroString="当前等级为"+SkillLevel+"，15%的概率获得"+String.format(Locale.getDefault(),"%.1f",SkillLevel*1.1+1.2)+"倍得分。";
    }
    @Override
    protected void Refresh()
    {
        initPriceString();
        initIntroString();
        clickable=false;
    }
}


