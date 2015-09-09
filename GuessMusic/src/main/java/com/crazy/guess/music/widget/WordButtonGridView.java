package com.crazy.guess.music.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.crazy.guess.music.R;
import com.crazy.guess.music.model.WordButton;
import com.crazy.guess.music.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义控件，用于显示带选框中的文字按钮
 * Created by vic_ma on 15/9/8.
 */
public class WordButtonGridView extends GridView{
    private Context mContext = null;
    //待选文字框集合类
    private List<WordButton> mWordButtons = new ArrayList<WordButton>();
    //自定义待选文字框adapter
    private WordButtonAdapter mAdapter = null;

    public WordButtonGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mAdapter = new WordButtonAdapter();
        setAdapter(mAdapter);
    }

    /**
     * 更新数据
     * @param wordButtons
     */
    public void updateData(List<WordButton> wordButtons){
        this.mWordButtons = wordButtons;
        setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }


    private class WordButtonAdapter extends BaseAdapter{

       @Override
       public int getCount() {
           return mWordButtons.size();
       }

       @Override
       public Object getItem(int i) {
           return mWordButtons.get(i);
       }

       @Override
       public long getItemId(int i) {
           return 0;
       }

       @Override
       public View getView(int postion, View view, ViewGroup viewGroup) {
           //获取每一个WordButton
           WordButton holder;
           if(view == null){
               view = Util.getView(mContext,R.layout.word_button_item);
               holder = mWordButtons.get(postion);
               holder.mButton = (Button)view.findViewById(R.id.item_id);
               view.setTag(holder);
           }else{
               holder = (WordButton) view.getTag();
           }
           holder.mButton.setText(holder.mWordText);
           return view;
       }
   }

}
