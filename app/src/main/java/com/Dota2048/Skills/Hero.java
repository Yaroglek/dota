package com.Dota2048.Skills;

import android.content.Context;
import android.content.SharedPreferences;

public class Hero {
    public static final int HeroNums = 4;
    public  SharedPreferences s;
    public Hero(Context context)
    {
        s=context.getSharedPreferences("Heroes",Context.MODE_PRIVATE);
    }
    public  int getPrice(int id)
    {
        if(id==0)return 0;
        return 100*(id+1)*(id+1);
    }
    public void bought(int id)
    {
        SharedPreferences.Editor e= s.edit();
        e.putInt("Bought"+id,1);
        e.apply();
    }
    public  boolean hasNone()
    {
        int k = 0;
        for(int i=1;i<=HeroNums;++i) k+=s.getInt("Bought"+i,0);
        return k==0;
    }
    public  boolean isbought(int id)
    {
        if(id==0)return true;
        return s.getInt("Bought"+id, 0) == 1;
    }
}
