package com.android.callassistant.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.android.callassistant.R;
import com.android.callassistant.util.Log;

public class TabContainer extends FrameLayout implements OnClickListener, OnTouchListener {
    private static final String TAG = "TabContainer";
    private static final int STEPS = 5;
    private static final int DELAY_MIS = 10;
    private Button []mTabButtons = null;
    private OnTabChangeListener mOnTabChangeListener;
    private LinearLayout mButtonContainer;
    private LinearLayout mTabSlideContainer;
    private Button mSlideButton;
    private int mWidth;
    private int mHeight;
    private int mButtonWidth;
    private int mTabButtonCount;
    private int mTabIndex;
    private Handler mHandler;
    private SlideRunnable mSlideRunnable;
    
    private int mSlideRawX;
    private static final int[] sTabLabelRes = new int[]{R.string.call_log, R.string.black_name, R.string.call_log};
    
    public TabContainer(Context context) {
        super(context);
        init(context);
    }
    public TabContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public TabContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    private void init(Context context){
        mHandler = new Handler();
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mButtonContainer = (LinearLayout) findViewById(R.id.tab_button_container);
        mTabSlideContainer = (LinearLayout) findViewById(R.id.tab_slide_container);
        mSlideButton = (Button) findViewById(R.id.tab_slide_button);
//        mSlideButton.setOnTouchListener(this);
        mTabButtonCount = mButtonContainer.getChildCount();
        mTabButtons = new Button[mTabButtonCount];
        int buttonId = 0;
        for(int i = 0 ;i < mTabButtonCount; i++){
            buttonId = mButtonContainer.getChildAt(i).getId();
            mTabButtons[i] = (Button) findViewById(buttonId);
            mTabButtons[i].setOnClickListener(this);
            mTabButtons[i].setEnabled(true);
        }
        mTabButtons[0].setEnabled(false);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        if(displayMetrics != null){
            mWidth = displayMetrics.widthPixels;
            mButtonWidth = Math.round((float)mWidth / mTabButtonCount);
            Log.d(TAG, "mWidth = " + mWidth + " , mButtonWidth = " + mButtonWidth);
            slideButton();
        }
    }
    @Override
    public void onClick(View v) {
        int len = mTabButtons.length;
        for(int i = 0 ; i < len ; i++){
            Button button = mTabButtons[i];
            if(button.getId() == v.getId()){
                button.setEnabled(false);
                slideButton(i);
                if(mOnTabChangeListener != null){
                    mOnTabChangeListener.onClick();
                }
            }else{
                button.setEnabled(true);
            }
        }
    }
    public void setCurrentTab(int pos) {
        mTabIndex = pos;
        for(int i = 0 ;i < mTabButtonCount; i++){
            mTabButtons[i].setEnabled(true);
        }
        mTabButtons[mTabIndex].setEnabled(false);
        slideButton();
    }
    public void setOnTabChangeListener(OnTabChangeListener l){
        mOnTabChangeListener = l;
    }
    public interface OnTabChangeListener{
        public void onTabChange(int position);
        public void onClick();
    }
    private void slideButton(){
        mTabSlideContainer.scrollTo(-1 * mButtonWidth * mTabIndex, 0);
        mSlideButton.setText(sTabLabelRes[mTabIndex]);
    }
    private void slideButton(int newIndex){
        mSlideRunnable = new SlideRunnable(newIndex);
        mHandler.post(mSlideRunnable);
    }
    private class SlideRunnable implements Runnable{
        private int mNewIndex;
        private int mFinalX;
        private int mStepLen;
        public SlideRunnable(int newIndex){
            mNewIndex = newIndex;
            mFinalX = -1 * mButtonWidth * mNewIndex;
            int OldX = mTabSlideContainer.getScrollX();
            int dis = Math.abs(Math.abs(mFinalX) - Math.abs(OldX));
            mStepLen = dis / STEPS;
            mStepLen = Math.abs(mFinalX) > Math.abs(OldX) ? -1 * mStepLen : mStepLen;
        }
        @Override
        public void run() {
            int scrollX = mTabSlideContainer.getScrollX();
            int deltaX = Math.abs(Math.abs(mFinalX) - Math.abs(scrollX));
            Log.d(TAG, "scrollX = " + scrollX + " , mFinalX = " + mFinalX + " , deltaX = " + deltaX + " , mStepLen = " + mStepLen);
            if(deltaX <= Math.abs(mStepLen / 2)){
                mTabSlideContainer.scrollTo(mFinalX, 0);
                mTabIndex = mNewIndex;
                setSlideText();
                setNewIndex();
                return ;
            }
            mTabSlideContainer.scrollBy(mStepLen, 0);
            postDelayed(mSlideRunnable, DELAY_MIS);
        }
    }
    private void setSlideText(){
        mSlideButton.setText(sTabLabelRes[mTabIndex]);
    }
    private void setNewIndex(){
        if(mOnTabChangeListener != null){
            mOnTabChangeListener.onTabChange(/*mTabButtons[mTabIndex].getId()*/mTabIndex);
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        int curRawX;
        switch(action){
        case MotionEvent.ACTION_DOWN:
            mSlideRawX = (int) event.getRawX();
            break;
        case MotionEvent.ACTION_MOVE:
            curRawX = (int) event.getRawX();
            int deltaX = mSlideRawX - curRawX;
            mSlideRawX = curRawX;
            moveButton(deltaX);
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            releaseButton();
            break;
        default:
            break;
        }
        return false;
    }
    private void moveButton(int deltaX){
        Log.d(TAG, "deltaX = " + deltaX);
        int scrollX = mTabSlideContainer.getScrollX();
        if(deltaX < 0 && Math.abs(scrollX) + mButtonWidth > mWidth){
            return ;
        }
        if(deltaX > 0 && scrollX > 0){
            return ;
        }
        mTabSlideContainer.scrollBy(deltaX, 0);
        int newIndex = (Math.abs(scrollX) + mButtonWidth / 2) / mButtonWidth;
        if(newIndex != mTabIndex){
            mTabIndex = newIndex;
            setSlideText();
            setNewIndex();
        }
    }
    private void releaseButton(){
        int scrollX = mTabSlideContainer.getScrollX();
        int newIndex = (Math.abs(scrollX) + mButtonWidth / 2) / mButtonWidth;
        slideButton(newIndex);
    }

    public static class TabButton extends Button{
        public TabButton(Context context) {
            super(context);
        }
        public TabButton(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
        public TabButton(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }
        @Override
        public void setPressed(boolean pressed) {
            if(pressed){
                setTextColor(Color.RED);
            }else{
                setTextColor(Color.BLACK);
            }
            super.setPressed(pressed);
        }
    }
}