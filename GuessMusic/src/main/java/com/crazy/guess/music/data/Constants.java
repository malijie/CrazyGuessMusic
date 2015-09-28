package com.crazy.guess.music.data;

/**
 * 常量类
 * Created by vic_ma on 15/9/11.
 */
public class Constants {
    //文件名称索引
    public static final int FILE_NAME_INDEX = 0;
    //歌曲名称索引
    public static final int SONG_NAME_INDEX = 1;
    //音效索引-－取消
    public static final int TONE_CANCEL = 0;
    //音效索引－－金币
    public static final int TONE_COIN = 1;
    //音效索引－－确定
    public static final int TONE_ENTER = 2;
    //保存数据文件名
    public static final String SAVE_DATA_FILE_NAME = "data.dat";
    //下标--当前关卡
    public static final int SAVE_DATA_INDEX = 0;
    //下标--金币
    public static final int SAVE_DATA_COINS = 1;

    //歌曲信息 文件名 ＋ 歌曲名称
    public static final String[][] SONGS_INFO = {
            {"__00000.m4a", "征服"},
            {"__00001.m4a", "童话"},
            {"__00002.m4a", "同桌的你"},
            {"__00003.m4a", "七里香"},
            {"__00004.m4a", "传奇"},
            {"__00005.m4a", "大海"},
            {"__00006.m4a", "后来"},
            {"__00007.m4a", "你的背包"},
            {"__00008.m4a", "再见"},
            {"__00009.m4a", "老男孩"},
            {"__00010.m4a", "龙的传人"},

    };
    //音效
    public static final String[] TONES_INFO = {
        "cancel.mp3","coin.mp3","enter.mp3"
    };
}
