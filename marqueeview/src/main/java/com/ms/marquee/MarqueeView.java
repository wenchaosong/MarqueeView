package com.ms.marquee;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SongWenChao
 * 自动轮播控件
 */
public class MarqueeView extends ViewFlipper {

    private int interval;
    private int animDuration;
    private int textSize;
    private int textColor;
    private boolean singleLine = false;

    private int gravity = Gravity.START | Gravity.CENTER_VERTICAL;
    private static final int GRAVITY_LEFT = 0;
    private static final int GRAVITY_CENTER = 1;
    private static final int GRAVITY_RIGHT = 2;

    private int ellipsize;
    private static final int ELLIPSIZE_NONE = 0;
    private static final int ELLIPSIZE_END = 4;
    private static final int ELLIPSIZE_START = 1;
    private static final int ELLIPSIZE_MIDDLE = 2;
    private static final int ELLIPSIZE_MARQUEE = 3;

    private boolean hasSetDirection = false;
    private int direction = DIRECTION_BOTTOM_TO_TOP;
    private static final int DIRECTION_BOTTOM_TO_TOP = 0;
    private static final int DIRECTION_TOP_TO_BOTTOM = 1;
    private static final int DIRECTION_RIGHT_TO_LEFT = 2;
    private static final int DIRECTION_LEFT_TO_RIGHT = 3;

    private int inAnimResId = R.anim.anim_bottom_in;
    private int outAnimResId = R.anim.anim_top_out;

    private int position;
    private List<? extends CharSequence> notices = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public MarqueeView(Context context) {
        this(context, null);
    }

    public MarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MarqueeView, defStyleAttr, 0);

        interval = typedArray.getInt(R.styleable.MarqueeView_mv_interval, 3000);
        animDuration = typedArray.getInt(R.styleable.MarqueeView_mv_duration, 1000);
        singleLine = typedArray.getBoolean(R.styleable.MarqueeView_mv_singleLine, false);
        textSize = typedArray.getDimensionPixelSize(R.styleable.MarqueeView_mv_textSize, px2sp(context, 13));
        textColor = typedArray.getColor(R.styleable.MarqueeView_mv_textColor, Color.parseColor("#ffffffff"));
        ellipsize = typedArray.getInt(R.styleable.MarqueeView_mv_ellipsize, ELLIPSIZE_NONE);

        int gravityType = typedArray.getInt(R.styleable.MarqueeView_mv_gravity, GRAVITY_LEFT);
        switch (gravityType) {
            case GRAVITY_LEFT:
                gravity = Gravity.START | Gravity.CENTER_VERTICAL;
                break;
            case GRAVITY_CENTER:
                gravity = Gravity.CENTER;
                break;
            case GRAVITY_RIGHT:
                gravity = Gravity.END | Gravity.CENTER_VERTICAL;
                break;
        }

        hasSetDirection = typedArray.hasValue(R.styleable.MarqueeView_mv_direction);
        direction = typedArray.getInt(R.styleable.MarqueeView_mv_direction, direction);
        if (hasSetDirection) {
            switch (direction) {
                case DIRECTION_BOTTOM_TO_TOP:
                    inAnimResId = R.anim.anim_bottom_in;
                    outAnimResId = R.anim.anim_top_out;
                    break;
                case DIRECTION_TOP_TO_BOTTOM:
                    inAnimResId = R.anim.anim_top_in;
                    outAnimResId = R.anim.anim_bottom_out;
                    break;
                case DIRECTION_RIGHT_TO_LEFT:
                    inAnimResId = R.anim.anim_right_in;
                    outAnimResId = R.anim.anim_left_out;
                    break;
                case DIRECTION_LEFT_TO_RIGHT:
                    inAnimResId = R.anim.anim_left_in;
                    outAnimResId = R.anim.anim_right_out;
                    break;
            }
        } else {
            inAnimResId = R.anim.anim_bottom_in;
            outAnimResId = R.anim.anim_top_out;
        }

        typedArray.recycle();
        setFlipInterval(interval);
    }

    /**
     * 根据字符串列表，启动翻页公告
     *
     * @param notices 字符串列表
     */
    public void startWithList(List<? extends CharSequence> notices) {
        startWithList(notices, inAnimResId, outAnimResId);
    }

    /**
     * 根据字符串列表，启动翻页公告
     *
     * @param notices      字符串列表
     * @param inAnimResId  进入动画的resID
     * @param outAnimResID 离开动画的resID
     */
    public void startWithList(List<? extends CharSequence> notices, int inAnimResId, int outAnimResID) {
        if (notices == null || notices.size() <= 0)
            return;
        setNotices(notices);
        postStart(inAnimResId, outAnimResID);
    }

    private void postStart(final int inAnimResId, final int outAnimResID) {
        post(new Runnable() {
            @Override
            public void run() {
                start(inAnimResId, outAnimResID);
            }
        });
    }

    private void start(final int inAnimResId, final int outAnimResID) {
        removeAllViews();
        clearAnimation();

        position = 0;
        addView(createTextView(notices.get(position)));

        if (notices.size() > 1) {
            setInAndOutAnimation(inAnimResId, outAnimResID);
            showNext();
            startFlipping();
        }

        if (getInAnimation() != null) {
            getInAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    position++;
                    if (position >= notices.size()) {
                        position = 0;
                    }
                    View view = createTextView(notices.get(position));
                    if (view.getParent() == null) {
                        addView(view);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    private TextView createTextView(CharSequence text) {
        TextView textView = (TextView) getChildAt((getDisplayedChild() + 1) % 3);
        if (textView == null) {
            textView = new TextView(getContext());
            textView.setGravity(gravity);
            textView.setTextColor(textColor);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            textView.setSingleLine(singleLine);
            switch (ellipsize) {
                case ELLIPSIZE_END:
                    textView.setEllipsize(TextUtils.TruncateAt.END);
                    break;
                case ELLIPSIZE_MARQUEE:
                    textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    textView.setTextIsSelectable(true);
                    textView.setFocusable(true);
                    break;
                case ELLIPSIZE_MIDDLE:
                    textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                    break;
                case ELLIPSIZE_START:
                    textView.setEllipsize(TextUtils.TruncateAt.START);
                    break;
                case ELLIPSIZE_NONE:
                    break;
            }
        }
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getPosition(), (TextView) v);
                }
            }
        });
        textView.setText(text);
        textView.setTag(position);
        return textView;
    }

    public int getPosition() {
        return (int) getCurrentView().getTag();
    }

    public List<? extends CharSequence> getNotices() {
        return notices;
    }

    public void setNotices(List<? extends CharSequence> notices) {
        this.notices = notices;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, TextView textView);
    }

    /**
     * 设置进入动画和离开动画
     *
     * @param inAnimResId  进入动画的resID
     * @param outAnimResID 离开动画的resID
     */
    private void setInAndOutAnimation(int inAnimResId, int outAnimResID) {
        Animation inAnim = AnimationUtils.loadAnimation(getContext(), inAnimResId);
        inAnim.setDuration(animDuration);
        setInAnimation(inAnim);

        Animation outAnim = AnimationUtils.loadAnimation(getContext(), outAnimResID);
        outAnim.setDuration(animDuration);
        setOutAnimation(outAnim);
    }

    // 将px值转换为sp值
    private int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
}
