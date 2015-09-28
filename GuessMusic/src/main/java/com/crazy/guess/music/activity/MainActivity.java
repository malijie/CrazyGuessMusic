package com.crazy.guess.music.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.crazy.guess.music.R;
import com.crazy.guess.music.data.Constants;
import com.crazy.guess.music.interfaces.ITipOnClickListener;
import com.crazy.guess.music.interfaces.IWordButtonOnClickListener;
import com.crazy.guess.music.model.Song;
import com.crazy.guess.music.model.WordButton;
import com.crazy.guess.music.utils.Logger;
import com.crazy.guess.music.utils.MusicMediaPlayer;
import com.crazy.guess.music.utils.Util;
import com.crazy.guess.music.widget.WordButtonGridView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "MLJ";
    /**
     * ===============Constants=================
     */
    //待选框汉字数量
    public static final int OPTIONS_WORDS_SIZE = 24;
    //已选框汉字数量
    private static final int SELECTED_WORDS_SIZE = 4;
    //当前答案状态——正确
    private static final int CHECK_ANSWER_RIGHT = 0;
    //当前答案状态——缺失
    private static final int CHECK_ANSWER_LACK = 1;
    //当前答案状态——错误
    public static final int CHECK_ANSWER_WRONG = 2;
    //弹出对话框类型——提示一个汉字
    public static final int DIALOG_COINS_TIP = 0;
    //弹出对话框类型——删除一个汉字
    public static final int DIALOG_COINS_DELETE = 1;
    //弹出对话框类型——金币不够
    public static final int DIALOG_COINS_LACK = 2;
    //弹出对话框类型—退出
    public static final int DIALOG_EXIT = 3;

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
    //已选框容器layout
    private LinearLayout mLayoutContainer = null;
    //过关界面
    private LinearLayout mPassLayoutView = null;
    //金币数量
    private TextView mTextCurrentCoins = null;
    //当前关卡
    private TextView mTextCurrentLevel = null;
    //删除一个错误答案按钮
    private ImageButton mButtonDelete = null;
    //提示一个正确答案按钮
    private ImageButton mButtonTip = null;
    //分享按钮
    private ImageButton mButtonShare = null;
    //增加金币按钮
    private ImageButton mButtoAddCoins= null;
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
        //读取数据
        loadData(MainActivity.this);
        //初始化动画
        initAnimations();
        //初始化控件
        initViews();
        //初始化待选框中数据
        initCurrentStageData();
    }

    /**
     * 读取数据
     */
    private void loadData(Context context){
        int data[] = Util.loadData(context);
        mCurrentStageIndex = data[Constants.SAVE_DATA_INDEX];
        mCurrentCoins = data[Constants.SAVE_DATA_COINS];
    }

    /**
     * 初始化控件
     */
    private void initViews(){
        mLayoutContainer = (LinearLayout)findViewById(R.id.word_select_container);

        mButtonPlay = (ImageButton)findViewById(R.id.pan_button_start);
        mViewPan = (ImageView) findViewById(R.id.pan_img_disc);
        mViewBar = (ImageView) findViewById(R.id.pan_img_bar);
        mTextCurrentCoins = (TextView) findViewById(R.id.topbar_text_coin_count);
        mTextCurrentLevel = (TextView) findViewById(R.id.float_text_level);

        mButtonShare = (ImageButton) findViewById(R.id.float_button_share);
        mButtonDelete = (ImageButton) findViewById(R.id.float_button_delete);
        mButtonTip = (ImageButton) findViewById(R.id.float_button_tip);
        mButtoAddCoins = (ImageButton) findViewById(R.id.topbar_button_addcoin);

        mWordButtonGridView = (WordButtonGridView) findViewById(R.id.words_gridview);

        mButtonPlay.setOnClickListener(this);
        mButtoAddCoins.setOnClickListener(this);

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
                //处理答案状态
                handleTheAnswer();

            }
        });
        //处理金币事件
        handleCoinEvent();
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
        //设置当前关卡
        setCurrentLevel(mCurrentStageIndex + 1);
        //进入自动播放
        handlePlayStart();
        //设置浮动按钮可点击
        setFloatButtonsClick();

    }

    private void setCurrentLevel(int currentLevel){
        mTextCurrentLevel.setText(String.valueOf(currentLevel));
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
     * 动态设置已选框布局显示
     */
    private void setSelectedWordsLayout(){
        //清除之前添加的所有view
        mLayoutContainer.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(180,180);
        for(int i=0;i<mSelectButtons.size();i++){
            mLayoutContainer.addView(mSelectButtons.get(i).mButton, params);
        }
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
                                //文字交替闪烁
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
                handlePassEvent();
                break;
        }
    }

    /**
     * 处理过关逻辑
     */
    private void handlePassEvent(){
        //停止播放音乐
        MusicMediaPlayer.stopTheSong();
        //清除动画
        clearAnimations();
        //浮动按钮不可点
        setFloatButtonsUnClick();

        mPassLayoutView = (LinearLayout)findViewById(R.id.pass_view);
        mPassLayoutView.setVisibility(View.VISIBLE);

        initPassViews();
    }

    /**
     * 处理金币逻辑
     */
    private void handleCoinEvent(){
        handleDeleteWordEvent();
        handleTipWordEvent();
    }

    /**
     * 初始化过界面控件
     */
    private void initPassViews() {
        TextView mTextLevel = (TextView) findViewById(R.id.pass_text_level);
        TextView mTextSong = (TextView) findViewById(R.id.pass_text_song);

        mTextLevel.setText(String.valueOf(mCurrentStageIndex + 1));
        mTextSong.setText(mCurrentSong.getSongName());

        ImageButton mButtonNext = (ImageButton) findViewById(R.id.pass_button_next);
        ImageButton mButtonWXShare = (ImageButton) findViewById(R.id.pass_button_share);

        mButtonNext.setOnClickListener(this);
        mButtonWXShare.setOnClickListener(this);
    }

    //当前金币数量
    private int mCurrentCoins;
    /**
     * 处理删除干扰选项事件
     */
    private void handleDeleteWordEvent() {
        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(DIALOG_COINS_DELETE);
            }
        });
    }

    /**
     * 删除一个单词事件
     */
    private void deleteOneWordEvent(){
        int needCoins = getDeleteOneWordCoins();
        if (consumeCoins(needCoins)) {
            //删除一个文字，1. 金币数量减少，2. 对应的WordButton消失
            mTextCurrentCoins.setText(mCurrentCoins + "");
            setWordButtonInVisible();

        } else {
            //金币不够减，弹出对话框
            showAlertDialog(DIALOG_COINS_LACK);
        }
    }

    /**
     * 使一个WordButton消失
     */
    private void setWordButtonInVisible(){
        //找出当前文字是否为答案，如果不是答案则将其WordButton隐藏，否则继续需要下一个
        WordButton buf = getNoAnswerWordButton();
        buf.mVisable = false;
        buf.mButton.setVisibility(View.INVISIBLE);
    }

    /**
     * 获取一个非正确答案的WordButton
     * @return
     */
    private WordButton getNoAnswerWordButton() {
        WordButton deleteButton ;
        char[] answer =  mCurrentSong.getSongName().toCharArray();
        while(true){
            for(int i=0;i<mWordButtons.size();i++){
                Random random = new Random();
                int index = random.nextInt(OPTIONS_WORDS_SIZE);
                for(int j=0;j<answer.length;j++){
                    //选中的文字依次与歌曲名称比较且当前文字可见
                    if(!mCurrentSong.getSongName().contains(mWordButtons.get(index).mWordText)
                            && mWordButtons.get(index).mVisable == true){
                        deleteButton = mWordButtons.get(index);
                        return deleteButton;
                    }
                }

            }
        }

    }

    /**
     * 获取删除一个文字需要的金币数
     * @return
     */
    private int getDeleteOneWordCoins(){
        return getResources().getInteger(R.integer.delete_value);
    }

    /**
     * 减去金币
     * @param coins 删除一个字需要金币
     * @return
     */
    private boolean consumeCoins(int coins) {
        mCurrentCoins = Integer.valueOf(mTextCurrentCoins.getText() + "");
        //金币不够减
        if(mCurrentCoins - coins < 0){
            return false;
        }else{
           //金币够减
            mCurrentCoins -= coins;
            return true;
        }
    }

    /**
     * 获取当前金币数量
     * @return
     */
    private int getCurrentCoins(){
        mCurrentCoins = Integer.parseInt(mTextCurrentCoins.getText() + "");
        return mCurrentCoins;
    }

    /**
     * 处理提示一个答案事件
     */
    private void handleTipWordEvent(){
        mButtonTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(DIALOG_COINS_TIP);
            }
        });
    }

    /**
     * 提示一个正确答案事件
     */
    private void tipOneAnswerEvent(){
        int needCoins = getTipOneWordCoins();
        mCurrentCoins = getCurrentCoins();
        if(mCurrentCoins-needCoins>0){
            //已选框中是否被占满
            if(!checkSelectionWordsIsFull()){
                //待选框中，答案对应的按钮消失
                WordButton buf = findOneAnswerButton();
                if(buf == null){
                    //后面的答案在前面的已选框中,清除已选框，重新提示一个答案
                    Toast.makeText(MainActivity.this,"一个答案已被选中",Toast.LENGTH_LONG).show();
                    return;

                }
                buf.mVisable = false;
                buf.mButton.setVisibility(View.INVISIBLE);
                //已选框中，答案对应按钮出现
                for(int i=0;i<SELECTED_WORDS_SIZE;i++){
                    WordButton answerButton = mSelectButtons.get(i);
                    if(answerButton.mVisable == false){
                        answerButton.mWordText = buf.mWordText;
                        answerButton.mVisable = true;
                        answerButton.mIndex = buf.mIndex;
                        answerButton.mButton.setText(buf.mWordText);
                        answerButton.mButton.setTextColor(Color.WHITE);
                        break;
                    }
                }
                //减去金币
                consumeCoins(needCoins);
                mTextCurrentCoins.setText(mCurrentCoins + "");
            }
            //已选框被占满，检查答案
            handleTheAnswer();

        }else{
            //金币不够减，显示提示对话框
            showAlertDialog(DIALOG_COINS_LACK);
        }
    }

    /**
     * 检查已选框是否被占满
     * @return
     */
    private boolean checkSelectionWordsIsFull() {
        for(int i=0;i<mSelectButtons.size();i++){
            if(mSelectButtons.get(i).mVisable == false){
                return false;
            }
        }
        return true;
    }

    /**
     * 从待选框里找出一个正确答案
     * @return
     */
    private WordButton findOneAnswerButton() {
        char[] answer = mCurrentSong.getSongName().toCharArray();
        String answerWord = "";

        for(int i=0;i<mSelectButtons.size();i++){
            if(mSelectButtons.get(i).mWordText.length() == 0){
                answerWord = String.valueOf(answer[i]);
                break;
            }
        }

        for(int j=0;j<OPTIONS_WORDS_SIZE;j++){
            if(answerWord.equals(mWordButtons.get(j).mWordText) && mWordButtons.get(j).mVisable == true){
                return mWordButtons.get(j);
            }
        }

        return null;
    }

    /**
     * 获取提示一个正确答案需要消耗的金币
     * @return
     */
    private int getTipOneWordCoins(){
        return getResources().getInteger(R.integer.tip_value);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //播放
            case R.id.pan_button_start:
                handlePlayStart();
              break;
            //下一关
            case R.id.pass_button_next:
                if(judgeIsCompleteGame()){
                    //已通关
                    Util.startActivity(MainActivity.this,CompleteActivity.class);
                }else{
                    mCurrentStageIndex++;
                    clearAnimations();
                    mPassLayoutView.setVisibility(View.INVISIBLE);
                    initCurrentStageData();
                }
                break;
            //增加金币
            case R.id.topbar_button_addcoin:
                goToShopActivity();
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
               //播放音乐
               MusicMediaPlayer.playTheSong(MainActivity.this, mCurrentSong.getFileName());
           }
       }
   }


    /**
     * 通过id类型弹出提示对话框
      * @param id
     */
   private void showAlertDialog(int id){
       switch (id){
           case DIALOG_COINS_DELETE:
               Util.showTipAlertDialg(DIALOG_COINS_DELETE,this, "花费30金币删除一个错误答案？", new ITipOnClickListener() {
                   @Override
                   public void onClick() {
                       deleteOneWordEvent();
                   }
               });
               break;
           case DIALOG_COINS_TIP:
               Util.showTipAlertDialg(DIALOG_COINS_TIP,this, "花费90金币提示一个正确答案？", new ITipOnClickListener() {
                   @Override
                   public void onClick() {
                    tipOneAnswerEvent();
                   }
               });
               break;
           case DIALOG_COINS_LACK:
               Util.showTipAlertDialg(DIALOG_COINS_LACK,this, "金币不够了，到商城补充？", new ITipOnClickListener() {
                   @Override
                   public void onClick() {
                       goToShopActivity();
                   }
               });

       }
   }

    @Override
    public void onBackPressed() {
        Util.showTipAlertDialg(DIALOG_EXIT,this, "确定要退出吗？", new ITipOnClickListener() {
            @Override
            public void onClick() {
                MainActivity.this.finish();
            }
        });
    }

    /**
     * 判断是否通关
     * @return
     */
    private boolean judgeIsCompleteGame(){
        return mCurrentStageIndex == Constants.SONGS_INFO.length - 1 ? true : false;
    }

    /**
     * 清除动画效果
     */
    private void clearAnimations(){
        mViewPan.clearAnimation();
        mViewBar.clearAnimation();
    }

    /**
     * 提示一个正确答案，去掉一个干扰答案，以及分享按钮不可点击
     */
    private void setFloatButtonsUnClick(){
        mButtonDelete.setClickable(false);
        mButtonTip.setClickable(false);
        mButtonPlay.setClickable(false);
    }

    /**
     * 提示一个正确答案，去掉一个干扰答案，以及分享按钮可点击
     */
    private void setFloatButtonsClick(){
        mButtonDelete.setClickable(true);
        mButtonTip.setClickable(true);
        mButtonPlay.setClickable(true);
    }

    /**
     * 去到购物商城
     */
    private void goToShopActivity(){
        Intent intent = new Intent(MainActivity.this,ShopActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        //保存数据
        Util.saveData(MainActivity.this,mCurrentStageIndex,mCurrentCoins);
        //清除动画效果
        clearAnimations();
        //停止播放音乐
        MusicMediaPlayer.stopTheSong();
        super.onPause();
    }

    @Override
    protected void onStop() {
        MusicMediaPlayer.stopTheSong();
        super.onStop();
    }
}
