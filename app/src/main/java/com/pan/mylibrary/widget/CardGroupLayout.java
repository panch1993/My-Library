package com.pan.mylibrary.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.core.view.ViewPropertyAnimatorListener;

import com.facebook.drawee.view.SimpleDraweeView;
import com.pan.mylibrary.R;
import com.pan.mylibrary.utils.MUtil;
import com.pan.mylibrary.utils.ResourceUtil;
import com.pan.mylibrary.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 最新--卡片组
 * Created by panchenhuan on 17/8/9.
 * todo 17年小贝智投的控件,有时间优化一下
 */

public class CardGroupLayout extends FrameLayout {
    private Context mContext;
    //初始位置集合 orderid/int[]{l,t.r.b}
    private SparseArray<Integer[]> locationMap = new SparseArray<>();
    //存储message 等触摸结束后散发
    private List<Message> mMessageList = new ArrayList<>();
    private boolean isAnimating, isTouching;
    //根据view 将数据list保存
    private Map<View, List<Object>> mDataMap = new HashMap<>();
    //根据stockid保存简称信息
//    private Map<String, String> mBlockIdMap = new HashMap<>();
    //需要滑动的小卡片集合
    private List<CardView> needSmooth = new ArrayList<>();
    //小卡片,竖直动画滑动标识
    private HashMap<View, Boolean> mSmoothAnimateFlag = new HashMap<>();
    //小卡片,数据索引
    private HashMap<View, Integer> mPositionMap = new HashMap<>();
    //翻转动画记录flag
    private Map<View, Boolean> mRotationAnimateFlag = new HashMap<>();
    //大卡片动画是否执行
    private boolean firstSmooth = false;
    //当前小卡片竖直滚动是否滑动到了最后一个数据
    private Map<View, Boolean> mBooleanMap = new HashMap<>();
    //第一个卡片是否正执行动画
    private boolean firstAnimte = false;
    //有无卡片数据
    private boolean hasCardData = false;
    //动画执行时间
    private static final int ANIMATION_TIME = 750;
    //动画间隔时间
    private static final int ANIMA_DELAY = 2 * ANIMATION_TIME;
    //存储所有卡片的orderid/布局信息
    private SparseArray<ViewGroup.LayoutParams> mParamsMap = new SparseArray<>();
    //小格子长宽
    private int mPercent33;
    //边距
    private int mMargin8;
    //设置tokenId
    private String tokenId;
    //数据时间
    private long mTimeMillis;
    //小卡片组
    private CardView[] mSubCard;
    //大卡片
    private CardView cvFirst, cvLast;
    //是否拦截所有动画handler
    private boolean interceptHander = false;
    //重新开始
    private static final int RESTART = 2;
    //小卡片滑动
    private static final int SMOOTH_CARD = 3;
    //只翻转
    private static final int JUST_OVERTURN = 5;
    //大卡片滑动
    private static final int SMOOTH_BIG = 6;
    //翻转后开始滑动
    private static final int OVER_AND_SMOOTH = 7;
    //延迟抬起
    private static final int LATE_UP = 8;

    private long lastSubCardAnimationTime;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (!hasCardData) {
                    mHandler.removeCallbacksAndMessages(null);
                    return;
                }
                if (getVisibility() == View.GONE) {
                    mHandler.removeCallbacksAndMessages(null);
                    return;
                }
                if (msg.what == LATE_UP) {
                    isTouching = false;
                    //刷新handler
                    for (Message message : mMessageList) {
                        mHandler.sendMessageDelayed(Message.obtain(mHandler, message.what, message.obj), 500);
                    }
                    mMessageList.clear();
                    return;
                }
                if (interceptHander || isTouching) {
                    //不可见或菜单打开,保存handler消息
                    //Log.d("panc", "msg.what" + msg.what);
                    mMessageList.add(Message.obtain(mHandler, msg.what, msg.obj));
                    return;
                }

                switch (msg.what) {
                    case JUST_OVERTURN:
                        //翻页,由滑动卡片方法调用,全部翻到描述页,在滚动
                        overturnCard((View) msg.obj, false);
                        lastSubCardAnimationTime = System.currentTimeMillis();
                        /*if (!firstAnimte) {
                        } else {
                            mHandler.sendMessageDelayed(Message.obtain(mHandler, msg.what, msg.obj), 2250);
                        }*/
                        break;
                    case OVER_AND_SMOOTH:
                        lastSubCardAnimationTime = System.currentTimeMillis();
                        overturnCard((View) msg.obj, true);
                        /*if (!firstAnimte) {
                        } else {
                            mHandler.sendMessageDelayed(Message.obtain(mHandler, msg.what, msg.obj), 2250);
                        }*/
                        break;
//                    case UPDATE_STOCK:
//                        //延迟 刷新
//                        String stockId = (String) msg.obj;
//                        updateStockInfo(stockId);
//                        break;
                    case SMOOTH_CARD:
                        FrameLayout current = (FrameLayout) msg.obj;
                        mHandler.removeMessages(SMOOTH_CARD, current);
                        lastSubCardAnimationTime = System.currentTimeMillis();
                        //滑动小卡片替换内容
                        smoothSubviewVertical(current);
                        //同时开始滑动
                        if (needSmooth.size() > 0) {
                            for (int i = needSmooth.size() - 1; i >= 0; i--) {
                                smoothSubviewVertical(needSmooth.get(i));
                                needSmooth.remove(i);
                            }
                        }
                        break;
                    case SMOOTH_BIG:
                        if (System.currentTimeMillis() - lastSubCardAnimationTime > 3000) {
                            restart();
                            return;
                        }
                        if (!firstAnimte) {
                            boolean scrollFirst = cvFirst.findViewById(R.id.ll_detail).getVisibility() == View.VISIBLE;
                            CardView nowCard = scrollFirst ? cvFirst : cvLast;
                            //得到存储的position索引
                            Integer position = mPositionMap.get(nowCard);
                            if (position == null) {
                                position = 0;
                            }
                            List<Object> list = mDataMap.get(nowCard);
                            if (list == null) {
                                //针对没有数据的情况
                                alphaBigCard();
                            } else if (position == list.size() - 1) {
                                //已经是最后一组数据,显示渐变动画,数据重置设为第一组
                                alphaBigCard();
                            } else {
                                //大卡片滑动
                                smoothFirstCard(nowCard);
                            }
                        }
                        break;
                    case RESTART:
                        if (!firstAnimte) {
                            mHandler.removeCallbacksAndMessages(null);
                            if (mSubCard != null && mSubCard.length > 0) {
                                for (CardView cardView : mSubCard) {
                                    mBooleanMap.put(cardView, false);
                                }
                                mHandler.sendMessage(Message.obtain(mHandler, OVER_AND_SMOOTH, mSubCard[0]));
                            }
                            mHandler.sendMessage(Message.obtain(mHandler, SMOOTH_BIG));
                        } else {
                            mHandler.sendMessageDelayed(Message.obtain(mHandler, msg.what, msg.obj), ANIMA_DELAY);
                        }
                        break;

                }
            } catch (Exception e) {
                restart();
                e.printStackTrace();
            }
        }
    };
    private List<Object>[] mLists;

    public CardGroupLayout(@NonNull Context context) {
        this(context, null);
    }

    public CardGroupLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardGroupLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initCardGroup();
    }

    public void setInterceptHander(boolean interceptHander) {
        this.interceptHander = interceptHander;
        if (!interceptHander && !mMessageList.isEmpty()) {
            //发送页面不可见时拦截的消息
            for (Message message : mMessageList) {
                mHandler.sendMessageDelayed(Message.obtain(mHandler, message.what, message.obj), 1500);
            }
            mMessageList.clear();
        }
    }

    //拖动&点击
    OnTouchListener dragTouchListener = new OnTouchListener() {
        int lastX, lastY; // 记录移动的最后的位置
        int startX, startY;//记录开始位置
        int endX, endY;//记录结束位置

        int left = 0, top = 0, right = 0, bottom = 0;
        int orderId = -1;
        long pressTime = 0;//按住的时间

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = MotionEventCompat.getActionMasked(event);
            //Log.d("panc", "event  " + event.getAction() + "  masked   " + action);
            //触摸按钮 拦截viewpager滑动事件
            v.getParent().requestDisallowInterceptTouchEvent(true);
            //获取卡片位置orderid
            for (int i = 0; i < mSubCard.length; i++) {
                if (mSubCard[i] == v) {
                    orderId = i + 2;
                    break;
                }
            }
            switch (action) {
                case MotionEvent.ACTION_CANCEL:
                    isTouching = false;
                    break;
                case MotionEvent.ACTION_DOWN: // 按下
                    mHandler.removeMessages(LATE_UP);
                    isTouching = true;
                    //提高当前子view 显示层次,可浮在顶上
                    bringChildToFront(v);
                    //记录初始控件位置
                    if (locationMap.get(orderId) == null || locationMap.get(orderId)[3] == 0) {
                        locationMap.put(orderId, new Integer[]{v.getLeft(), v.getTop(), v.getRight(), v.getBottom()});
                    }
                    //记录按下时间
                    pressTime = System.currentTimeMillis();
                    //按下坐标
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    startX = v.getLeft();
                    startY = v.getTop();
                    break;
                case MotionEvent.ACTION_MOVE: // 移动
                    //可能没有down事件,以防万一
                    isTouching = true;
                    // 移动中动态设置位置
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;
                    //新位置信息
                    left = v.getLeft() + dx;
                    top = v.getTop() + dy;
                    right = v.getRight() + dx;
                    bottom = v.getBottom() + dy;
                    //限定滑动范围
                    if (left < 0) {
                        //左边越界
                        left = 0;
                        right = left + v.getWidth();
                    } else if (right > CardGroupLayout.this.getWidth()) {
                        //右边越界
                        right = CardGroupLayout.this.getWidth();
                        left = right - v.getWidth();
                    }
                    if (top < 0) {
                        //上越界
                        top = 0;
                        bottom = top + v.getWidth();
                    } else if (bottom > CardGroupLayout.this.getHeight()) {
                        //下越界
                        bottom = CardGroupLayout.this.getHeight();
                        top = bottom - v.getHeight();
                    }

                    //重绘
                    v.layout(left, top, right, bottom);
                    //重新赋值
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_UP: // 抬起
                    //触摸flag
                    mHandler.sendEmptyMessageDelayed(LATE_UP, 1500);
                    //结束位置坐标
                    endX = v.getLeft();
                    endY = v.getTop();
                    //获取原始位置信息
                    Integer[] locations;
                    //根据点击时间或移动距离判断 区分点击和拖动
                    boolean isMove = Math.abs(endX - startX) > 10 || Math.abs(endY - startY) > 10 || System.currentTimeMillis() - pressTime > 150;

                    if (isMove) {//拖动事件
                        int bottom = v.getBottom();
                        int right = v.getRight();
                        //判断是否在第一个卡片内
                        final boolean inFirstCard = cvFirst.getWidth() > right * 0.75f && cvFirst.getHeight() > bottom * 0.75f;
                        boolean inLastCard = cvLast.getLeft() * 0.75f < endX && cvLast.getTop() * 0.75f < endY;
                        if (inFirstCard || inLastCard) {
                            //详细页
                            View detail = mSubCard[0].findViewById(R.id.ll_detail);
                            v.setVisibility(View.INVISIBLE);
                            //当前第一个卡片是否显示详情页,把当前的卡片提前设置为一样状态
                            boolean isDetail = detail.getVisibility() == View.VISIBLE;
                            if (isDetail) {
                                v.findViewById(R.id.ll_detail).setVisibility(View.VISIBLE);
                                v.findViewById(R.id.ll_detail).setAlpha(1);
                                //描述页
                                v.findViewById(R.id.ll_simple).setVisibility(View.GONE);
                                Boolean aBoolean = mBooleanMap.get(v);
                                if (aBoolean == null || !aBoolean) {
                                    needSmooth.add((CardView) v);
                                } else {
                                    needSmooth.remove(v);
                                }
                            } else {
                                v.findViewById(R.id.ll_detail).setVisibility(View.GONE);
                                //描述页
                                v.findViewById(R.id.ll_simple).setVisibility(View.VISIBLE);
                                v.findViewById(R.id.ll_simple).setAlpha(1);
                            }
                            //各个动画
                            if (orderId == 2) {
                                bigCardScaleAnimation(inFirstCard);
                            } else {
                                //平移动画
                                final ViewPropertyAnimatorCompat compat = ViewCompat.animate(mSubCard[0]).translationYBy(mMargin8 + mPercent33).setDuration(ANIMATION_TIME);
                                compat.setListener(new ViewPropertyAnimatorListener() {
                                    @Override
                                    public void onAnimationStart(View view) {
                                        //更改排序 指针
                                        int orderIndex = orderId - 2;
                                        final CardView temp = mSubCard[orderIndex];
                                        for (int i = orderIndex; i >= 0; i--) {
                                            if (i == 0) {
                                                mSubCard[0] = temp;
                                            } else {
                                                mSubCard[i] = mSubCard[i - 1];
                                            }
                                        }

                                        //将view放置在右上角
                                        mSubCard[0].setLayoutParams(mParamsMap.get(2));
                                        //第一个卡片缩放动画
                                        bigCardScaleAnimation(inFirstCard);
                                        //小卡片位移动画,使用switch贯穿
                                        switch (orderId) {
                                            case 6:
                                                cardAnimation(mSubCard[4], 0, 1);
                                            case 5:
                                                cardAnimation(mSubCard[3], -1, 0);
                                            case 4:
                                                cardAnimation(mSubCard[2], -1, 0);
                                                break;
                                        }

                                    }

                                    @Override
                                    public void onAnimationEnd(View view) {
                                        //重新设置布局规则
                                        for (int i = 0; i < mSubCard.length; i++) {
                                            CardView cardView = mSubCard[i];
                                            if (!cardView.isShown()) {
                                                cardView.setVisibility(View.VISIBLE);
                                            }
                                            cardView.setLayoutParams(mParamsMap.get(i + 2));
                                        }
                                        //平移归零
                                        checkTranslation(view);
                                        //移除动画监听   很重要
                                        compat.setListener(null);
                                    }

                                    @Override
                                    public void onAnimationCancel(View view) {
                                        compat.setListener(null);
                                        isAnimating = false;
                                    }
                                }).start();
                            }
                        } else {
                            //位移较小,只移动至原位
                            locations = locationMap.get(orderId);
                            //根据位移距离计算归位动画时间
                            int left = v.getLeft();
                            int top = v.getTop();
                            int absX = Math.abs(left - locations[0]);
                            int absY = Math.abs(top - locations[1]);
                            double time = Math.sqrt(absX * absX + absY * absY);
                            //归位动画---移动至原位,缩放 1X,透明度 1
                            final ViewPropertyAnimatorCompat compat = ViewCompat.animate(v).x(locations[0]).y(locations[1]).scaleX(1).scaleY(1).alpha(1).setDuration((long) time);
                            compat.setListener(new ViewPropertyAnimatorListener() {
                                @Override
                                public void onAnimationStart(View view) {
                                    isAnimating = true;
                                }

                                @Override
                                public void onAnimationEnd(View view) {
                                    isAnimating = false;
                                    //归位
                                    view.setTranslationX(0);
                                    view.setTranslationY(0);
                                    //移除监听
                                    compat.setListener(null);
                                    view.setLayoutParams(mParamsMap.get(orderId));
                                }

                                @Override
                                public void onAnimationCancel(View view) {
                                    isAnimating = false;
                                }
                            }).start();
                        }

                    } else {
                        //位移较小,只移动至原位
                        locations = locationMap.get(orderId);
                        v.layout(locations[0], locations[1], locations[2], locations[3]);
                        //显示详细页
                        if (v.findViewById(R.id.ll_detail).getVisibility() == View.VISIBLE) {
                            ToastUtil.showToast("暂无数据");

                        } else {
                            ToastUtil.showToast("暂无数据2");
                        }
                    }
                    return true;
            }
            return true;
        }

    };

    /**
     * 大卡片缩放/小卡片缩放 动画
     */
    private void bigCardScaleAnimation(final boolean isFirst) {
        //得到subview和firstview的数据list
        final CardView subView = mSubCard[0];
        final List<Object> subList = mDataMap.get(subView);
        final CardView bigCard = isFirst ? cvFirst : cvLast;
        final List<Object> firList = mDataMap.get(bigCard);
        //原卡片缩放动画
        final ScaleAnimation bigCardAnimation;
        if (isFirst) {
            bigCardAnimation = new ScaleAnimation(1, 0, 1, 0, bigCard.getWidth(), 0);
        } else {
            bigCardAnimation = new ScaleAnimation(1, 0, 1, 0, 0, 0);
        }
        bigCardAnimation.setInterpolator(new AccelerateDecelerateInterpolator() {
            @Override
            public float getInterpolation(float input) {
                bigCard.setAlpha(1 - input);
                if (input == 1) {
                    bigCardAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                }
                return super.getInterpolation(input);
            }
        });
        bigCardAnimation.setDuration(ANIMATION_TIME);
        //添加新卡片放大动画
        bigCardAnimation.setAnimationListener(new Animation.AnimationListener() {
            CardView view = (CardView) View.inflate(getContext(), R.layout.layout_big_card_left_up, null);

            @Override
            public void onAnimationStart(Animation animation) {
                //正在执行动画标致
                isAnimating = true;
                firstAnimte = true;
                //设置id
                view.setId(isFirst ? R.id.vg_big_card_1 : R.id.vg_big_card_2);
                ViewGroup.LayoutParams layoutParams = bigCard.getLayoutParams();
                //添加进总布局
                CardGroupLayout.this.addView(view, 0, layoutParams);
                final ScaleAnimation subAnimation;
                if (isFirst) {
                    subAnimation = new ScaleAnimation(0, 1, 0, 1, 0, 0);
                } else {
                    subAnimation = new ScaleAnimation(0, 1, 0, 1, subView.getWidth(), 0);
                }
                if (subView.findViewById(R.id.ll_detail).getVisibility() == View.VISIBLE) {
                    subView.findViewById(R.id.ll_detail).setAlpha(1);
                }
                subAnimation.setDuration(ANIMATION_TIME);
                subView.setVisibility(View.VISIBLE);
                subView.startAnimation(subAnimation);
                //放大动画
                final ScaleAnimation newAnimation;
                if (isFirst) {
                    newAnimation = new ScaleAnimation(0, 1, 0, 1, 0, bigCard.getHeight());
                } else {
                    newAnimation = new ScaleAnimation(0, 1, 0, 1, bigCard.getWidth(), bigCard.getHeight());
                }
                newAnimation.setInterpolator(new AccelerateDecelerateInterpolator() {
                    @Override
                    public float getInterpolation(float input) {
                        view.setAlpha(input);
                        if (input == 1) {
                            newAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                        }
                        return super.getInterpolation(input);
                    }
                });
                newAnimation.setDuration(ANIMATION_TIME);
                view.startAnimation(newAnimation);
                //修改颜色
                //子card原有 颜色
                final int subColor = subView.getCardBackgroundColor().getDefaultColor();
                final int bigColor = bigCard.getCardBackgroundColor().getDefaultColor();
                //设置小卡片新颜色
                subView.setCardBackgroundColor(bigColor);
                view.setCardBackgroundColor(subColor);
                //设置数据
                updateExhangeView(view, subView, bigCard);
                view.findViewById(R.id.ll_detail).setVisibility(bigCard.findViewById(R.id.ll_detail).getVisibility());
                view.findViewById(R.id.fl_img).setVisibility(bigCard.findViewById(R.id.fl_img).getVisibility());
                //交换数据
                mDataMap.put(view, subList);
                mDataMap.put(subView, firList);
                //设置tag
                Integer position = mPositionMap.get(subView);
                Integer integer = mPositionMap.get(bigCard);
                mPositionMap.put(subView, integer);
                mPositionMap.put(view, position);
                //移除原数据
                mDataMap.remove(bigCard);
                view.findViewById(R.id.ll_detail).setOnClickListener(mOnClickListener);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimating = false;
                firstAnimte = false;
                CardGroupLayout.this.removeView(bigCard);//移除原view
                if (isFirst) {
                    cvFirst = view;//修改指针
                } else {
                    cvLast = view;
                }
                animation.setAnimationListener(null);//移除动画监听   很重要
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        bigCard.startAnimation(bigCardAnimation);
    }

    /**
     * 小卡片位移
     *
     * @param subView 需要动画的卡片
     * @param xNum    x位移几个卡片单位
     * @param yNum    y位移几个卡片单位
     */
    private void cardAnimation(View subView, int xNum, int yNum) {
        //单个卡片位移长度
        float oneCard = mPercent33 + mMargin8;
        ViewCompat.animate(subView).translationYBy(yNum * oneCard).translationXBy(xNum * oneCard)
                .setDuration(ANIMATION_TIME)
                .setListener(new ViewPropertyAnimatorListener() {

                    @Override
                    public void onAnimationStart(View view) {

                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        //平移归零
                        checkTranslation(view);
                    }

                    @Override
                    public void onAnimationCancel(View view) {
                        //移除监听
                        ViewCompat.animate(view).setListener(null);
                    }
                }).start();
    }

    //平移校准
    private void checkTranslation(View view) {
        if (view.getTranslationY() != 0) {
            view.setTranslationY(0);
        }
        if (view.getTranslationX() != 0) {
            view.setTranslationX(0);
        }
    }

    //大卡片渐变置换
    private void alphaBigCard() {
        final View firstDetail = cvFirst.findViewById(R.id.ll_detail);
        final View firstImage = cvFirst.findViewById(R.id.fl_img);
        final View lastDetail = cvLast.findViewById(R.id.ll_detail);
        final View lastImage = cvLast.findViewById(R.id.fl_img);
        final boolean isFirstDetail = firstDetail.getVisibility() == View.VISIBLE;
        ValueAnimator animator = ObjectAnimator.ofFloat(1f, 0f);
        animator.setDuration(ANIMATION_TIME)
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float invisible = (float) animation.getAnimatedValue();
                        float visible = 1 - invisible;
                        if (isFirstDetail) {
                            firstImage.setAlpha(visible);
                            firstDetail.setAlpha(invisible);
                            lastImage.setAlpha(invisible);
                            lastDetail.setAlpha(visible);
                        } else {
                            firstImage.setAlpha(invisible);
                            firstDetail.setAlpha(visible);
                            lastImage.setAlpha(visible);
                            lastDetail.setAlpha(invisible);
                        }
                    }
                });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isFirstDetail) {
                    firstDetail.setVisibility(View.GONE);
                    lastImage.setVisibility(View.GONE);
                    ((TextView) lastDetail.findViewById(R.id.tv_date)).setText(mTimeMillis+"-");
                } else {
                    firstImage.setVisibility(View.GONE);
                    lastDetail.setVisibility(View.GONE);
                    ((TextView) firstDetail.findViewById(R.id.tv_date)).setText(mTimeMillis+"-");
                }
                //获取对应数据list
                CardView nowCard = isFirstDetail ? cvFirst : cvLast;
                //隐藏的页面重置数据
                List<Object> beanList = mDataMap.get(nowCard);
                if (beanList != null && !beanList.isEmpty()) {
                    mPositionMap.put(nowCard, 0);
                    Object bean = beanList.get(0);
                    setBigCardData(bean, nowCard);
                }
                mHandler.sendEmptyMessageDelayed(SMOOTH_BIG, ANIMA_DELAY);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                firstDetail.setVisibility(View.VISIBLE);
                firstImage.setVisibility(View.VISIBLE);
                lastDetail.setVisibility(View.VISIBLE);
                lastImage.setVisibility(View.VISIBLE);
            }
        });
        animator.start();
    }

    //跳转技术面
//    private void turnTechActivity(String tabName) {
//        Intent intent = new Intent(getContext(), TechnologyMoreActivity.class);
//        intent.putExtra("Title", "最新");// 详情界面的标题
//        intent.putExtra("DataType", "最新");
//        intent.putExtra("TabName", tabName);// 当前模块的标题
//        mContext.startActivity(intent);
//    }

    //置换数据后刷新
    private void updateExhangeView(CardView newBigCard, CardView subView, CardView preBigCard) {
        //原大卡片的股票
//        NewConfigBean preBigBean = null, preSubBean = null;
//        List<Object> preBigList = mDataMap.get(preBigCard);
//        List<Object> preSubList = mDataMap.get(subView);
//        if (preBigList != null && !preBigList.isEmpty()) {
//            Integer preBigPosition = mPositionMap.get(preBigCard);
//            if (preBigPosition == null) {
//                preBigPosition = 0;
//            }
//            try {
//                preBigBean = preBigList.get(preBigPosition);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        if (preSubList != null && !preSubList.isEmpty()) {
//            Integer preSubPosition = mPositionMap.get(subView);
//            if (preSubPosition == null) {
//                preSubPosition = 0;
//            }
//            try {
//                preSubBean = preSubList.get(preSubPosition);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        //设置数据
//        setBigCardData(preSubBean, newBigCard);
//        setSubCardData(preBigBean, subView);
    }

    //跳转点击
    OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
           ToastUtil.showToast("mOnClickListener");
        }
    };

    //设置大卡片数据
    private void setBigCardData(final Object bean, CardView bigCard) {
        String description, stockId, stockName, percent, blockName;
//        if (bean != null) {
//            stockId = bean.getSTOCK_ID();
//            description = bean.getDESCRIPTION();
//            blockName = mBlockIdMap.get(stockId);
//            RegistHQZCBean hqzcBean = StkStockPrice.getStockPriceflag(stockId);
//            if (hqzcBean != null) {
//                stockName = hqzcBean.getStock_name();
//            } else {
//                mNetCenter.sendHQRequest(stockId, tokenId);
//                stockName = realmController.getStockName(stockId);
//            }
//            percent = mFormatUtil.formatStock(hqzcBean)[1];
//        } else {
//        }
            description = "暂无数据";
            blockName = "暂无数据";
            stockId = "--";
            stockName = "--";
            percent = "--";
        bigCard.findViewById(R.id.sv).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                    ToastUtil.showToast("暂无数据  888");
            }
        });
        ((TextView) bigCard.findViewById(R.id.tv_block_intro)).setText(description);
        ((TextView) bigCard.findViewById(R.id.tv_block)).setText(blockName);
        ((TextView) bigCard.findViewById(R.id.tv_block_name)).setText(description);
        ((TextView) bigCard.findViewById(R.id.tv_stock_name)).setText(stockName);
        ((TextView) bigCard.findViewById(R.id.tv_stock_id)).setText(stockId);
        ((TextView) bigCard.findViewById(R.id.tv_percent)).setText(percent);
        ((TextView) bigCard.findViewById(R.id.tv_date)).setText(mTimeMillis+" 802");
        ((ImageView) bigCard.findViewById(R.id.iv_block_icon)).setImageResource(R.drawable.ic_menu_camera);
        ((SimpleDraweeView) bigCard.findViewById(R.id.sv)).setImageURI(bigCard.getId() == R.id.vg_big_card_1 ? "http://pic1.5442.com/2015/0527/06/03.jpg" : "http://pic26.photophoto.cn/20130319/0008020219680542_b.jpg");
        bigCard.findViewById(R.id.ll_detail).setOnClickListener(mOnClickListener);
    }


    //获取下一个cardview
    private CardView getNextCard(View current) {
        CardView next = null;
        for (int i = 0; i < mSubCard.length; i++) {
            if (current == mSubCard[i]) {
                if (i != mSubCard.length - 1) {
                    next = mSubCard[i + 1];
                } else {
                    next = mSubCard[0];
                }
                break;
            }
        }
        if (next == null) {
            next = mSubCard[0];
        }
        return next;
    }

    /**
     * 大卡片滑动动画
     */
    private synchronized void smoothFirstCard(CardView nowCard) {
        if (firstSmooth) {
            //正在执行动画
            return;
        }
        mHandler.removeMessages(SMOOTH_BIG);
        //两个大卡片 两面不同,获取当前显示哪面详情
        final View rightPart = nowCard.findViewById(R.id.ll_right);
        final FrameLayout content = (FrameLayout) nowCard.findViewById(R.id.fl_content);
        //获取对应数据list
        final List<Object> beanList = mDataMap.get(nowCard);

        final Integer currentPosition;
        if (beanList == null || beanList.size() == 1 || content.getChildCount() > 1) {
            mHandler.sendMessageDelayed(Message.obtain(mHandler, SMOOTH_BIG), ANIMA_DELAY + ANIMATION_TIME);
            return;
        }
        //得到存储的position索引
        Integer position = mPositionMap.get(nowCard);
        if (position == null) {
            //设置数字用于判断滑动的数据索引
            mPositionMap.put(nowCard, 1);
            currentPosition = 1;
        } else {
            //保存新索引
            mPositionMap.put(nowCard, (position + 1) % beanList.size());
            currentPosition = (position + 1) % beanList.size();
        }
        final Object bean = beanList.get(currentPosition);
        final ViewPropertyAnimatorCompat compat = ViewCompat.animate(rightPart).translationYBy(rightPart.getHeight()).setDuration(ANIMATION_TIME);
        compat.setListener(new ViewPropertyAnimatorListener() {
            View mView = View.inflate(getContext(), R.layout.layout_big_card_scroll_part, null);

            @Override
            public void onAnimationStart(View view) {
                firstSmooth = true;
                //设置数据
                if (mView == null) {
                    mView = View.inflate(getContext(), R.layout.layout_big_card_scroll_part, null);
                }

                ((TextView) mView.findViewById(R.id.tv_stock_id)).setText("stock_id");
                ((TextView) mView.findViewById(R.id.tv_block_intro)).setText("bean.getDESCRIPTION()");
//                RegistHQZCBean hqzcBean = StkStockPrice.getStockPriceflag(stock_id);
//                String[] strings = mFormatUtil.formatStock(hqzcBean);
//                if (hqzcBean == null) {
//                    mNetCenter.sendHQRequest(stock_id, tokenId);
//                    ((TextView) mView.findViewById(R.id.tv_stock_name)).setText(realmController.getStockName(stock_id));
//                } else {
//                    ((TextView) mView.findViewById(R.id.tv_stock_name)).setText(hqzcBean.getStock_name());
//                }
//                ((TextView) mView.findViewById(R.id.tv_percent)).setText(strings[1]);
//                ((TextView) mView.findViewById(R.id.tv_date)).setText(DateUtil.getTimeRange(mTimeMillis));
                //TextColorUtil.setColor(((TextView) mView.findViewById(R.id.tv_percent)), strings[3]);
                //添加新view
                content.addView(mView);
                //设置偏移量(位于原卡片正上方)
                mView.setTranslationY(-rightPart.getHeight());
                //滑动到小卡片位置
                ViewCompat.animate(mView).translationY(0).setDuration(ANIMATION_TIME).start();
                //点击事件
                mView.setOnClickListener(mOnClickListener);
            }

            @Override
            public void onAnimationEnd(View view) {
                firstSmooth = false;
                //移除监听
                compat.setListener(null);
                //移除原view
                content.removeView(rightPart);
                mHandler.sendEmptyMessageDelayed(SMOOTH_BIG, ANIMA_DELAY);
            }

            @Override
            public void onAnimationCancel(View view) {

            }
        }).start();
    }

    /**
     * 小卡片竖直平移
     */
    private void smoothSubviewVertical(final FrameLayout subView) {
        Boolean flag = mSmoothAnimateFlag.get(subView);
        if (flag != null && flag) {
            //当前card正在执行竖直滑动动画
            return;
        }
        //判断是否正在执行动画
        Boolean flag2 = mRotationAnimateFlag.get(subView);
        if (flag2 != null && flag2) {
            //当前card正在执行翻转
            return;
        }
        final View detail = subView.findViewById(R.id.ll_detail);
        if (detail.getVisibility() == View.GONE) {
            //未翻页
            overturnCard(subView, true);
            return;
        }
        int currentPosition;
        //获取对应数据list
        List<Object> beanList = mDataMap.get(subView);
        if (beanList != null && beanList.size() > 1) {
            //设置flag(用于判断该card是否正在执行动画)
            mSmoothAnimateFlag.put(subView, true);
            //得到存储的position索引
            Integer position = mPositionMap.get(subView);
            if (position == null) {
                //设置数字用于判断滑动的数据索引
                mPositionMap.put(subView, 1);
                currentPosition = 1;
            } else {
                //保存新索引
                mPositionMap.put(subView, (position + 1) % beanList.size());
                currentPosition = (position + 1) % beanList.size();
            }
//            final NewConfigBean bean;
//            if (currentPosition >= beanList.size()) {
//                bean = beanList.get(beanList.size() - 1);
//            } else {
//                bean = beanList.get(currentPosition);
//            }
            final ViewPropertyAnimatorCompat compat = ViewCompat.animate(detail).translationY(detail.getHeight()).setDuration(ANIMATION_TIME);
            final int finalCurrentPosition = currentPosition;
            compat.setListener(new ViewPropertyAnimatorListener() {
                View mView = View.inflate(getContext(), R.layout.layout_sub_card_detail, null);

                @Override
                public void onAnimationStart(View view) {
                    if (mView == null) {
                        mView = View.inflate(getContext(), R.layout.layout_sub_card_detail, null);
                    }
                    String description, stockId, stockName, percent;
//                    if (bean != null) {
//                        stockId = bean.getSTOCK_ID();
//                        description = bean.getDESCRIPTION();
//                        RegistHQZCBean hqzcBean = StkStockPrice.getStockPriceflag(stockId);
//                        if (hqzcBean != null) {
//                            stockName = hqzcBean.getStock_name();
//                        } else {
//                            mNetCenter.sendHQRequest(stockId, tokenId);
//                            stockName = realmController.getStockName(stockId);
//                        }
//                        percent = mFormatUtil.formatStock(hqzcBean)[1];
//                    } else {
//                        description = "暂无数据";
//                        stockId = "";
//                        stockName = "";
//                        percent = "";
//                    }
//                    ((TextView) mView.findViewById(R.id.tv_block_intro)).setText(description);
//                    ((TextView) mView.findViewById(R.id.tv_stock_id)).setText(stockId);
//                    ((TextView) mView.findViewById(R.id.tv_stock_name)).setText(stockName);
//                    ((TextView) mView.findViewById(R.id.tv_percent)).setText(percent);
//                    ((TextView) subView.findViewById(R.id.tv_block_name)).setText(description);
//                    ((ImageView) subView.findViewById(R.id.iv_block_icon)).setImageResource(SmartAssistantUtils.getWhiteImage(mBlockIdMap.get(stockId)));
                    //设为可见(初始状态为不可见)
                    mView.setVisibility(View.VISIBLE);
                    //添加新view
                    subView.addView(mView);
                    //设为不透明(初始状态为透明)
                    mView.setAlpha(1);
                    //设置偏移量(位于小卡片正上方)
                    mView.setTranslationY((detail.getTop() - detail.getHeight()));
                    //滑动到小卡片位置
                    ViewCompat.animate(mView).translationY(0).setDuration(ANIMATION_TIME).start();
                }

                @Override
                public void onAnimationEnd(View view) {
                    //移除监听
                    compat.setListener(null);
                    //移除原view
                    subView.removeView(detail);
                    //移除tag
                    mSmoothAnimateFlag.put(subView, false);
                    //下一个如果是正面,翻过来
                    CardView nextCard = getNextCard(subView);
                    //详细页
                    View detail = nextCard.findViewById(R.id.ll_detail);
                    //当前是否显示详情页
                    boolean isDetail = detail.getAlpha() == 1;
                    if (!isDetail) {
                        mHandler.sendMessageDelayed(Message.obtain(mHandler, OVER_AND_SMOOTH, nextCard), ANIMA_DELAY);
                    }
                    if (finalCurrentPosition < mDataMap.get(subView).size() - 1) {
                        //如果不是最后一个,继续
                        mHandler.sendMessageDelayed(Message.obtain(mHandler, SMOOTH_CARD, subView), ANIMA_DELAY);
                        mBooleanMap.put(subView, false);
                    } else {
                        //是否滑动到底部
                        mBooleanMap.put(subView, true);
                        mHandler.removeMessages(SMOOTH_CARD, subView);
                        //通知第一个开始翻转
                        boolean canTurn = true;
                        for (Boolean aBoolean : mBooleanMap.values()) {
                            if (aBoolean != null) {
                                if (!aBoolean) {
                                    canTurn = false;
                                    break;
                                }
                            }
                        }
                        //全部滚动到最后 开始翻转
                        if (canTurn) {
                            mHandler.sendMessageDelayed(Message.obtain(mHandler, JUST_OVERTURN, mSubCard[0]), ANIMA_DELAY);
                            mHandler.removeMessages(SMOOTH_CARD);
                        }
                    }
                }

                @Override
                public void onAnimationCancel(View view) {
                    //移除监听
                    compat.setListener(null);
                    //移除插补
                    compat.setInterpolator(new AccelerateDecelerateInterpolator());
                }
            }).start();
        } else {
            //是否滑动到底部
            mBooleanMap.put(subView, true);
            mHandler.removeMessages(SMOOTH_CARD, subView);
            boolean canTurn = true;
            for (Boolean aBoolean : mBooleanMap.values()) {
                if (aBoolean != null) {
                    if (!aBoolean) {
                        canTurn = false;
                        break;
                    }
                }
            }
            if (canTurn) {
                //通知第一个开始翻转
                mHandler.sendMessageDelayed(Message.obtain(mHandler, JUST_OVERTURN, mSubCard[0]), 1500);
                mHandler.removeMessages(SMOOTH_CARD);
            }
        }

    }

    /**
     * 设置小卡片数据
     */
    private void setSubCardData(Object bean, CardView cardView) {
        String description, stockId, stockName, percent;
//        if (bean != null) {
//            stockId = bean.getSTOCK_ID();
//            description = bean.getDESCRIPTION();
//            RegistHQZCBean hqzcBean = StkStockPrice.getStockPriceflag(stockId);
//            if (hqzcBean != null) {
//                stockName = hqzcBean.getStock_name();
//            } else {
//                mNetCenter.sendHQRequest(stockId, tokenId);
//                stockName = realmController.getStockName(stockId);
//            }
//            percent = mFormatUtil.formatStock(hqzcBean)[1];
//        } else {
//            description = "暂无数据";
//            stockId = "--";
//            stockName = "--";
//            percent = "--";
//        }
//        ((TextView) cardView.findViewById(R.id.tv_block_intro)).setText(description);
//        ((TextView) cardView.findViewById(R.id.tv_block_name)).setText(description);
//        ((TextView) cardView.findViewById(R.id.tv_stock_id)).setText(stockId);
//        ((TextView) cardView.findViewById(R.id.tv_stock_name)).setText(stockName);
//        ((TextView) cardView.findViewById(R.id.tv_percent)).setText(percent);
//        ((ImageView) cardView.findViewById(R.id.iv_block_icon)).setImageResource(SmartAssistantUtils.getWhiteImage(mBlockIdMap.get(stockId)));
    }

    /**
     * 翻转小卡片
     */
    private void overturnCard(final View subView, final boolean thanSmooth) {
        //判断是否正在执行动画
        Boolean flag = mRotationAnimateFlag.get(subView);
        if (flag != null && flag) {
            //当前card正在执行竖直滑动动画
            return;
        }
        //只允许一个handler翻转控制
        mHandler.removeMessages(JUST_OVERTURN);
        mHandler.removeMessages(OVER_AND_SMOOTH);
        //详细页
        final View detail = subView.findViewById(R.id.ll_detail);
        //描述页
        final View simple = subView.findViewById(R.id.ll_simple);

        //当前是否显示详情页
        final boolean isDetail = detail.getVisibility() == View.VISIBLE;
        if (thanSmooth && isDetail) {
            //已是详情页 并需要滑动  直接开始滑动
            smoothSubviewVertical((FrameLayout) subView);
            return;
        }
        //等待,先翻转下一个
        if (!thanSmooth && !isDetail) {
            if (subView != mSubCard[mSubCard.length - 1]) {
                overturnCard(getNextCard(subView), false);
            } else {
                mHandler.sendMessageDelayed(Message.obtain(mHandler, RESTART), ANIMA_DELAY);
            }
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(subView, "scaleY", 1, 0, 1);
        animator.setInterpolator(new AccelerateDecelerateInterpolator() {
            @Override
            public float getInterpolation(float input) {
                if (isDetail) {
                    detail.setAlpha(1 - input);
                    simple.setAlpha(input);
                } else {
                    detail.setAlpha(input);
                    simple.setAlpha(1 - input);
                }
                return super.getInterpolation(input);
            }
        });
        animator.setDuration(ANIMATION_TIME);
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                mRotationAnimateFlag.put(subView, true);
                //全部设为显示
                simple.setVisibility(View.VISIBLE);
                detail.setVisibility(View.VISIBLE);
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRotationAnimateFlag.put(subView, false);
                if (isDetail) {
                    detail.setVisibility(View.GONE);
                    simple.setVisibility(View.VISIBLE);
                } else {
                    detail.setVisibility(View.VISIBLE);
                    simple.setVisibility(View.GONE);
                }
                isAnimating = false;
                if (thanSmooth) {
                    if (subView != mSubCard[mSubCard.length - 1]) {
                        CardView nextCard = getNextCard(subView);
                        //通知下一个翻转&延迟500毫秒开始滑动数据
                        mHandler.sendMessageDelayed(Message.obtain(mHandler, OVER_AND_SMOOTH, nextCard), ANIMA_DELAY);
                    } else {
                        mHandler.removeMessages(OVER_AND_SMOOTH);
                    }
                    mHandler.sendMessageDelayed(Message.obtain(mHandler, SMOOTH_CARD, subView), ANIMA_DELAY);
                } else {
                    if (subView == mSubCard[mSubCard.length - 1]) {
                        //开始翻转滑动
                        mHandler.sendMessageDelayed(Message.obtain(mHandler, RESTART), ANIMA_DELAY);
                    } else {
                        //只滑动
                        mHandler.sendMessageDelayed(Message.obtain(mHandler, JUST_OVERTURN, getNextCard(subView)), ANIMA_DELAY);
                    }
                }
            }
        });
        animator.start();
    }

    //刷新股票信息
    /*public void updateStockInfo(String stockId) {
        if (!stockIds.contains(stockId)) {
            return;
        }
        if (isTouching) {
            //触摸时 修改数据会导致界面重绘,延迟一秒通知刷新
            mHandler.sendMessageDelayed(Message.obtain(mHandler, UPDATE_STOCK, stockId), 1000);
            return;
        }
        String stockId1 = ((TextView) cvFirst.findViewById(R.id.tv_stock_id)).getText().toString();
        String stockId2 = ((TextView) cvLast.findViewById(R.id.tv_stock_id)).getText().toString();
//        RegistHQZCBean bean = StkStockPrice.getStockPriceflag(stockId);
//        if (bean == null) {
//            //重新注册
//            mNetCenter.sendHQRequest(stockId, tokenId);
//        } else {
//            //获取数据
//            String[] strings = mFormatUtil.formatStock(bean);
//            //根据显示的卡片的 股票id进行刷新
//            if (TextUtils.equals(stockId, stockId1)) {
//                ((TextView) cvFirst.findViewById(R.id.tv_stock_name)).setText(bean.getStock_name());
//                ((TextView) cvFirst.findViewById(R.id.tv_percent)).setText(strings[1]);
//            } else if (TextUtils.equals(stockId, stockId2)) {
//                ((TextView) cvLast.findViewById(R.id.tv_stock_name)).setText(bean.getStock_name());
//                ((TextView) cvLast.findViewById(R.id.tv_percent)).setText(strings[1]);
//            } else {
//                for (CardView cardView : mSubCard) {
//                    if (cardView == null) {
//                        mHandler.sendMessageDelayed(Message.obtain(mHandler, UPDATE_STOCK, stockId), 1000);
//                        continue;
//                    }
//                    String stock_Id = ((TextView) cardView.findViewById(R.id.tv_stock_id)).getText().toString();
//                    if (TextUtils.equals(stock_Id, stockId)) {
//                        ((TextView) cardView.findViewById(R.id.tv_stock_name)).setText(bean.getStock_name());
//                        ((TextView) cardView.findViewById(R.id.tv_percent)).setText(strings[1]);
//                        break;
//                    }
//                }
//            }
//        }
    }*/

    //设置所有数据
    public void setData(List<Object> settingses) {
        clearCache();
        mHandler.removeCallbacksAndMessages(null);
        if (settingses != null && !settingses.isEmpty()) {
            mLists = new List[settingses.size()];
            for (int i = 0; i < settingses.size(); i++) {
                //放入集合
                if (mLists[i] == null) {
                    mLists[i] = new ArrayList();
                    //Log.d("panc", "init List order" + (orderId - 1));
                }
                mLists[i].add("test"+i);
                mLists[i].add("test2"+i);
                mLists[i].add("test3"+i);
            }
            restart();
        } else {
            mTimeMillis = System.currentTimeMillis();
            initCardGroup();
            showData(mLists);
        }
    }

    private void restart() {
        initCardGroup();
        showData(mLists);
        //开始翻转,滑动第一个大卡片
        interceptHander = false;
        mHandler.sendMessageDelayed(Message.obtain(mHandler, RESTART), 2000);
    }

    private void clearCache() {
        isTouching = false;
        isAnimating = false;
        mMessageList.clear();
        needSmooth.clear();
        mSmoothAnimateFlag.clear();
        mPositionMap.clear();
        mRotationAnimateFlag.clear();
        mBooleanMap.clear();
        mMessageList.clear();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void initCardGroup() {
        if (this.getChildCount() > 0) {
            this.removeAllViews();
        }
        //计算宽高单位
        int windowWidth = MUtil.getWindowWidth(mContext);
        mMargin8 = MUtil.dp2px(8);
        mPercent33 = (windowWidth - 4 * mMargin8) / 3;
        LayoutParams params;
        mSubCard = new CardView[5];
        for (int i = 0; i < 7; i++) {
            if (i == 0) {
                cvFirst = (CardView) View.inflate(getContext(), R.layout.layout_big_card_left_up, null);
                params = new LayoutParams(mPercent33 * 2 + mMargin8, mPercent33);
                params.setMargins(mMargin8, mMargin8, 0, 0);
                this.addView(cvFirst, params);
            } else if (i == 6) {
                cvLast = (CardView) View.inflate(getContext(), R.layout.layout_big_card_right_down, null);
                params = new LayoutParams(mPercent33 * 2 + mMargin8, mPercent33);
                params.setMargins(mMargin8 * 2 + mPercent33, mMargin8 * 3 + mPercent33 * 2, 0, mMargin8);
                this.addView(cvLast, params);
            } else {
                CardView subCard = (CardView) View.inflate(getContext(), R.layout.layout_sub_card_single, null);
                params = new LayoutParams(mPercent33, mPercent33);
                switch (i) {
                    case 1:
                        params.setMargins(mMargin8 * 3 + mPercent33 * 2, mMargin8, 0, 0);
                        subCard.setCardBackgroundColor(ResourceUtil.getColor(R.color.red_default));
                        break;
                    case 2:
                        params.setMargins(mMargin8 * 3 + mPercent33 * 2, mMargin8 * 2 + mPercent33, 0, 0);
                        subCard.setCardBackgroundColor(ResourceUtil.getColor(R.color.yellow_default));
                        break;
                    case 3:
                        params.setMargins(mMargin8 * 2 + mPercent33, mMargin8 * 2 + mPercent33, 0, 0);
                        subCard.setCardBackgroundColor(ResourceUtil.getColor(R.color.blue_default));
                        break;
                    case 4:
                        params.setMargins(mMargin8, mMargin8 * 2 + mPercent33, 0, 0);
                        subCard.setCardBackgroundColor(ResourceUtil.getColor(R.color.blue_default));
                        break;
                    case 5:
                        params.setMargins(mMargin8, mMargin8 * 3 + mPercent33 * 2, 0, 0);
                        subCard.setCardBackgroundColor(ResourceUtil.getColor(R.color.gray_88));
                        break;
                }
                this.addView(subCard, params);
                subCard.setOnTouchListener(dragTouchListener);
                //设置指向
                mSubCard[i - 1] = subCard;
            }
            mParamsMap.put(i + 1, params);
        }

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //绘制结束,保存位置信息
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // getLayoutParams().height = MethodUtil.getWindowWidth(mContext);
                // requestLayout();
                for (int i = 0; i < mSubCard.length; i++) {
                    locationMap.put(i + 2, new Integer[]{mSubCard[i].getLeft(), mSubCard[i].getTop(), mSubCard[i].getRight(), mSubCard[i].getBottom()});
                }
            }
        });
    }

    public void showData(List<Object>[] orderList) {
        if (orderList == null || orderList.length == 0) {
            hasCardData = false;
            for (int i = 0; i < 7; i++) {
                int order_id = i + 1;
                switch (order_id) {
                    case 1:
                        setBigCardData(null, cvFirst);
                        break;
                    case 7:
                        setBigCardData(null, cvLast);
                        break;
                    default:
                        CardView cardView = mSubCard[order_id - 2];
                        setSubCardData(null, cardView);
                        break;
                }

            }
            return;
        }
        hasCardData = true;
        List<Object> newConfigBeanList;
        Object bean = null;
        for (int i = 0; i < 7; i++) {
            if (i < orderList.length) {
                newConfigBeanList = orderList[i];
            } else {
                newConfigBeanList = null;
            }
            if (i == 0) {
                mDataMap.put(cvFirst, newConfigBeanList);
            } else if (i == 6) {
                mDataMap.put(cvLast, newConfigBeanList);
            } else {
                mDataMap.put(mSubCard[i - 1], newConfigBeanList);
            }
            //展示每个list第一个数据
            if (newConfigBeanList != null && !newConfigBeanList.isEmpty()) {
                bean = newConfigBeanList.get(0);
            } else {
                bean = null;
            }
            //根据排序设置数据
            int order_id= i + 1;
//            if (bean == null) {
//                order_id ;
//            } else {
//                order_id = bean.getORDER_ID();
//            }
            switch (order_id) {
                case 1:
                    setBigCardData(bean, cvFirst);
                    break;
                case 7:
                    setBigCardData(bean, cvLast);
                    break;
                default:
                    CardView cardView = mSubCard[order_id - 2];
                    setSubCardData(bean, cardView);
                    break;
            }
        }
    }
}
