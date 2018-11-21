package com.example.android.music.util;

import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 作者    wangchang
 * 时间    2018/10/31 10:22
 * 文件    music
 * 描述    音频计时器
 */
public class MusicTimer {

    public final static int REFRESH_PROGRESS_EVENT = 0x100;

    private static final int INTERVAL_TIME = 500;
    private Handler mHandler;
    private Timer mTimer;
    private TimerTask mTimerTask;

    private int what;
    private boolean mTimerStart = false;

    public MusicTimer(Handler handler) {

        this.mHandler = handler;
        this.what = REFRESH_PROGRESS_EVENT;
        mTimer = new Timer();
    }

    public void startTimer() {
        if (mHandler == null || mTimerStart) {
            return;
        }
        mTimerTask = new MyTimerTask();
        mTimer.schedule(mTimerTask, INTERVAL_TIME, INTERVAL_TIME);
        mTimerStart = true;
    }

    public void stopTimer() {
        if (!mTimerStart) {
            return;
        }
        mTimerStart = false;
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mHandler.removeMessages(REFRESH_PROGRESS_EVENT);
            mTimerTask = null;
        }
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (mHandler != null&&mTimerStart) {
                Message msg = mHandler.obtainMessage(what);
                msg.sendToTarget();
            }
        }

    }
}
