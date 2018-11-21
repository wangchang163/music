package com.example.android.music.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.android.music.IMediaService;
import com.example.android.music.MainActivity;
import com.example.android.music.MusicInfo;
import com.example.android.music.R;
import com.example.android.music.core.IConstants;
import com.example.android.music.core.MusicController;

import java.util.List;

/**
 * 作者    wangchang
 * 时间    2018/11/2 14:38
 * 文件    music
 * 描述
 */
public class MusicService extends Service implements IConstants {

    private MusicController controller;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new ServerStub();

    @Override
    public void onCreate() {
        super.onCreate();
        if (controller == null) {
            controller = MusicController.getInstance(getApplicationContext());
        }
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("前台服务标题")
                .setContentText("前台服务内容")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources()
                        , R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
    }


    private class ServerStub extends IMediaService.Stub {
        @Override
        public boolean play(int pos)  {
            return controller.playIndex(pos);
        }

        @Override
        public boolean playById(String id)  {
            return controller.playById(id);
        }

        @Override
        public boolean rePlay() {
            return controller.rePlay();
        }

        @Override
        public boolean pause() {
            return controller.pause();
        }

        @Override
        public boolean prev() {
            return controller.prev();
        }

        @Override
        public boolean next() {
            return controller.next();
        }

        @Override
        public int duration() {
            return controller.getDuration();
        }

        @Override
        public int position()  {
            return controller.getCurrentPosition();
        }

        @Override
        public boolean seekTo(int progress) {
            return controller.seekTo(progress);
        }

        @Override
        public int getPlayState(){
            return controller.getPlayState();
        }

        @Override
        public int getPlayMode() {
            return controller.getPlayMode();
        }

        @Override
        public void setPlayMode(int mode){
            controller.setPlayMode(mode);
        }

        @Override
        public void sendPlayStateBrocast() {
            controller.sendBroadCast();
        }

        @Override
        public void exit(){
            controller.exit();
        }

        @Override
        public void stop(){
            controller.stop();
        }

        @Override
        public void refreshMusicList(List<MusicInfo> musicList) {
            controller.refreshMusicList(musicList);
        }

        @Override
        public List<MusicInfo> getMusicList(List<MusicInfo> musicList){
            return controller.getMusicList();
        }

        @Override
        public String getCurMusicId()  {
            return controller.getCurMusicId();
        }

        @Override
        public MusicInfo getCurMusic() {
            return controller.getMusicObject();
        }

        @Override
        public void holdPlay() {
            controller.holdPlay();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (controller != null) {
            controller.exit();
        }
    }
}
