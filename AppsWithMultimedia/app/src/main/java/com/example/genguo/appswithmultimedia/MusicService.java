package com.example.genguo.appswithmultimedia;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * Created by genguo on 5/7/16.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener{
    private MediaPlayer mediaPlayer;
    public static final String ACTION_PLAY = "wgg.PLAY";
    private static final String [] URLS = {"http://genguowang.github.io/music/little_lucky.mp3"
            ,"http://genguowang.github.io/music/bu_jiang_jiu.mp3"};

    private void initMediaPlayer(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(URLS[0]));
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
        }
        catch (IOException e){
            Log.e("Kingo", e.getMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null){
            Log.d("Kingo", "start null");
            initMediaPlayer();
            return START_STICKY;
        }
        Log.d("Kingo","on start command");
        if(intent.getAction().equals(ACTION_PLAY)){
            initMediaPlayer();
        }
        return START_STICKY;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d("Kingo","mp error");
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
