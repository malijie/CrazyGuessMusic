package com.crazy.guess.music;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.crazy.guess.music.model.WordButton;
import com.crazy.guess.music.widget.WordButtonGridView;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener{
    //唱片控件
    private ImageView mViewPan = null;
    //播杆控件
    private ImageView mViewBar = null;
    //播放按钮
    private ImageButton mButtonPlay = null;
    //自定义控件 文字待选框
    private WordButtonGridView mWordButtonGridView = null;
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
    //标志符，防止用户多次点击
    private boolean isPlaying = false;
    //待选框的数据集合
    private List<WordButton> mWordButtons = null;



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
        //生成待选框数据
        mWordButtons = getOptionsData();
        //将数据给自定义控件
        mWordButtonGridView.updateData(mWordButtons);

    }

    /**
     * 生成待选框数据
     * @return
     */
    private List<WordButton> getOptionsData(){
        List<WordButton> wordButtons = new ArrayList<WordButton>();
        //TODO 随机生成数据
        for(int i=0;i<24;i++){
            WordButton wordButton = new WordButton();
            wordButton.mWordText = "好";
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
