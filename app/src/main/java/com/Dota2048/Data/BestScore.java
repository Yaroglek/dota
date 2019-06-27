package com.Dota2048.Data;

import android.content.Context;
import android.content.SharedPreferences;

public class BestScore {
    private SharedPreferences s;
    private boolean hardGame;
    public BestScore(Context context,boolean h){
        s = context.getSharedPreferences("bestscore",context.MODE_PRIVATE);
        hardGame=h;
    }

    public int getBestScode(){
        return s.getInt(hardGame?"besthard":"bestscore",0);
    }
    public void setBestScode(int bestScode){
        SharedPreferences.Editor editor = s.edit();
        editor.putInt(hardGame?"besthard":"bestscore",bestScode);
        editor.apply();
    }
}
