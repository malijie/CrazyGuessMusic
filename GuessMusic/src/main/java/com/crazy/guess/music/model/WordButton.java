package com.crazy.guess.music.model;

import android.widget.Button;

/**
 * 按钮实体类
 * Created by vic_ma on 15/9/8.
 */
public class WordButton {
    //按钮索引
    public int mIndex;
    //按钮文字
    public String mWordText;
    //是否可见
    public boolean mVisable;
    //按钮Button
    public Button mButton;

    public WordButton(){
        this.mWordText = "";
        this.mVisable = true;
    }
}
