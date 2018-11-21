# music
音频开发组件

支持功能：音频播放，进度，缓存进度，播放状态，音频位置，暂停，上一曲，下一曲，继续播放，重新播放，列表循环，顺序播放，随机播放，单曲循环等

![Image text](https://github.com/wangchang163/music/blob/master/image/aa.jpg)

注意事项：

## 1：需要注册服务

      <service android:name=".service.MusicService" />

## 2：注册音频广播，接收音频消息（音频对象，音频状态，音频下标）

## 3：初始化MediaServiceManager，在播放时执行refreshMusicList添加音频列表数据

## 4：IConstants接口根据注释查看相应参数
 
 
