package com.aranga.moodloop.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.aranga.moodloop.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.LogRecord;

/**
 * Created by aakashranga on 1/6/16.
 */
public class MyPlayService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    public static boolean IS_RUNNING = false;

    private final Binder myBinder = new MyLocalBinder();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private int currentPos, previousPos;
    private Intent seekintent, nextsongintent;
    private ArrayList<String> currentList;
    private static final int NOTIFICATION_ID = 1;
    private static boolean setShuffling, isNull;
    int mediaPosition, play_pause_mediaposition, mediaMax, repeat;
    private android.os.Handler handler = new android.os.Handler();
    private static int songEnded;
    public static final String BROADCAST_ACTION = "com.aranga.moodloop.seekprogress";
    public static final String BROADCAST_NEXT_SONG = "com.aranga.moodloop.nextsong";

    @Override
    public void onCreate() {
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        nextsongintent = new Intent(BROADCAST_NEXT_SONG);
        seekintent = new Intent(BROADCAST_ACTION);
        registerReceiver(broadcastReceiverSeekPos, new IntentFilter(First_fragment.BROADCAST_SEEKBAR));
        mediaPlayer.reset();
        Log.d("Service", "created");
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        initNotification();
        Log.d("Service", "Service Instantiated");
        currentPos = intent.getExtras().getInt("Song position");
        Log.d("Service", "Song position received" + currentPos);
        currentList = intent.getExtras().getStringArrayList("Songs List");
        mediaPlayer.reset();
        if (!mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.setDataSource(currentList.get(currentPos));
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        setupHandler();
        IS_RUNNING = true;
        return START_NOT_STICKY;
    }


    public void setCurrentPos(int current) {
        previousPos = currentPos;
        this.currentPos = current;
    }

    public void setSetShuffling(boolean shuffling) {
        setShuffling = shuffling;
    }

    public void setRepeat(int repeatVal) {
        if (repeatVal == 2) {
            if (!mediaPlayer.isLooping()) {
                mediaPlayer.setLooping(true);
            }
        }
        else {
            if (mediaPlayer.isLooping()) {
                mediaPlayer.setLooping(false);
            }
        }
        repeat = repeatVal;
        Log.d("Repeat", "set to " + repeat);
    }









    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
        cancelNotification();
        handler.removeCallbacks(sendUpdatesToUi);
        unregisterReceiver(broadcastReceiverSeekPos);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        handler.removeCallbacks(sendUpdatesToUi);
        changeSong();
        updateActivityUI();
        setupHandler();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playSong();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (!mediaPlayer.isPlaying()) {
            playSong();
        }
    }

    public void playSong() {
        currentPos = getCurrentPosForNowPlaying();
        if (!isNull) {
            if (mediaPlayer.isPlaying() && previousPos != currentPos) {
                mediaPlayer.stop();
                mediaPlayer.reset();

                try {
                    mediaPlayer.setDataSource(currentList.get(currentPos));
                    if (repeat == 2) {
                        mediaPlayer.setLooping(true);
                    }
                    mediaPlayer.prepare();
                    mediaPosition = mediaPlayer.getCurrentPosition();
                    mediaMax = mediaPlayer.getDuration();
                    seekintent.putExtra("counter", String.valueOf(mediaPosition));
                    seekintent.putExtra("mediamax", String.valueOf(mediaMax));
                    seekintent.putExtra("song ended", String.valueOf(songEnded));
                    sendBroadcast(seekintent);
                    isNull = false;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

            }
            else if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                mediaPosition = mediaPlayer.getCurrentPosition();
                mediaMax = mediaPlayer.getDuration();
                seekintent.putExtra("counter", String.valueOf(mediaPosition));
                seekintent.putExtra("mediamax", String.valueOf(mediaMax));
                seekintent.putExtra("song ended", String.valueOf(songEnded));
                sendBroadcast(seekintent);
            }
        }
        else {
            try {
                Log.d("playsong", "entered the currect block");
                mediaPlayer.setDataSource(currentList.get(currentPos));
                Log.d("playsong", "data source set to " + currentList.get(currentPos));
                if (repeat == 2) {
                    mediaPlayer.setLooping(true);
                }
                Log.d("playsong", "calling mediaplayer.prepare");
                mediaPlayer.prepare();
                mediaPosition = mediaPlayer.getCurrentPosition();
                mediaMax = mediaPlayer.getDuration();
                seekintent.putExtra("counter", String.valueOf(mediaPosition));
                seekintent.putExtra("mediamax", String.valueOf(mediaMax));
                seekintent.putExtra("song ended", String.valueOf(songEnded));
                sendBroadcast(seekintent);
                isNull = false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        }
    }

    public void initNotification() {
        /*String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) getSystemService(ns);
        int icon = R.drawable.ic_equalizer_white_24dp;
        CharSequence ticker = "Music currently Playing";
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, ticker, when);
        notification.flags = Notification.FLAG_ONGOING_EVENT;


        notificationManager.notify(NOTIFICATION_ID, notification);*/
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.mood_loop)
                        .setContentTitle("MoodLoop")
                        .setContentText("Playing song").setContentIntent(pendingIntent);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) getSystemService(ns);
        notificationManager.notify(NOTIFICATION_ID, notification);

    }

    public void cancelNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) getSystemService(ns);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public void setupHandler() {
        handler.removeCallbacks(sendUpdatesToUi);
        handler.postDelayed(sendUpdatesToUi, 1000);
    }

    private Runnable sendUpdatesToUi = new Runnable() {
        @Override
        public void run() {
            logMediaPosition();
            handler.postDelayed(this, 1000);
        }
    };

    public void logMediaPosition() {
        if (mediaPlayer.isPlaying()) {
            mediaPosition = mediaPlayer.getCurrentPosition();
            mediaMax = mediaPlayer.getDuration();
            seekintent.putExtra("counter", String.valueOf(mediaPosition));
            seekintent.putExtra("mediamax", String.valueOf(mediaMax));
            seekintent.putExtra("song ended", String.valueOf(songEnded));
            sendBroadcast(seekintent);
        }
    }

    private BroadcastReceiver broadcastReceiverSeekPos = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSeekBarPos(intent);
        }
    };

    public void updateSeekBarPos(Intent intent) {
        int seekPos = intent.getIntExtra("seekpos", 0);
        if (mediaPlayer.isPlaying()) {
            handler.removeCallbacks(sendUpdatesToUi);
            mediaPlayer.seekTo(seekPos);
            setupHandler();
        }
    }

    public void updateMediaPlayerState(int intent) {
        //int play_or_pause = intent.getIntExtra("play_pause", 1);
        if (intent == 1) {
            if (!mediaPlayer.isPlaying()) {
                handler.removeCallbacks(sendUpdatesToUi);
                mediaPlayer.seekTo(play_pause_mediaposition);
                mediaPlayer.start();
                setupHandler();
            }
        }
        else {
            if (mediaPlayer.isPlaying()) {
                play_pause_mediaposition = mediaPlayer.getCurrentPosition();
                play_pause_mediaposition++;
                mediaPlayer.pause();
            }
        }
    }

    public void changeSong() {
        if (repeat == 1) {
            if (currentPos + 1 == currentList.size()) {
                previousPos = currentPos;
                currentPos = 0;
                mediaPlayer.reset();
                isNull = true;
                playSong();
            }
            else {
                previousPos = currentPos;
                currentPos++;
                mediaPlayer.reset();
                isNull = true;
                playSong();
            }

        }
        else if (repeat == 0){
            Log.d("changesong", "entered repeat 0");
            if (currentPos + 1 == currentList.size()) {
                Log.d("changesong", " currentPos:" + currentPos + " currentlistsize: " + currentList.size());
                handler.removeCallbacks(sendUpdatesToUi);
                mediaPlayer.stop();
                mediaPlayer.release();
                stopSelf();
                Log.d("changesong", "stopped self");
            }
            else {
                if (setShuffling) {
                    Log.d("changesong", "entered shuffling mode");
                    previousPos = currentPos;
                    currentPos = 0 + (int)(Math.random() * ((currentList.size() - 0) + 1));
                    mediaPlayer.reset();
                    isNull = true;
                    playSong();
                }
                else {
                    Log.d("changesong", "entered non shuffling mode");
                    if (mediaPlayer.isLooping()) {
                        Log.d("changesong", "mediaplayer is looping");
                    }
                    else {
                        Log.d("changesong", "mediaplayer not looping. should act normally");
                    }
                    previousPos = currentPos;
                    currentPos++;
                    Log.d("changesong", "Pos changed to " +currentPos);
                    mediaPlayer.reset();
                    isNull = true;
                    Log.d("changesong", "mediaplayer has been reset, calling playsong");
                    playSong();
                }
            }
        }
    }

    public void updateActivityUI() {
        nextsongintent.putExtra("currentpos", ""+currentPos);
        sendBroadcast(nextsongintent);
    }

    public int getCurrentPosForNowPlaying() {
        if (setShuffling) {
            currentPos = 0 + (int) (Math.random() * ((currentList.size() - 0) + 1));
        }
        return currentPos;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void seekForward(int seconds) {
        if (mediaPlayer.isPlaying()) {
            if (mediaPlayer.getCurrentPosition() + seconds <= mediaPlayer.getDuration()) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + seconds);
            }
        }
    }

    public void seekBackward(int seconds) {
        if (mediaPlayer.isPlaying()) {
            if (mediaPlayer.getCurrentPosition() - seconds >= 0) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - seconds);
            }
        }
    }
/*
   public void fadeOut() {
        Thread fadeOut = new Thread() {
            @Override
            public void run() {
                int seconds = 5000;
                float vol = 1.0f;
                while (seconds >= 0) {
                    try {
                        sleep(500);
                        mediaPlayer.setVolume(vol - 0.1f, vol - 0.1f);
                        vol -= 0.05f;
                        currentPos -= 500;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public void fadeIn() {
        final android.os.Handler h = new android.os.Handler();
        float leftVol = 0f;
        float rightVol = 0f;
        Runnable increaseVol = new Runnable(){
            @Override
            public void run() {
                    mediaPlayer.setVolume(leftVol, rightVol);
                    if(leftVol < 1.0f){
                        leftVol += .05f;
                        rightVol += .05f;
                        h.postDelayed(increaseVol, 500);
                    }

            }
        };


        h.post(increaseVol);

    }*/


    public class MyLocalBinder extends Binder {
        MyPlayService getService() {
            return MyPlayService.this;
        }
    }


}

