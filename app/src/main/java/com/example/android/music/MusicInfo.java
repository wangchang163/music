package com.example.android.music;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 作者    wangchang
 * 时间    2018/10/30 15:13
 * 文件    music
 * 描述    音频对象
 */
public class MusicInfo implements Serializable,Parcelable{

    public String audioId="0";//音频id
    public String audioName="";//音频的名称
    public String size="";//音频大小
    public String url="";//音频链接
    public int progress;//播放进度
    public int totalTime;//音频总时长
    public int bufferTime;//缓冲进度

    public MusicInfo() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.audioId);
        dest.writeString(this.audioName);
        dest.writeString(this.size);
        dest.writeString(this.url);
        dest.writeInt(this.progress);
        dest.writeInt(this.totalTime);
        dest.writeInt(this.bufferTime);
    }

    protected MusicInfo(Parcel in) {
        this.audioId = in.readString();
        this.audioName = in.readString();
        this.size = in.readString();
        this.url = in.readString();
        this.progress = in.readInt();
        this.totalTime = in.readInt();
        this.bufferTime = in.readInt();
    }

    public static final Creator<MusicInfo> CREATOR = new Creator<MusicInfo>() {
        @Override
        public MusicInfo createFromParcel(Parcel source) {
            return new MusicInfo(source);
        }

        @Override
        public MusicInfo[] newArray(int size) {
            return new MusicInfo[size];
        }
    };
}
