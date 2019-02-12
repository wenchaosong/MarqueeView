# 轮播控件

## 使用步骤

#### Step 1.
```groovy
repositories {
        jcenter()
        maven { url 'https://jitpack.io' }
    }

dependencies{
    compile 'com.github.wenchaosong:MarqueeView:1.0.0'
}
```
或者引用本地lib
```groovy
compile project(':marqueeview')
```

#### Step 2.
```xml
<com.ms.marquee.MarqueeView
        android:id="@+id/marquee"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:direction="LeftToRight"
        app:duration="1000"
        app:ellipsize="end"
        app:gravity="left"
        app:interval="3000"
        app:singleLine="true"
        app:textColor="#666666"
        app:textSize="12sp" />
```

#### Step 3.

```java
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
```

#### Step 4.（可选）
```java
@Override
protected void onStart() {
    super.onStart();
    if (mMarqueeView != null) {
        mMarqueeView.showNext();
        mMarqueeView.startFlipping();
    }
}

@Override
protected void onStop() {
    super.onStop();
    if (mMarqueeView != null)
        mMarqueeView.stopFlipping();
}
```