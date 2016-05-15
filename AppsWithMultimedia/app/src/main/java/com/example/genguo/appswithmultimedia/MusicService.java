package com.example.genguo.appswithmultimedia;

import android.app.Notification;
import android.app.PendingIntent;
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

    private int status;
    private static final int STATUS_IDLE = 0;
    private static final int STATUS_PLAY = 1;
    private static final int STATUS_PAUSE = 2;

    private int mp_status;
    private static final int MP_IDLE = 0;
    private static final int MP_PREPARED = 1;
    private Notification.Builder notification;

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
    public void onCreate() {
        status = STATUS_IDLE;
        mp_status = MP_IDLE;
        initMediaPlayer();
        String soneName = "no song";
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),0,new Intent(getApplicationContext(),MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setTicker("wgg ticker text");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setOngoing(true);
        builder.setContentIntent(pi);
        builder.setContentText("paused");
        builder.setContentTitle("Kingo Player");
        notification = builder;

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null){
            Log.d("Kingo", "start null");
            status = STATUS_PLAY;
            return START_STICKY;
        }
        Log.d("Kingo","on start command");
        if(intent.getAction().equals(ACTION_PLAY)){
            status = STATUS_PLAY;
        }
        return START_STICKY;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(status == STATUS_PLAY) {
            mp.start();
            notification.setContentText("A little luck");
            startForeground(11, notification.build());
        }
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

    private void changeState(int nextStatus){
        if(mediaPlayer == null) return;
        status = nextStatus;
        switch (nextStatus){
            case STATUS_PLAY:
                if(mp_status == MP_PREPARED){
                    mediaPlayer.start();
                    notification.setContentText("A little luck");
                    startForeground(11, notification.build());
                }
                else
                {
                    mediaPlayer.prepareAsync();
                }
                break;
            case STATUS_IDLE:
                mediaPlayer.stop();
                stopForeground(true);
                mp_status = MP_IDLE;
                break;
            case STATUS_PAUSE:
                stopForeground(true);
                mediaPlayer.pause();
                break;
        }
    }
}
