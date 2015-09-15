package com.crazy.guess.music;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.crazy.guess.music.data.Constants;
import com.crazy.guess.music.interfaces.IWordButtonOnClickListener;
import com.crazy.guess.music.model.Song;
import com.crazy.guess.music.model.WordButton;
import com.crazy.guess.music.utils.Logger;
import com.crazy.guess.music.utils.Util;
import com.crazy.guess.music.widget.WordButtonGridView;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "MLJ";
    /**
     * ===============Constants=================
     */
    public static final int OPTIONS_WORDS_SIZE = 24;
    private static final int SELECTED_WORDS_SIZE = 4;

    private static final int CHECK_ANSWER_RIGHT = 0;
    private static final int CHECK_ANSWER_LACK = 1;
    private static final int CHECK_ANSWER_WRONG = 2;
    /**
     * ===============View Widget==============
     */
    //唱片控件
    private ImageView mViewPan = null;
    //播杆控件
    private ImageView mViewBar = null;
    //播放按钮
    private ImageButton mButtonPlay = null;
    //自定义控件 文字待选框
    private WordButtonGridView mWordButtonGridView = null;
    //
    private LinearLayout mLayoutContainer = null;
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
        mLayoutContainer = (LinearLayout)findViewById(R.id.word_select_container);

        mButtonPlay = (ImageButton)findViewById(R.id.pan_button_start);
        mViewPan = (ImageView) findViewById(R.id.pan_img_disc);
        mViewBar = (ImageView) findViewById(R.id.pan_img_bar);

        mWordButtonGridView = (WordButtonGridView) findViewById(R.id.words_gridview);

        mButtonPlay.setOnClickListener(this);

        mWordButtonGridView.setWordButtonOnClickListener(new IWordButtonOnClickListener() {
            @Override
            public void onWordButtonClick(WordButton wordButton) {
                for (int i = 0; i < mSelectButtons.size(); i++) {
                    if (TextUtils.isEmpty(mSelectButtons.get(i).mWordText)) {
                        //已选框出现文字
                        mSelectButtons.get(i).mIndex = wordButton.mIndex;
                        mSelectButtons.get(i).mVisable = true;
                        mSelectButtons.get(i).mButton.setText(wordButton.mWordText);
                        mSelectButtons.get(i).mWordText = wordButton.mWordText;
                        mSelectButtons.get(i).mButton.setTextColor(Color.WHITE);
                        mSelectButtons.get(i).mButton.setVisibility(View.VISIBLE);

                        //待选框文字消失
                        wordButton.mButton.setVisibility(View.INVISIBLE);
                        wordButton.mVisable = false;

                        break;
                    }

                }
                handleTheAnswer();
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
    private int mCurrentStageIndex = 0;

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
            final WordButton wordButton = new WordButton();
            View v =  Util.getView(this,R.layout.word_button_item);
            wordButton.mButton = (Button) v.findViewById(R.id.item_button);
            wordButton.mButton.setBackgroundResource(R.mipmap.game_wordblank);
            wordButton.mButton.setTextColor(Color.WHITE);
            wordButton.mVisable = false;
            //已选框点击事件
            wordButton.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    wordButton.mVisable = false;
                    wordButton.mButton.setText("");
                    wordButton.mWordText = "";

                    mWordButtons.get(wordButton.mIndex).mVisable = true;
                    mWordButtons.get(wordButton.mIndex).mButton.setVisibility(View.VISIBLE);
                }
            });

            selectWords.add(wordButton);
        }

        return selectWords;
    }

    /**
     * 动态设置已选框布局显示
     */
    private void setSelectedWordsLayout(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50,50);
        for(int i=0;i<mSelectButtons.size();i++){
            mLayoutContainer.addView(mSelectButtons.get(i).mButton, params);
        }
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
        //打乱待选框中的文字顺序
        words = Util.getRandomOptionData(words);

        List<WordButton> wordButtons = new ArrayList<WordButton>();
        for(int i=0;i<OPTIONS_WORDS_SIZE;i++){
            WordButton wordButton = new WordButton();
            wordButton.mIndex = i;
            wordButton.mWordText = String.valueOf(words[i]);
            wordButtons.add(wordButton);
        }
        return wordButtons;
    }

    /**
     * 检查当前答案情况
     * @return 答案状态值，0:答案正确，1:答案缺失，2:答案正确
     */
    private int checkTheAnswer(){
        //答案缺失
        for(int i=0;i<mSelectButtons.size();i++){
            if(mSelectButtons.get(i).mWordText.length() == 0){
                return CHECK_ANSWER_LACK;
            }
        }
        //答案错误
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<mSelectButtons.size();i++){
            sb.append(mSelectButtons.get(i).mWordText);
        }
        String songName = getCurrentStageSong(mCurrentStageIndex).getSongName();
        if(!sb.toString().equals(songName)){
            return CHECK_ANSWER_WRONG;
        }
        //答案正确
        return CHECK_ANSWER_RIGHT;

    }

    /**
     * 根据答案状态值
     */
    private void handleTheAnswer(){
        int result = checkTheAnswer();
        switch (result){
            //答案缺失
            case CHECK_ANSWER_LACK:
                Logger.d(TAG, "answer is lack");

                break;
            //答案错误
            case CHECK_ANSWER_WRONG:
                Logger.d(TAG, "answer is wrong");
                //闪烁提示用户
                TimerTask task = new TimerTask() {
                    //标志位，用于交替闪烁
                    boolean mChange = false;
                    //闪烁次数
                    int times = 0;
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(times > 6){
                                    return;
                                }

                                for(int i=0;i<mSelectButtons.size();i++){
                                    if(!mChange){
                                        mSelectButtons.get(i).mButton.setTextColor(Color.RED);
                                    }else{
                                        mSelectButtons.get(i).mButton.setTextColor(Color.WHITE);
                                    }
                                }
                                mChange = !mChange;
                                times ++;
                            }
                        });
                    }
                };

                Timer timer = new Timer();
                timer.schedule(task,1,150);
                break;
            //答案正确
            case CHECK_ANSWER_RIGHT:
                Logger.d(TAG, "answer is right");
                break;
        }
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
