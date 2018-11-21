package com.example.android.music.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.example.android.music.IMediaService;
import com.example.android.music.MainActivity;
import com.example.android.music.MusicInfo;
import com.example.android.music.R;

import java.util.List;

/**
 * 作者    wangchang
 * 时间    2018/11/6 16:08
 * 文件    music
 * 描述
 */
public class MediaServiceManager {

    private static volatile MediaServiceManager manager;
    private ServiceConnection mConn;
    private IMediaService iMediaService;
    private Context context;

    private MediaServiceManager(Context context) {
        this.context = context.getApplicationContext();
        initConn();
        connectService();
    }

    private void initConn() {
        mConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                iMediaService = IMediaService.Stub.asInterface(service);
                if (iMediaService == null || !service.isBinderAlive()) {
                    Log.e("MediaServiceManager", "连接失败");
                    return;
                }


            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.e("MediaServiceManager", "连接失败");
            }
        };
    }

    public static MediaServiceManager getInstance(Context context) {
        if (manager == null) {
            synchronized (MediaServiceManager.class) {
                if (manager == null) {
                    manager = new MediaServiceManager(context);
                }
            }
        }
        return manager;
    }

    private void connectService() {
        Intent intent = new Intent(context, MusicService.class);
        context.bindService(intent, mConn, Context.BIND_AUTO_CREATE);
    }

    private void stopService() {
        if (iMediaService != null) {
            context.unbindService(mConn);
            try {
                iMediaService.exit();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            iMediaService = null;
            mConn = null;
            context = null;
            manager = null;
        }
    }

    public void pause() {
        if (iMediaService != null) {
            try {
                iMediaService.pause();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean play(int pos) {
        if (iMediaService != null) {
            try {
                return iMediaService.play(pos);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public int getPlayState(){
        if (iMediaService!=null){
            try {
                return iMediaService.getPlayState();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public int getPlayMode(){
        if (iMediaService!=null){
            try {
                return iMediaService.getPlayMode();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }


    //讲数据刷新到里面去
    public void refreshMusicList(List<MusicInfo> musicList) {
        if (musicList != null && iMediaService != null) {
            try {
                iMediaService.refreshMusicList(musicList);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void next() {
        if (iMediaService != null) {
            try {
                iMediaService.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    public void prev() {
        if (iMediaService != null) {
            try {
                iMediaService.prev();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void keepPlay() {
        if (iMediaService != null) {
            try {
                iMediaService.holdPlay();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void exit() {
        if (iMediaService != null) {
            stopService();
        }
    }

    public void seekTo(int mProgress) {
        if (iMediaService != null && mProgress >= 0) {
            try {
                iMediaService.seekTo(mProgress);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void rePlay() {
        if (iMediaService != null) {
            try {
                iMediaService.rePlay();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void playById(String id) {
        if (iMediaService != null) {
            try {
                iMediaService.playById(id);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPlayMode(int playMode) {
        if (iMediaService != null) {
            try {
                iMediaService.setPlayMode(playMode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public String getCurMusicId() {
        if (iMediaService != null) {
            try {
                return iMediaService.getCurMusicId();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return "null";
    }


}
