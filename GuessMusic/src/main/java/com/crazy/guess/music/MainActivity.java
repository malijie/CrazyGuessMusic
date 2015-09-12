package com.crazy.guess.music;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crazy.guess.music.data.Constants;
import com.crazy.guess.music.interfaces.IWordButtonOnClickListener;
import com.crazy.guess.music.model.Song;
import com.crazy.guess.music.model.WordButton;
import com.crazy.guess.music.utils.Util;
import com.crazy.guess.music.widget.WordButtonGridView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends Activity implements View.OnClickListener{
    /**
     * ===============Constants=================
     */
    private static final int OPTIONS_WORDS_SIZE = 24;
    private static final int SELECTED_WORDS_SIZE = 4;
    /**
     * ===============view widget==============
     */
    //唱片控件
    private ImageView mViewPan = null;
    //播杆控件
    private ImageView mViewBar = null;
    //播放按钮
    private ImageButton mButtonPlay = null;
    //自定义控件 文字待选框
    private WordButtonGridView mWordButtonGridView = null;
    /**
     * ===============Animations===============
     */
    //唱片动画
    private Animation mPanAnim = null;
    //播杆进入动画
    private Animation mBarInAnim = null;
    //播杆移出动画
    private Animation mBarOutAnim = null;
    //唱片进入线性动画
    private LinearInterpolator mPanInLin = null;
    //播杆进入线性动画
    private LinearInterpolator mBarInLin;
    //播杆移出线性动画
    private LinearInterpolator mBarOutLin;
    /**
     * ==============Data======================
     */
    //标志符，防止用户多次点击
    private boolean isPlaying = false;
    //待选框的数据集合
    private List<WordButton> mWordButtons = null;
    //已选文字框数据集合
    private List<WordButton> mSelectButtons = null;
    //当前歌曲关卡对象
    private Song mCurrentSong = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化动画
        initAnimations();
        //初始化控件
        initViews();
        //初始化待选框中数据
        initCurrentStageData();

    }

    /**
     * 初始化控件
     */
    private void initViews(){
        mButtonPlay = (ImageButton)findViewById(R.id.pan_button_start);
        mViewPan = (ImageView) findViewById(R.id.pan_img_disc);
        mViewBar = (ImageView) findViewById(R.id.pan_img_bar);

        mWordButtonGridView = (WordButtonGridView) findViewById(R.id.words_gridview);

        mButtonPlay.setOnClickListener(this);
        mWordButtonGridView.setWordButtonOnClickListener(new IWordButtonOnClickListener() {
            @Override
            public void onWordButtonClick() {
                Toast.makeText(MainActivity.this, "hello", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 初始化动画
     */
    private void initAnimations() {
        //初始化唱片动画
        mPanAnim = AnimationUtils.loadAnimation(this,R.anim.rotate);
        mPanInLin = new LinearInterpolator();
        mPanAnim.setInterpolator(mPanInLin);

        //初始化播杆进入动画
        mBarInAnim = AnimationUtils.loadAnimation(this,R.anim.rotate_45);
        mBarInLin = new LinearInterpolator();
        mBarInAnim.setInterpolator(mBarInLin);
        mBarInAnim.setFillAfter(true);//动画播放时保持停留状态

        //初始化播杆移出动画
        mBarOutAnim = AnimationUtils.loadAnimation(this,R.anim.rotate_45_d);
        mBarOutLin = new LinearInterpolator();
        mBarOutAnim.setInterpolator(mBarOutLin);
        mBarOutAnim.setFillAfter(true);
        mBarOutAnim.setFillAfter(true);//动画播放时保持停留状态

        //设置唱片动画相关播放顺序
        mPanAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //唱片播放结束时，播放播杆移出动画
                mViewBar.startAnimation(mBarOutAnim);
            }
        });

        //设置播杆进入动画相关播放顺序
        mBarInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //播杆进入动画播放时，唱片动画播放
                mViewPan.startAnimation(mPanAnim);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //设置播杆移出动画相关播放顺序
        mBarOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isPlaying = false;
                mButtonPlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    /**
     * 初始化待选框数据
     */
    private void initCurrentStageData(){
        //生成已选文字框数据
        mSelectButtons = getSelectedWords();
        //设置已选文字框布局
        setSelectedWordsLayout();
        //生成待选框数据
        mWordButtons = getOptionsData();
        //将数据给自定义控件
        mWordButtonGridView.updateData(mWordButtons);

    }

    //当前关卡索引
    private int mCurrentStageIndex = 9;

    /**
     * 获取当前关卡歌曲信息
     * @param mCurrentStageIndex
     * @return
     */
    private Song getCurrentStageSong(int mCurrentStageIndex){
        Song song = new Song();
        String songInfo[] = Constants.SONGS_INFO[mCurrentStageIndex];
        String mSongName = songInfo[Constants.SONG_NAME_INDEX];
        String mFileName = songInfo[Constants.FILE_NAME_INDEX];
        song.setFileName(mFileName);
        song.setSongName(mSongName);
        return song;
    }

    /**
     * 获取当前歌曲长度
     * @return
     */
    private int getCurrentSongLength(){
        mCurrentSong = getCurrentStageSong(mCurrentStageIndex);
        return mCurrentSong.getLength();
    }

    /**
     * 生成已选框文字数据
     * @return
     */
    private List<WordButton> getSelectedWords(){
        //根据当前关卡获取歌曲名称长度
        List<WordButton> selectWords = new ArrayList<WordButton>();
        for(int i=0;i<getCurrentSongLength();i++){
            View v =  Util.getView(this,R.layout.word_button_item);
            WordButton wordButton = new WordButton();
            wordButton.mButton = (Button) v.findViewById(R.id.item_button);
            wordButton.mButton.setBackgroundResource(R.mipmap.game_wordblank);
            wordButton.mButton.setTextColor(Color.WHITE);
            wordButton.mWordText = "美";
            selectWords.add(wordButton);
        }

        return selectWords;
    }

    /**
     * 动态设置已选框布局显示
     */
    private void setSelectedWordsLayout(){
        LinearLayout layoutContainer = (LinearLayout)findViewById(R.id.word_select_container);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(40,40);
        for(int i=0;i<mSelectButtons.size();i++){
            layoutContainer.addView(mSelectButtons.get(i).mButton, params);
        }
    }

    /**
     * 获取当前关卡歌曲名称
     * @return
     */
    private char[] getSongName(){
       return mCurrentSong.getSongName().toCharArray();
    }

    /**
     * 生成待选框数据
     * @return
     */
    private List<WordButton> getOptionsData(){
        //一共24个待选文字（答案 ＋ 干扰文字）
        char[] words = new char[OPTIONS_WORDS_SIZE];
        char[] songName = mCurrentSong.getSongName().toCharArray();
        //获取歌曲答案文字
        for(int i=0;i<songName.length;i++){
            words[i] = songName[i];
        }
        //填充随机干扰文字
        for(int i=songName.length;i<OPTIONS_WORDS_SIZE;i++){
            words[i] =Util.getRandomChar();
        }
        //打乱待选文字框中填充顺序，算法为将歌曲名称的汉字与生成的随机数下标对应的汉字进行交换，交换次数为歌曲名称的长度（既3个字的歌曲名称则需交换3次）
        for(int i=0;i<songName.length;i++){
            Random random = new Random();
            int index =  random.nextInt(OPTIONS_WORDS_SIZE);
            char temp = words[index];
            words[index] = words[i];
            words[i] = temp;
        }

        List<WordButton> wordButtons = new ArrayList<WordButton>();
        //TODO 随机生成数据
        for(int i=0;i<OPTIONS_WORDS_SIZE;i++){
            WordButton wordButton = new WordButton();
            wordButton.mWordText = String.valueOf(words[i]);
            wordButtons.add(wordButton);
        }
        return wordButtons;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //播放
            case R.id.pan_button_start:
                handlePlayStart();
              break;
        }
    }

    /**
     * 启动播放
     */
   private void handlePlayStart(){
       if(mViewBar != null){
           if(!isPlaying){
               isPlaying = true;
               mButtonPlay.setVisibility(View.INVISIBLE);
               mViewBar.startAnimation(mBarInAnim);
           }
       }
   }

    @Override
    protected void onPause() {
        mViewPan.clearAnimation();
        super.onPause();
    }
}
