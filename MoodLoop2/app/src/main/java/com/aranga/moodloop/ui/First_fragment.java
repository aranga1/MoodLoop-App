package com.aranga.moodloop.ui;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aranga.moodloop.R;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by aakashranga on 12/31/15.
 */
public class First_fragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    MyPlayService myservice;
    boolean isBound;
    Intent serviceIntent;
    Bundle songPositionPassedIn;
    ArrayList<String> pathList;
    ArrayList<MusicFile> currentSongsList;
    MainActivity activityTemp;
    int currentPos;
    LinearLayout layoutMoodloop;
    private static boolean isPlaying;
    Vibrator vibrator;
    RangeSeekBar<Integer> moodLoopBar;
    ImageButton play_pause, next, previous, shuffle, repeat;
    ImageButton endMoodLoop;
    int repeatCount, shuffleCount, sendRepeat, playPauseCount, seekMax, currentSongPositionForMoodloop, moodLoopMin, moodLoopMax;
    private static int songEnded = 0;
    boolean broadcaseRegistered, moodLoopInitialized, nextsongregistered;
    TextView songName, playerInfo, moodLoopMinText, moodLoopMaxText, moodLoopInfo, moodloopSongInfo, currentPosition, totalDuration, moodloopcurrentduration;
    SeekBar seekBarMain;
    ImageView songImage, songImageMoodloop;
    View myView;
    public static final String BROADCAST_SEEKBAR = "com.aranga.moodloop.seekbar";
    Intent intentSeekbar;




    //saving current state of the fragment
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcaseReceiver, new IntentFilter(MyPlayService.BROADCAST_ACTION));
        getActivity().registerReceiver(nextSongReceiver, new IntentFilter(MyPlayService.BROADCAST_NEXT_SONG));
        nextsongregistered = true;
        broadcaseRegistered = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(myView!=null && (ViewGroup)myView.getParent()!=null) {
            ((ViewGroup) myView.getParent()).removeView(myView);
            return myView;
        }
        myView = inflater.inflate(R.layout.activity_player_page, container, false);
        setRetainInstance(true);
        songPositionPassedIn = this.getArguments();
        currentSongsList = new ArrayList<MusicFile>();
        pathList = new ArrayList<String>();
        activityTemp = (MainActivity) this.getActivity();


        for (int i = 0; i < activityTemp.getSongs().size(); i++) {
            pathList.add(activityTemp.getSongs().get(i).getPath());
            currentSongsList.add(activityTemp.getSongs().get(i));
        }


        if (songPositionPassedIn != null) {
            currentPos = songPositionPassedIn.getInt("Song Position");
            String albumname = songPositionPassedIn.getString("albumname");
            if (albumname != null) {
                songListCreator(albumname);
            }
            Log.d("Album Name", ""+ albumname);
            Log.d("Current Position", "this :" + currentPos);
        }
        else {
            currentPos = 0;
        }


        serviceIntent = new Intent(getActivity().getApplicationContext(), MyPlayService.class);
        getActivity().bindService(serviceIntent, myconnection, Context.BIND_AUTO_CREATE);
        intentSeekbar = new Intent(BROADCAST_SEEKBAR);
        vibrator = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        play_pause = (ImageButton) myView.findViewById(R.id.btplaypause);
        next = (ImageButton) myView.findViewById(R.id.btnxt);
        previous = (ImageButton) myView.findViewById(R.id.btprev);
        shuffle = (ImageButton) myView.findViewById(R.id.shuffle);
        repeat = (ImageButton) myView.findViewById(R.id.repeat);
        songName = (TextView) myView.findViewById(R.id.songName);
        songName.setSelected(true);
        playerInfo = (TextView) myView.findViewById(R.id.infoText);
        songImage = (ImageView) myView.findViewById(R.id.songImage);
        seekBarMain = (SeekBar) myView.findViewById(R.id.seekBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            seekBarMain.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        }
        currentPosition = (TextView) myView.findViewById(R.id.current_song_position);
        totalDuration = (TextView) myView.findViewById(R.id.total_song_duration);

        if (songPositionPassedIn != null) {
            setEverythingUp();
            playsong();
            isPlaying = true;
            play_pause.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
            playPauseCount++;
        }
        else {
            setEverythingUp();
            currentPosition.setText("00:00");
            totalDuration.setText("00:00");
            seekBarMain.setProgress(0);
        }


        return myView;
    }

    private BroadcastReceiver broadcaseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    private BroadcastReceiver nextSongReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updatePlayerPageLayout(intent);
        }
    };

    private void updateUI(Intent serviceIntent) {
        String counter = serviceIntent.getStringExtra("counter");
        String mediamax = serviceIntent.getStringExtra("mediamax");
        String strSongEnded = serviceIntent.getStringExtra("song ended");
        songEnded = Integer.parseInt(strSongEnded);
        seekMax = Integer.parseInt(mediamax);
        seekBarMain.setMax(seekMax);
        currentSongPositionForMoodloop = Integer.parseInt(counter);
        String currentTime = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(currentSongPositionForMoodloop), TimeUnit.MILLISECONDS.toSeconds(currentSongPositionForMoodloop) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentSongPositionForMoodloop)));
        String songDuration = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(seekMax), TimeUnit.MILLISECONDS.toSeconds(seekMax) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(seekMax)));
        currentPosition.setText(currentTime);
        totalDuration.setText(songDuration);
        seekBarMain.setProgress(currentSongPositionForMoodloop);
        if (songEnded == 1) {
            play_pause.setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
        }
    }

    public void updatePlayerPageLayout(Intent intent) {
        currentPos = Integer.parseInt(intent.getStringExtra("currentpos"));
        setImage();
        setTextForSongPage();
    }

    public void setEverythingUp() {
        //TextView setup
        songName.setKeyListener(null);
        songName.setTextColor(Color.WHITE);
        playerInfo.setKeyListener(null);
        playerInfo.setTextColor(Color.WHITE);

        //buttons Setup
        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseCount++;
                if (playPauseCount == 1) {
                    playsong();
                    play_pause.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
                } else if (playPauseCount % 2 == 0) {
                    play_pause.setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
                    isPlaying = false;
                    myservice.updateMediaPlayerState(0);
                    /*play_pause_action.putExtra("play_pause", 1);
                    getActivity().sendBroadcast(play_pause_action);*/
                } else {
                    play_pause.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
                    isPlaying = true;
                    myservice.updateMediaPlayerState(1);
                    /*play_pause_action.putExtra("play_pause", 0);
                    getActivity().sendBroadcast(play_pause_action);*/
                    //stopSong();
                }
            }
        });

        play_pause.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                vibrator.vibrate(100);
                if (isPlaying) {
                    callMoodLoop();
                } else {
                    Toast toast = Toast.makeText(getActivity(), "Song should be playing to Initialize MoodLoop", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                return true;
            }


        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Current Song Position", "Current Position = " + currentPos);
                if (currentPos + 1 == pathList.size()) {
                    Log.d("PathList End", "End reached");
                    if (sendRepeat == 1) {
                        currentPos = 0;
                        myservice.setCurrentPos(currentPos);
                        myservice.playSong();
                        currentPos = myservice.getCurrentPos();
                        setImage();
                        setTextForSongPage();
                    }
                } else {
                    currentPos++;
                    myservice.setCurrentPos(currentPos);
                    myservice.playSong();
                    currentPos = myservice.getCurrentPos();
                    /*next_action.putExtra("currentpos", currentPos);
                    getActivity().sendBroadcast(next_action);
                    Log.d("Next Action", "Position sent :" + currentPos);*/
                    setImage();
                    setTextForSongPage();
                }
                play_pause.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
            }
        });

        next.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                myservice.seekForward(3000);
                return true;
            }
        });


        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPos == 0) {
                    Log.d("Entered this", "Current Pos is 0");
                    currentPos = 0;
                } else {
                    currentPos--;
                    myservice.setCurrentPos(currentPos);
                    myservice.playSong();
                    currentPos = myservice.getCurrentPos();
                    setImage();
                    setTextForSongPage();
                }

            }
        });

        previous.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                myservice.seekBackward(3000);
                return true;
            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffleCount++;
                int sendShuffle = shuffleCount % 2;
                if (sendShuffle == 0) {
                    shuffle.setImageResource(R.drawable.ic_shuffle_white_24dp);
                    myservice.setSetShuffling(false);
                }
                else {
                    shuffle.setImageResource(R.drawable.ic_shuffle_red_24dp);
                    myservice.setSetShuffling(true);
                }
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatCount++;
                sendRepeat = repeatCount % 3;
                switch (sendRepeat) {
                    case 0: repeat.setImageResource(R.drawable.ic_repeat_white_24dp);
                        break;
                    case 1: repeat.setImageResource(R.drawable.ic_repeat_red_24dp);
                        break;
                    case 2: repeat.setImageResource(R.drawable.ic_repeat_one_red_24dp);
                }
                myservice.setRepeat(sendRepeat);
                /*repeat_action.putExtra("repeat", sendRep);
                getActivity().sendBroadcast(repeat_action);*/
            }
        });

        seekBarMain.setOnSeekBarChangeListener(this);
        setImage();
        setTextForSongPage();
    }

    public void playsong() {
        serviceIntent.putExtra("Song position", currentPos);
        Log.d("Song Position", "Song Position sent in " + currentPos);
        serviceIntent.putStringArrayListExtra("Songs List", pathList);
        Log.d("Play Song", "Everything Put in");
        try {
            getActivity().startService(serviceIntent);
            Log.d("Service", "started");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void setImage() {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(pathList.get(currentPos));
        byte[] art;
        try {
            art = mmr.getEmbeddedPicture();
            Bitmap image = BitmapFactory.decodeByteArray(art, 0, art.length);
            songImage.setImageBitmap(image);
        } catch (Exception e) {
            songImage.setImageResource(R.drawable.default_album_art_white_cd);
        }
    }

    public void setImageMoodloop() {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(pathList.get(currentPos));
        byte[] art;
        try {
            art = mmr.getEmbeddedPicture();
            Bitmap image = BitmapFactory.decodeByteArray(art, 0, art.length);
            songImageMoodloop.setImageBitmap(image);
        } catch (Exception e) {
            songImageMoodloop.setImageResource(R.drawable.default_album_art_white_cd);
        }
    }

    public void setTextForSongPage() {
        songName.setText(currentSongsList.get(currentPos).getSongTitle());
        playerInfo.setText(currentSongsList.get(currentPos).getAlbumName() + "\n" + currentSongsList.get(currentPos).getArtistName());
    }

    public void callMoodLoop() {
        final Dialog moodLoop = new Dialog(First_fragment.this.getContext());
        WindowManager.LayoutParams lp = moodLoop.getWindow().getAttributes();
        lp.dimAmount=0.0f;
        moodLoop.getWindow().setAttributes(lp);
        moodLoop.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        moodLoop.requestWindowFeature(Window.FEATURE_NO_TITLE);
        moodLoop.setContentView(R.layout.moodloop_dialog);
        moodLoop.show();
        moodLoop.setTitle("Mood Loop");
        moodLoop.setCancelable(false);
        endMoodLoop = (ImageButton) moodLoop.findViewById(R.id.imageButton);
        moodLoopBar = new RangeSeekBar<Integer>(this.getContext());
        layoutMoodloop = (LinearLayout) moodLoop.findViewById(R.id.double_seekbar_holder);
        moodLoopBar.setRangeValues(0, seekMax);
        layoutMoodloop.addView(moodLoopBar);
        moodLoopMinText = (TextView) moodLoop.findViewById(R.id.from_text_view);
        moodLoopMaxText = (TextView) moodLoop.findViewById(R.id.to_text_view);
        moodloopcurrentduration = (TextView) moodLoop.findViewById(R.id.currenttime_moodloop);
        moodLoopInfo = (TextView) moodLoop.findViewById(R.id.moodloop);
        moodloopSongInfo = (TextView) moodLoop.findViewById(R.id.song_title);
        moodloopSongInfo.setKeyListener(null);
        moodloopSongInfo.setSelected(true);
        moodloopSongInfo.setEnabled(true);
        songImageMoodloop = (ImageView) moodLoop.findViewById(R.id.song_image_moodloop);
        setImageMoodloop();
        moodLoopInfo.setKeyListener(null);
        moodloopSongInfo.setText("" + currentSongsList.get(currentPos).getSongTitle() + "     " + currentSongsList.get(currentPos).getAlbumName() + "    " + currentSongsList.get(currentPos).getArtistName());
        moodLoopInfo.setSelected(true);
        moodLoopInfo.setEnabled(true);
        moodLoopMinText.setKeyListener(null);
        moodLoopMinText.setFocusable(false);
        moodLoopMaxText.setFocusable(false);
        moodLoopMaxText.setKeyListener(null);
        moodLoopBar.setNotifyWhileDragging(true);
        moodloopcurrentduration.setKeyListener(null);
        moodLoopBar.setTextAboveThumbsColor(0);
        moodLoopBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                moodLoopInitialized = true;
                moodLoopMin = minValue;
                moodLoopMax = maxValue;

                String minText = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(moodLoopMin), TimeUnit.MILLISECONDS.toSeconds(moodLoopMin) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(moodLoopMin)));

                String maxText = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(moodLoopMax), TimeUnit.MILLISECONDS.toSeconds(moodLoopMax) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(moodLoopMax)));

                moodLoopMinText.setText(minText);
                moodLoopMaxText.setText(maxText);
                intentSeekbar.putExtra("seekpos", moodLoopMin);
                seekBarMain.setProgress(moodLoopMin);
                getActivity().sendBroadcast(intentSeekbar);
                Log.d("MoodLoopInitialized", "" + moodLoopInitialized);
                Log.d("Range Seek Bar", "User is looping from " + minValue + " to " + maxValue);
            }
        });
        endMoodLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodLoop.cancel();
            }
        });
        moodLoop.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                moodLoopInitialized = false;
            }
        });

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String currentTime = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(seekBar.getProgress()), TimeUnit.MILLISECONDS.toSeconds(seekBar.getProgress()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(seekBar.getProgress())));
        if (fromUser) {
            currentPosition.setText(currentTime);
            int seekPos = progress;
            intentSeekbar.putExtra("seekpos", seekPos);
            getActivity().sendBroadcast(intentSeekbar);
        }
        else {
            if (moodLoopInitialized) {
                Log.d("SeekBar", "Progress Changed automatically to" + seekBar.getProgress());
                /*if (seekBar.getProgress() >= (moodLoopMax-6000)) {
                    myservice.fadeOut();
                }*/
                moodloopcurrentduration.setText(currentTime);
                if (seekBar.getProgress() >= moodLoopMax) {
                    int seekPos = moodLoopMin;
                    intentSeekbar.putExtra("seekpos", seekPos);
                    getActivity().sendBroadcast(intentSeekbar);
                    //myservice.fadeIn();
                }
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private ServiceConnection myconnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyPlayService.MyLocalBinder binder = (MyPlayService.MyLocalBinder) service;
            myservice = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcaseReceiver);
        broadcaseRegistered = !broadcaseRegistered;
        getActivity().unregisterReceiver(nextSongReceiver);
        nextsongregistered = !nextsongregistered;
        getActivity().unbindService(myconnection);
    }

    private void songListCreator(String albumname) {
        String[] columns = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST};

        String where = MediaStore.Audio.Media.ALBUM + "=?";

        String whereVal[] = { albumname };

        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        ContentResolver cr = this.getActivity().getContentResolver();

        Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, where, whereVal, sortOrder);
        int count = 0;
        pathList.clear();
        currentSongsList.clear();
        if(cursor != null) {
            count = cursor.getCount();
            if(count > 0) {
                while(cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    MusicFile song = new MusicFile(title, artistName, albumname, path);
                    pathList.add(path);
                    currentSongsList.add(song);
                    // Add code to get more column here
                    // Save to your list here
                }
            }
        }
        Log.d("Path List", "Path List Size =" + pathList.size());
        Log.d("Current List", "Current List size = " + currentSongsList.size());
        cursor.close();
    }












}
