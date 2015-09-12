package com.crazy.guess.music.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

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


}