package com.crazy.guess.music.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by vic_ma on 15/9/8.
 */
public class Util {
    public static View getView(Context context, int resId){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = View.inflate(context,resId,null);
        return view;
    }
}
