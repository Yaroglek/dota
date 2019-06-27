package com.Dota2048.Data;

import android.content.Context;
import android.content.SharedPreferences;

public class Gold {

    private SharedPreferences s;

    public Gold(Context context){
        s = context.getSharedPreferences("gold",context.MODE_PRIVATE);
    }

    public int getGold(){
        return s.getInt("gold",0);
    }
    public void setGold(int bestScode){
        SharedPreferences.Editor editor = s.edit();
        editor.putInt("gold",bestScode);
        editor.apply();
    }
    public void addGold(int add)
    {
        int yourGold = s.getInt("gold",0);
        SharedPreferences.Editor editor = s.edit();
        editor.putInt("gold",yourGold+add);
        editor.apply();
    }
    public boolean buy(int price)
    {
        int yourGold = s.getInt("gold",0);
        if(yourGold<price)return false;
        yourGold-=price;
        setGold(yourGold);
        return true;
    }
}
