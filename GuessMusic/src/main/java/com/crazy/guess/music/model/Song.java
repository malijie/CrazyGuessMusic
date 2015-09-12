package com.crazy.guess.music.model;

/**
 * 歌曲实体类
 * Created by vic_ma on 15/9/11.
 */
public class Song {
    //歌曲文件名称
    public String mFileName;
    //歌曲名称
    public String mSongName;
    //歌曲长度
    public int mLength;

    public String getSongName() {
        return mSongName;
    }

    public void setSongName(String songName) {
        this.mSongName = songName;
    }

    public int getLength() {
        char[] songChars = getSongName().toCharArray();
        return songChars.length;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        this.mFileName = fileName;
    }

    public void setLength(int length) {
        this.mLength = length;
    }

}
