package com.crazy.guess.music.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.crazy.guess.music.MainActivity;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * Created by vic_ma on 15/9/8.
 */
public class Util {
    public static View getView(Context context, int resId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = View.inflate(context, resId, null);
        return view;
    }

    /**
     * 生成随机汉字
     * http://www.cnblogs.com/skyivben/archive/2012/10/20/2732484.html
     *
     * @return
     */
    public static char getRandomChar() {
        String str = "";
        int hightPos;
        int lowPos;

        Random random = new Random();

        hightPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));

        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(hightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();

        try {
            str = new String(b, "GBK");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return str.charAt(0);
    }

    /**
     * 将待选框中文字随机排列
     * @return
     */
    public static char[] getRandomOptionData(char[] data){
        //打乱待选文字框中填充顺序，算法为将歌曲名称的汉字与生成的随机数下标对应的汉字进行交换，交换次数为歌曲名称的长度（既3个字的歌曲名称则需交换3次）
        for(int i=0;i<data.length;i++){
            Random random = new Random();
            int index =  random.nextInt(MainActivity.OPTIONS_WORDS_SIZE);
            char temp = data[index];
            data[index] = data[i];
            data[i] = temp;
        }
        return data;
    }


}