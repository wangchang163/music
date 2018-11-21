package com.example.android.music;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.android.music.core.IConstants;
import com.example.android.music.service.MediaServiceManager;
import com.example.android.music.util.LogUtil;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private SeekBar seekbar;
    private MediaServiceManager manager;
    private Button btnPlay, btnPause, btnNext, btnPre, btnReplay, btnStop, btnKeepPlay;
    private TextView tvName, tvTime;
    private List<MusicInfo> list = new ArrayList<>();
    private MusicPlayBroadcast mPlayBroadcast;//广播;
    private int playState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.e("life","onCreate");

        setContentView(R.layout.activity_main);
        seekbar = findViewById(R.id.seekbar);
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnNext = findViewById(R.id.btnNext);
        btnPre = findViewById(R.id.btnPre);
        tvName = findViewById(R.id.tvName);
        btnReplay = findViewById(R.id.btnReplay);
        btnStop = findViewById(R.id.btnStop);
        tvTime = findViewById(R.id.tvTime);
        btnPlay.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnReplay.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        findViewById(R.id.btnClick).setOnClickListener(this);
        findViewById(R.id.btnKeepPlay).setOnClickListener(this);
        findViewById(R.id.mode1).setOnClickListener(this);
        findViewById(R.id.mode2).setOnClickListener(this);
        findViewById(R.id.mode3).setOnClickListener(this);
        findViewById(R.id.mode4).setOnClickListener(this);
        seekbar.setOnSeekBarChangeListener(this);
        list.clear();
        for (int i = 0; i < 10; i++) {
            MusicInfo musicInfo = new MusicInfo();
            musicInfo.audioId = "" + i;
            musicInfo.audioName = "音频" + i;
            musicInfo.url = "http://file.kuyinyun.com/group1/M00/90/B7/rBBGdFPXJNeAM-nhABeMElAM6bY151.mp3";
            list.add(musicInfo);
        }
        registerReciever();
        manager = MediaServiceManager.getInstance(getApplicationContext());
    }

    private void registerReciever() {
        //音频广播
        mPlayBroadcast = new MusicPlayBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(IConstants.BROADCAST_NAME);
        registerReceiver(mPlayBroadcast, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayBroadcast != null) {
            unregisterReceiver(mPlayBroadcast);
        }
        if (manager!=null){
            manager.exit();
        }
        LogUtil.e("life","onDestroy");
    }


    private class MusicPlayBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(IConstants.BROADCAST_NAME)) {

                playState = intent.getIntExtra(IConstants.PLAY_STATE_NAME, IConstants.MPS_NOFILE);
                Bundle bundle = intent.getBundleExtra(IConstants.KEY_MUSIC);
                MusicInfo musicInfo = (MusicInfo) bundle.getSerializable(IConstants.KEY_MUSIC_DATA);
                int music_position = bundle.getInt(IConstants.PLAY_MUSIC_INDEX, 0);
                if (musicInfo != null) {
                    String state = "";
                    if (playState == -1) {
                        state = "无音乐文件";
                    } else if (playState == 0) {
                        state = "音乐文件无效";
                    } else if ((playState == 1)) {
                        state = "准备中";
                    } else if (playState == 2) {
                        state = "播放中";
                        tvTime.setText(musicInfo.progress / 1000 + "/" + musicInfo.totalTime / 1000);
                        seekbar.setProgress((int) ((float) musicInfo.progress / musicInfo.totalTime * 100));
                        seekbar.setSecondaryProgress(musicInfo.bufferTime);
                    } else if (playState == 3) {
                        state = "暂停";
                    } else {
                        state = "准备中";
                    }
                    tvName.setText(musicInfo.audioName + "     " + state + "     位置：" + music_position);
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlay:
                if (manager != null) {
                    manager.refreshMusicList(list);
                    manager.play(0);
                }
                break;
            case R.id.btnPause:
                if (manager!=null){
                    manager.pause();
                }
                break;
            case R.id.btnNext:
                if (manager!=null){
                    manager.next();
                }
                break;
            case R.id.btnPre:
                if (manager!=null){
                    manager.prev();
                }
                break;
            case R.id.btnReplay:
                if (manager!=null){
                    manager.rePlay();
                }
                break;
            case R.id.btnStop:
                if (manager!=null){
                    manager.exit();
                }
                break;
            case R.id.btnKeepPlay:
                if (manager!=null){
                    manager.keepPlay();
                }
                break;
            case R.id.mode1:
                if (manager!=null){
                    manager.setPlayMode(IConstants.MPM_LIST_LOOP_PLAY);
                }

                break;
            case R.id.mode2:
                if (manager!=null){
                    manager.setPlayMode(IConstants.MPM_ORDER_PLAY);
                }

                break;
            case R.id.mode3:
                if (manager!=null){
                    manager.setPlayMode(IConstants.MPM_RANDOM_PLAY);
                }
                break;
            case R.id.mode4:
                if (manager!=null){
                    manager.setPlayMode(IConstants.MPM_SINGLE_LOOP_PLAY);
                }
                break;
            case R.id.btnClick:
                startActivity(new Intent(this, Main2Activity.class));
                break;

        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        manager.pause();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (manager!=null){
            manager.seekTo(seekBar.getProgress());
            if (playState == IConstants.MPS_PAUSE) {
                manager.rePlay();
            } else {
                manager.playById(manager.getCurMusicId());
            }
        }
    }
}
