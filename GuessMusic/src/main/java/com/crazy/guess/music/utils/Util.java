package com.crazy.guess.music.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.crazy.guess.music.R;
import com.crazy.guess.music.activity.MainActivity;
import com.crazy.guess.music.data.Constants;
import com.crazy.guess.music.interfaces.ITipOnClickListener;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * Created by vic_ma on 15/9/8.
 */
public class Util {
    public static View getView(Context context, int resId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(resId, null);
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

    /**
     * 跳转到相应的Activity
     * @param context
     * @param desti
     */
    public static void startActivity(Context context, Class desti){
        Intent intent = new Intent();
        intent.setClass(context,desti);
        context.startActivity(intent);

        ((Activity)context).finish();
    }

    /**
     * 弹出提示对话框
     * @param context
     * @param message
     * @param listener
     */
    public static void showTipAlertDialg(final Context context, String message,
                                         final ITipOnClickListener listener){
        //播放音效
        MusicMediaPlayer.playTheTone(context, Constants.TONE_ENTER);

        final View v = getView(context, R.layout.tip_view);

        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogTheme);
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();

        ImageButton okButton = (ImageButton) v.findViewById(R.id.tip_button_ok);
        ImageButton cancelButton = (ImageButton) v.findViewById(R.id.tip_button_cancel);
        TextView messageText = (TextView) v.findViewById(R.id.tip_text_message);
        messageText.setText(message);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicMediaPlayer.playTheTone(context, Constants.TONE_COIN);
                alertDialog.dismiss();
                listener.onClick();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicMediaPlayer.playTheTone(context, Constants.TONE_CANCEL);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

}