package com.example.android.music.core;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.music.MusicInfo;
import com.example.android.music.util.LogUtil;
import com.example.android.music.util.MusicTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 作者    wangchang
 * 时间    2018/10/30 15:20
 * 文件    music
 * 描述    音频控制类
 */
public class MusicController implements IPlayController, IConstants, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener {

    private static MusicController musicController;
    private String TAG = MusicController.class.getSimpleName();
    private static MediaPlayer mMediaPlayer;
    private List<MusicInfo> mMusicList = new ArrayList<MusicInfo>();
    private Context mContext;
    private String mCurMusicId;//当前歌曲的id
    private AudioManager audioManager;//音频焦点
    private AtomicBoolean exit = new AtomicBoolean(Boolean.FALSE);
    private Random mRandom;
    private MusicTimer musicTimer;

    /**
     * 播放模式
     */
    private int mPlayMode;
    /**
     * 播放状态
     */
    private int mPlayState;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (mPlayState == IConstants.MPS_PLAYING&&mMediaPlayer!=null) {
                sendBroadCast();
            }
            return false;
        }
    });

    public MusicController(Context context) {
        this.mContext = context;
        initMediaPlayer();
        initTimer();
    }

    private void initTimer() {
        musicTimer = new MusicTimer(handler);
        musicTimer.startTimer();
    }

    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setScreenOnWhilePlaying(true);//设置屏幕常亮
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mPlayMode = MPM_LIST_LOOP_PLAY;//默认播放模式为列表循环
        mPlayState = MPS_INVALID;
        mRandom = new Random();
        mRandom.setSeed(System.currentTimeMillis());
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public static MusicController getInstance(Context context) {
        if (musicController == null) {
            musicController = new MusicController(context);
            return musicController;
        } else {
            return musicController;
        }

    }

    @Override
    public boolean playIndex(int position) {
        if (mMusicList == null || mMusicList.isEmpty() || position > mMusicList.size() - 1) {
            LogUtil.e(TAG, "音频列表为空或数组越界");
            return false;
        }
        if (mPlayState == MPS_NOFILE) {
            return false;
        }
        MusicInfo musicInfo = mMusicList.get(position);
        if (musicInfo == null) {
            return false;
        }
        return playById(musicInfo.audioId);
    }

    @Override
    public boolean playById(String id) {
        if (mPlayState == MPS_PREPARE || TextUtils.isEmpty(id)) {
            return false;
        }

        try {
            if (!TextUtils.isEmpty(mCurMusicId)) {
                if (!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                    mPlayState = MPS_PLAYING;
                    sendBroadCast();
                    getAduioManager();
                } else {
                    pause();
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            prepare(id);
        }
        return true;
    }

    @Override
    public boolean rePlay() {
        if (mPlayState == MPS_PAUSE&&mMediaPlayer!=null) {
            getAduioManager();
            mMediaPlayer.start();
            mPlayState = MPS_PLAYING;
            sendBroadCast();
        }
        if (mPlayState == MPS_INVALID || mPlayState == MPS_NOFILE) {
            LogUtil.e(TAG, "音频无效");
            return false;
        }
        return mPlayState != MPS_PREPARE;
    }

    @Override
    public boolean holdPlay() {
        getAduioManager();
        return prepare(getCurMusicId());
    }

    @Override
    public boolean pause() {
        if (mPlayState != MPS_PLAYING) {
            return false;
        }
        if (mMediaPlayer!=null)
        mMediaPlayer.pause();
        mPlayState = MPS_PAUSE;
        sendBroadCast();
        return true;
    }

    @Override
    public boolean prev() {
        if (mPlayState == MPS_PREPARE) {
            return false;
        }
        if (null == mMusicList || mMusicList.isEmpty()) {
            return true;
        }
        if (TextUtils.isEmpty(mCurMusicId)) {
            prepare(mMusicList.get(0).audioId);
        } else {
            MusicInfo musicInfo = getMusicObject();
            int position = getMusicPosition(musicInfo.audioId);
            if (position == 0) {
                prepare(musicInfo.audioId);
            } else {
                MusicInfo preMusicInfo = mMusicList.get(position - 1);
                if (preMusicInfo != null && !TextUtils.isEmpty(preMusicInfo.audioId)) {
                    prepare(preMusicInfo.audioId);
                } else {
                    pause();
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean next() {
        if (mPlayState == MPS_PREPARE) {
            return false;
        }
        if (null == mMusicList || mMusicList.isEmpty()) {
            return true;
        }
        if (TextUtils.isEmpty(mCurMusicId)) {
            prepare(mMusicList.get(0).audioId);
        } else {
            MusicInfo musicInfo = getMusicObject();
            int position = getMusicPosition(musicInfo.audioId);
            if (position != mMusicList.size() - 1) {
                MusicInfo nextMusicInfo = mMusicList.get(position + 1);
                if (nextMusicInfo != null && !TextUtils.isEmpty(nextMusicInfo.audioId)) {
                    prepare(nextMusicInfo.audioId);
                    return true;
                } else {
                    return false;
                }
            } else {
                pause();
                return false;
            }
        }
        return true;
    }

    @Override
    public int getCurrentPosition() {
        if (mPlayState == MPS_PREPARE) {
            return 0;
        }
        if (mMediaPlayer==null){
            return 0;
        }
        if (mPlayState == MPS_PLAYING || mPlayState == MPS_PAUSE) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int getDuration() {
        if (mPlayState == MPS_INVALID || mPlayState == MPS_NOFILE) {
            return 0;
        }
        if (getPlayState() == MPS_PREPARE) {
            return 0;
        }
        if (mMediaPlayer==null){
            return 0;
        }
        return mMediaPlayer.getDuration();
    }

    public int getPlayState() {
        return mPlayState;
    }

    public int getPlayMode() {
        return mPlayMode;
    }

    public String getCurMusicId() {
        return mCurMusicId;
    }

    public void setPlayMode(int mode) {
        switch (mode) {
            case MPS_NOFILE:
                mPlayState = MPS_NOFILE;
                break;
            case MPM_LIST_LOOP_PLAY:
            case MPM_ORDER_PLAY:
            case MPM_RANDOM_PLAY:
            case MPM_SINGLE_LOOP_PLAY:
                mPlayMode = mode;
                break;
        }
    }

    public List<MusicInfo> getMusicList() {
        return mMusicList;
    }

    @Override
    public boolean seekTo(int progress) {
        if (mPlayState == MPS_INVALID || mPlayState == MPS_NOFILE) {
            return false;
        }
        int pro = reviseSeekValue(progress);
        int time = mMediaPlayer.getDuration();
        int curTime = (int) ((float) pro / 100 * time);
        if (mMediaPlayer!=null){
            mMediaPlayer.seekTo(curTime);
        }

        return true;
    }

    @Override
    public void refreshMusicList(List<MusicInfo> musicList) {
        mMusicList.clear();
        mMusicList.addAll(musicList);
        if (mMusicList.size() == 0) {
            mPlayState = MPS_NOFILE;
            return;
        }
        Log.e("musicList--->",""+musicList.size());
    }

    @Override
    public boolean prepare(String id) {
        if (mPlayState == MPS_PREPARE) {
            return false;
        }
        if (TextUtils.isEmpty(id)) {
            return false;
        }
        if (mMediaPlayer==null){
            return false;
        }
        mMediaPlayer.reset();
        try {
            String url = "";
            for (MusicInfo musicInfo : mMusicList) {
                if (musicInfo.audioId.equals(id)) {
                    url = musicInfo.url;
                }
            }
            if (TextUtils.isEmpty(url)) {
                return false;
            }
            mMediaPlayer.setDataSource(url);
            mPlayState = MPS_PREPARE;
            // 通过异步的方式装载媒体资源
            mMediaPlayer.prepareAsync();
            mMediaPlayer.seekTo(0);
            mMediaPlayer.setOnPreparedListener(this);
            mCurMusicId = id;
        } catch (Exception e) {
            Log.e(TAG, "", e);
            mPlayState = MPS_INVALID;
            MusicInfo musicInfo = null;
            for (MusicInfo music : mMusicList) {
                if (music.audioId.equals(id)) {
                    musicInfo = music;
                }
            }
            if (null != musicInfo) {
                int position = getMusicPosition(musicInfo.audioId);
                if (position < mMusicList.size() - 1) {
                    playById(mMusicList.get(position + 1).audioId);
                } else {
                    playById(mMusicList.get(mMusicList.size() - 1).audioId);
                }

            }
            return false;
        }
        sendBroadCast();
        return true;
    }

    @Override
    public void stop() {
        exit.set(Boolean.TRUE);
        if (mPlayState == MPS_PREPARE) {
            return;
        }
        if (null != mMediaPlayer) {
            mMediaPlayer.stop();
            mPlayState = MPS_NOFILE;
            sendBroadCast();
        }
        exit.set(Boolean.FALSE);
    }

    @Override
    public void exit() {
        if (null != mMediaPlayer) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurMusicId = "";
            mMusicList.clear();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (null == mMusicList || mMusicList.isEmpty()) {
            return;
        }
        if (mPlayState == MPS_NOFILE || mPlayState == MPS_INVALID || mPlayState == MPS_PAUSE) {
            return;
        }
        if (TextUtils.isEmpty(mCurMusicId)) {
            mCurMusicId = mMusicList.get(0).audioId;
        }
        switch (mPlayMode) {
            case MPM_LIST_LOOP_PLAY:
                next();
                break;
            case MPM_ORDER_PLAY:
                if (getMusicPosition(mCurMusicId) != mMusicList.size() - 1) {
                    if (mPlayState == MPS_PREPARE) {
                        return;
                    }
                    next();
                }
                break;
            case MPM_RANDOM_PLAY:
                int index = getRandomIndex();
                if (index >= mMusicList.size()) {
                    index = mMusicList.size() - 1;
                }
                if (index < 0) {
                    index = 0;
                }
                prepare(mMusicList.get(index).audioId);
                break;
            case MPM_SINGLE_LOOP_PLAY:
                playById(mCurMusicId);
                break;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (exit.get()) {
            stop();
        } else {
            startPlay();
        }
    }

    public void sendBroadCast() {
        if (musicTimer != null) {
            if (mPlayState == IConstants.MPS_PLAYING) {
                musicTimer.startTimer();
            } else {
                musicTimer.stopTimer();
            }
        }

        Intent intent = new Intent(IConstants.BROADCAST_NAME);
        intent.putExtra(PLAY_STATE_NAME, mPlayState);
        if (mPlayState != MPS_NOFILE && mMusicList != null && mMusicList.size() > 0 && !TextUtils.isEmpty(mCurMusicId)) {
            MusicInfo musicInfo = getMusicObject();
            if (null != musicInfo) {
                Bundle bundle = new Bundle();
                musicInfo.bufferTime=this.percent;
                musicInfo.progress=getCurrentPosition();
                musicInfo.totalTime=getDuration();
                bundle.putSerializable(IConstants.KEY_MUSIC_DATA, musicInfo);
                intent.putExtra(IConstants.KEY_MUSIC, bundle);
            }
        }
        mContext.sendBroadcast(intent);
    }

    public MusicInfo getMusicObject() {
        if (mMusicList != null && mMusicList.size() > 0 && !TextUtils.isEmpty(mCurMusicId)) {
            for (MusicInfo musicInfo : mMusicList) {
                if (musicInfo != null && !TextUtils.isEmpty(musicInfo.audioId)) {
                    if (musicInfo.audioId.equals(mCurMusicId)) {
                        return musicInfo;
                    }
                }
            }
        }
        return null;
    }

    public int getMusicPosition(String id) {
        if (mMusicList != null && mMusicList.size() > 0) {
            for (int i = 0; i < mMusicList.size(); i++) {
                if (id.equals(mMusicList.get(i).audioId)) {
                    return i;
                }
            }
        }
        return 0;
    }

    private void getAduioManager() {//获取音频焦点让其他音频暂停
        audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    private boolean isPauseFromAudioLoss = false;//是否是因为失去焦点而暂停，如果是则再次获取焦点时继续播放

    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            try {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        pause();
                    }
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        pause();
                        isPauseFromAudioLoss = true;
                    }
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    if (mMediaPlayer != null && isPauseFromAudioLoss && !mMediaPlayer.isPlaying()) {
                        rePlay();
                        isPauseFromAudioLoss = false;
                    }
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "音频焦点异常");
            }
        }
    };

    private int reviseSeekValue(int progress) {
        if (progress < 0) {
            progress = 0;
        } else if (progress > 100) {
            progress = 100;
        }
        return progress;
    }

    private void startPlay() {
        getAduioManager();//不播放，但是重启播放的时候去请求音频焦点
        mMediaPlayer.start();
        mPlayState = MPS_PLAYING;
        sendBroadCast();
    }

    private int getRandomIndex() {
        int size = mMusicList.size();
        if (size == 0) {
            return -1;
        }
        return Math.abs(mRandom.nextInt() % size);
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        return false;
    }

    private int percent;
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        this.percent=percent;
        sendBroadCast();
    }
}
