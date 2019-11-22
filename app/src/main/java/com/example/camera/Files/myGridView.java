package com.example.camera.Files;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.GridView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class myGridView extends SlidingUpPanelLayout {


    public myGridView(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return false;
    }
}
