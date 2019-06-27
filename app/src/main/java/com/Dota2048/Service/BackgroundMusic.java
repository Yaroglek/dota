package com.Dota2048.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;

public class BackgroundMusic extends Service {
    private MediaPlayer mediaPlayer;
    private String fileDirPath = android.os.Environment
            .getExternalStorageDirectory().getAbsolutePath()// 得到外部存储卡的数据库的路径名
            + "/Dota2048/data/background";// 我要存储的目录
    private String fileName = "dota2_tutorial_music_explore_001_layer02.mp3";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        if (mediaPlayer == null) {

            // R.raw.mmp是资源文件，MP3格式的
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(fileDirPath + "/" + fileName);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();

            }
        }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mediaPlayer.stop();
    }
}
