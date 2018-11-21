package com.example.android.music.core;

/**
 * 作者    wangchang
 * 时间    2018/10/30 15:25
 * 文件    music
 * 描述    音频常量
 */
public interface IConstants {

    /**
     * 音乐控制的广播
     */
    String BROADCAST_NAME = "com.music.controller.broadcast";//

    /**
     * 音频key
     */
    String KEY_MUSIC = "KEY_MUSIC";
    //播放状态

    /**
     * 无音乐文件
     */
    int MPS_NOFILE = -1;
    /**
     * 当前音乐文件无效
     */
    int MPS_INVALID = 0;
    /**
     * 准备中
     */
    int MPS_PREPARE = 1;
    /**
     * 播放中
     */
    int MPS_PLAYING = 2;
    /**
     * 暂停
     */
    int MPS_PAUSE = 3;


    // 播放模式
    /**
     * 列表循环
     */
    int MPM_LIST_LOOP_PLAY = 0;
    /**
     * 顺序播放
     */
    int MPM_ORDER_PLAY = 1;
    /**
     * 随机播放
     */
    int MPM_RANDOM_PLAY = 2;
    /**
     * 单曲循环
     */
    int MPM_SINGLE_LOOP_PLAY = 3;

    /**
     * 播放状态
     */
    String PLAY_STATE_NAME = "PLAY_STATE_NAME";
    /**
     * 播放音频下标
     */
    String PLAY_MUSIC_INDEX = "PLAY_MUSIC_INDEX";
    /**
     * 播放音频对象
     */
    String KEY_MUSIC_DATA="KEY_MUSIC_DATA";

}
