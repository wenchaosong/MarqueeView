package com.test;

import android.app.Activity;
import android.os.Bundle;

import com.ms.marquee.MarqueeView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private MarqueeView mMarqueeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMarqueeView = findViewById(R.id.marquee);

        List<String> info = new ArrayList<>();
        info.add("1.这是第一条测试的文字");
        info.add("2.这是第二条测试的文字");
        info.add("3.这是第三条测试的文字");
        info.add("4.这是第四条测试的文字");
        info.add("5.这是第五条测试的文字");
        mMarqueeView.startWithList(info);

    }

    //如果你需要考虑更好的体验，可以这么操作
    @Override
    protected void onStart() {
        super.onStart();
        //开始轮播
        if (mMarqueeView != null) {
            mMarqueeView.showNext();
            mMarqueeView.startFlipping();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //结束轮播
        if (mMarqueeView != null)
            mMarqueeView.stopFlipping();
    }

}
