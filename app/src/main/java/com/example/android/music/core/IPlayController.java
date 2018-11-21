package com.example.android.music.core;

import com.example.android.music.MusicInfo;

import java.util.List;

/**
 * media player control
 *
 * @author wangchang
 */
public interface IPlayController {

    /**
     * 播放指定位置的音频
     *
     * @param position
     */
    boolean playIndex(int position);

    /**
     * 根据音频id播放
     *
     * @param id
     */
    boolean playById(String id);

    /**
     * 继续播放
     *
     * @return
     */
    boolean rePlay();

    /**
     * 重新播放
     * @return
     */
    boolean holdPlay();

    /**
     * 暂停播放
     *
     * @return
     */
    boolean pause();

    /**
     * 上一首
     *
     * @return
     */
    boolean prev();

    /**
     * 下一首
     *
     * @return
     */
    boolean next();

    /**
     * 获取音频播放位置
     *
     * @return
     */
    int getCurrentPosition();

    /**
     * 获取音频时长(毫秒)
     *
     * @return
     */
    int getDuration();


    /**
     * 播放指定位置
     *
     * @param progress
     * @return
     */
    boolean seekTo(int progress);


    /**
     * 刷新音频数据
     *
     * @param musicList
     */
    void refreshMusicList(List<MusicInfo> musicList);


    /**
     * 预加载
     * @param id
     * @return
     */
    boolean prepare(String id);

    /**
     * 暂停音频
     */
    void stop();

    /**
     * 停止播放
     */
    void exit();


}
