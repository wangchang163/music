// IMediaService.aidl
package com.example.android.music;
import com.example.android.music.MusicInfo;
//import com.example.android.music.MusicInfo;
// Declare any non-default types here with import statements

interface IMediaService {

      boolean play(int pos);
      boolean playById(String id);
      boolean rePlay();
      boolean pause();
  	  boolean prev();
      boolean next();
      int duration();
      int position();
      boolean seekTo(int progress);


      int getPlayState();
      int getPlayMode();
      void setPlayMode(int mode);
      void sendPlayStateBrocast();
      void exit();
      void stop();
      void holdPlay();

//      void updateNotification(in Bitmap bitmap, String title, String name);
//      void cancelNotification();
      MusicInfo getCurMusic();
      void refreshMusicList(in List<MusicInfo> musicList);
      List<MusicInfo> getMusicList(out List<MusicInfo> musicList);
      String getCurMusicId();
}
