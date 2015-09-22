package com.crazy.guess.music.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import com.crazy.guess.music.data.Constants;

import java.io.IOException;

/**
 * Created by vic_ma on 15/9/21.
 */
public class MusicMediaPlayer {

    //播放音乐player
    private static MediaPlayer mMediaPlayer = null;
    //播放音效player
    private static MediaPlayer mTonePlayer = null;

    /**
     * 播放歌曲
     * @param context
     * @param fileName
     */
    public static void playTheSong(Context context, String fileName){

        if(mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();

        }
        try {

            mMediaPlayer.reset();
            AssetFileDescriptor fd = context.getAssets().openFd(fileName);

            mMediaPlayer.setDataSource(fd.getFileDescriptor(),fd.getStartOffset(),
                                       fd.getLength());
            mMediaPlayer.prepare();
            mMediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放音效
     * @param context
     * @param toneType
     */
    public static void playTheTone(Context context, int toneType){
        playTone(context,Constants.TONES_INFO[toneType]);
    }

    private static void playTone(Context context, String fileName){
        if(mTonePlayer == null){
            mTonePlayer = new MediaPlayer();
        }
        try {
            mTonePlayer.reset();
            AssetFileDescriptor fd = context.getAssets().openFd(fileName);
            mTonePlayer.setDataSource(fd.getFileDescriptor(),fd.getStartOffset(),fd.getLength());
            mTonePlayer.prepare();
            mTonePlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放音效
     */
    public static void stopTheTone(){
        if(mTonePlayer != null){
            mTonePlayer.stop();
        }
    }

    /**
     * 停止播放音乐
     */
    public static void stopTheSong(){
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
        }
    }
}

