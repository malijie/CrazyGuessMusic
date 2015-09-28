package com.crazy.guess.music.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.crazy.guess.music.R;
import com.crazy.guess.music.data.Constants;
import com.crazy.guess.music.utils.Util;

/**
 * Created by vic_ma on 15/9/26.
 */
public class HomeActivity extends Activity implements View.OnClickListener{
    //当前关卡TextView
    private TextView mTextCurrentIndex = null;
    //总共关卡TextView
    private TextView mTextTotal = null;
    //点击开始按钮
    private ImageButton mButtonStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        initViews();
        initData();

    }

    //初始化控件
    private void initViews() {
        mTextCurrentIndex = (TextView) findViewById(R.id.home_text_current);
        mTextTotal = (TextView) findViewById(R.id.home_text_total);
        mButtonStart = (ImageButton) findViewById(R.id.home_button_start);
        mButtonStart.setOnClickListener(this);
    }

    //初始化数据
    private void initData() {
        //获取当前关卡
        int[] data = Util.loadData(HomeActivity.this);
        int currentIndex = data[Constants.SAVE_DATA_INDEX];
        //获取总共关卡数
        int totalIndex = Constants.SONGS_INFO.length;

        mTextCurrentIndex.setText(String.valueOf(currentIndex + 1));
        mTextTotal.setText(String.valueOf(totalIndex));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.home_button_start:
                Util.startActivity(HomeActivity.this,MainActivity.class);
                break;
        }
    }
}
