package com.Dota2048.Skills;

import android.content.Context;
import android.content.SharedPreferences;

import com.Dota2048.Activity.MainActivity;
import com.Game.Dota2048.test.R;


public class TimeLapse extends Skill {
    private int[][][] values = new int[5][5][4];
    private int step;
    public TimeLapse(Context context,MainActivity ma)
    {
        super(context,"TimeLapse",ma);
        SkillId=2;
        step=0;
        CoolDown=70-10*SkillLevel;
        image=R.drawable.skill_2;
        CurrentCool=s.getInt("currentCool",CoolDown);
        ReadValues();
        Refresh();
    }
    @Override
    public void Cool() {
        super.Cool();
        System.arraycopy(values,1,values,0,4);
        values[4]=parent.Export();
        /*for(int i=0;i<5;++i)
        {
            StringBuilder sb = new StringBuilder();
            for(int j=0;j<4;++j)
            {
                for(int k = 0;k<4;++k) {
                    sb.append(values[i][k][j]);
                    sb.append(" ");
                }
                sb.append("\n");
            }
            Log.d(i+"",sb.toString());

        }*/
        WriteValues();
    }
    private void WriteValues()
    {
        SharedPreferences.Editor e = s.edit();
        for(int i =0;i<5;++i)
            for(int j=0;j<4;++j)
                e.putInt("value"+i+j,values[0][i][j]);
        e.putInt("CurrentCool",CurrentCool);
        e.apply();
    }
    @Override
    public  String getSkillName(){return "时光倒流";}
    private void ReadValues()
    {
        for(int i=0;i<5;++i)
            for(int j=0;j<4;++j)
                for(int k =0;k<5;++k)
                    values[k][i][j]=s.getInt("value"+i+j,0);
        CurrentCool=s.getInt("CurrentCool",CoolDown);
    }
    @Override
    public boolean ClickOn(){
        if(!clickable)return false;
        if(CurrentCool==0){
            CurrentCool=CoolDown;
            parent.Load(values[0]);
            WriteValues();
            parent.playSkillVoice();
            return true;
        }
        else{
            parent.showTextToast("技能还在冷却，剩余"+CurrentCool+"次移动");
            return false;
        }
    }
    @Override
    public void init()
    {
        for(int i=0;i<5;++i)values[i]=parent.Export();
        CurrentCool=0;
        /*for(int i=0;i<5;++i)
        {
            StringBuilder sb = new StringBuilder();
            for(int j=0;j<4;++j)
            {
                for(int k = 0;k<4;++k) {
                    sb.append(values[i][k][j]);
                    sb.append(" ");
                }
                sb.append("\n");
            }
            Log.d(i+"",sb.toString());

        }*/
    }
    @Override
    protected void Refresh()
    {
        CoolDown=70-10*SkillLevel;
        initPriceString();
        initIntroString();
        clickable=SkillLevel!=0;
    }
    @Override
    public int getVoice()
    {
        return R.raw.voice_attack_2;
    }
    @Override
    protected void initIntroString()
    {
        if(SkillLevel==0)IntroString ="你没有学习此技能";
        else IntroString="使你回到5步前的状态。当前等级为"+SkillLevel+"，冷却时间为"+CoolDown+"步。";
    }
}
