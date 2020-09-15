package com.baymax.game.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;

import com.baymax.game.helper.SandDuneHelper;
import com.baymax.game.utils.Utils;


import androidx.annotation.Nullable;

/**
 * 沙堆游戏View
 * @Created by  Baymax on 2020/9/10.
 * Copyright © 2020/9/10 Hyperspace Technology(Beijing)Co.,Ltd. All rights reserved.
 * @e-mail 761241917@qq.com
 */
public class SandDuneGameView extends View implements Runnable, View.OnTouchListener {
    private SandDuneHelper helper;
    /**
     * 迭代时间间隔
     */
    public  int time_in_frame = 17;


    private Paint gridPaint,sandPaint;

    /**
     * 图表中的原始触摸矩阵
     */
    private Matrix mMatrix ;

    /**
     * 用于存放矩阵的9个值
     */
    private final float[] matrixValues = new float[9];

    /**
     * 网格轴背景颜色
     */
    private int axisBackgroundColor= Color.parseColor("#05000000");



    /**
     * 放大缩小手势
     */
    private ScaleGestureDetector scaleGestureDetector;
    /**
     * 颜色数组
     */
    private int[] mSandColor={
            Color.parseColor("#00000000"),
            Color.parseColor("#44FF0000"),
            Color.parseColor("#88FF0000"),
            Color.parseColor("#FF00FF00"),
    };
    private int width;

    private int height;

    /**
     * 当前轴的数量
     *
     * 也为最大边界坐标
     */
    private int  axisNumber;
    /**
     * 当前网格数量
     */
    private int gridNumber;

    /**
     * 网格的大小 同时也是单位长度
     */
    private int mSize = 3;
    private char[][] sendDune;
    /**
     * 执行的代数
     */
    private int iterationCount;
    /**
     * 工作线程控制
     */
    private boolean mIsDrawing=false;
    /**
     * 是否暂停
     */
    private boolean isPause=false;
    /**
     * 正在绘制时需要暂停计算
     */
    private boolean isDrawPause=false;

    /**
     * 是否只绘制发生改变的部分
     */
    private boolean isDrawChange=false;
    /**
     * 工作线程
     */
    private Thread thread;
    private int mTouchSlop;
    /**
     * 最大放大倍数
     */
    public static final float SCALE_MAX = 20f;
    /**
     * 最小缩放倍数
     */
    private float initScale = 0.5f;




    public SandDuneGameView(Context context) {
        this(context,null,0);
    }

    public SandDuneGameView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SandDuneGameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        gridPaint =new Paint();
        gridPaint.setAntiAlias(true);
        gridPaint.setColor(axisBackgroundColor);
        gridPaint.setStrokeWidth(0.01f);

        sandPaint=new Paint();
        sandPaint.setAntiAlias(true);
        sandPaint.setStrokeWidth(mSize/2);
        sandPaint.setColor(mSandColor[0]);

        width = Utils.getDisplayWidth(getContext());
        height = Utils.getDisplayHeight(getContext());

        initAxis();
        initScale();
        helper=new SandDuneHelper();

        helper.createGame(axisNumber,axisNumber);

        thread=new Thread(this);

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        mMatrix=getMatrix();
    }

    private void initAxis() {
        axisNumber= (int) (height / mSize);

        gridNumber=(axisNumber-1)*(axisNumber-1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        isDrawPause=true;
        long startTime = System.currentTimeMillis();
        drawGrid(canvas);
        drawSendDune(canvas);

        long endTime = System.currentTimeMillis();
        Log.i("time","绘制耗时="+(endTime-startTime));
        isDrawPause=false;
    }
    /**
     * 绘制网格背景
     * @param canvas 画布
     */
    private void drawGrid(Canvas canvas) {
        for(int i=0, v = 0,h = 0;i<axisNumber;i++){
            canvas.drawLine(0,  v,  axisNumber*mSize, v, gridPaint);
            canvas.drawLine(h, 0, h, axisNumber*mSize, gridPaint);
            v+= mSize;
            h+= mSize;
        }
    }
    /**
     * 绘制沙堆
     * @param canvas
     */
    RectF mRectF=new RectF();
    private void drawSendDune(Canvas canvas) {
        if (sendDune==null)return;
        for (int x = 0; x < sendDune.length; x++) {
            for (int y = 0; y < sendDune[x].length; y++) {
                if (sendDune[x][y]==0)continue;
                if (isDrawChange&&helper.getLastSendDune()!=null&&sendDune[x][y]==helper.getLastSendDune()[x][y]){
                    //只绘制发生改变的坐标
                    continue;
                }
                if (sendDune[x][y]<mSandColor.length&&sendDune[x][y]>=0) sandPaint.setColor(mSandColor[sendDune[x][y]]);
                mRectF.set(x* mSize,
                        y* mSize,
                        x* mSize + mSize,
                        y* mSize + mSize);
                canvas.drawRoundRect(mRectF,
                        x* mSize,y* mSize,
                        sandPaint);
            }
        }
    }

    @Override
    public void run() {
        while (mIsDrawing){
            iterationCount++;
            /*取得刷新之前的时间**/
            long startTime = System.currentTimeMillis();
            nextSandDune();
            long endTime = System.currentTimeMillis();
            Log.i("time","计算下一代耗时="+(endTime-startTime));

            /*计算出一次更新的毫秒数**/
            int diffTime  = (int)(endTime - startTime);

            while(time_in_frame!=0&&diffTime <=time_in_frame) {
                diffTime = (int)(System.currentTimeMillis() - startTime);
                /*线程等待**/
                Thread.yield();
            }
            while (isPause||isDrawPause){
                Thread.yield();
            }
        }
    }

    /**
     * 计算下一代沙堆
     */
    private void nextSandDune() {
        sendDune=helper.nextSandDune();
        postInvalidate();
    }
    /**
     * 开始迭代
     */
    public void start(){
        isPause=false;
        if (mIsDrawing){
            return;
        }
        mIsDrawing=true;
        thread.start();
    }
    /**
     * 获得当前的缩放比例
     * @return float
     */
    public final float getScale() {
        mMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }


    float x = 0,y=0;
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        // 拿到触摸点的个数
        final int pointerCount = event.getPointerCount();
        if (pointerCount>1){
            return scaleGestureDetector.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x=event.getX();
                y=event.getY();
                Log.i("ACTION_MOVE","ACTION_DOWN dx="+x+",dy="+y);
                break;
            case MotionEvent.ACTION_MOVE:

                float dx = event.getX() - x;
                float dy = event.getY() - y;
                if (isCanDrag(dx,dy)){
                    x=event.getX();
                    y=event.getY();
                    Log.i("ACTION_MOVE","dx="+dx+",dy="+dy);
                    mMatrix.postTranslate(dx, dy);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        setAnimationMatrix(mMatrix);
                    }

                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                x=0;
                y=0;
                break;
        }
        return true;
    }

    private void initScale(){
        setOnTouchListener(this);

        scaleGestureDetector=new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scale = getScale();
                float scaleFactor = detector.getScaleFactor();
                /*
                 * 缩放的范围控制
                 */
                if ((scale < SCALE_MAX && scaleFactor > 1.0f) || (scale > initScale && scaleFactor < 1.0f)) {
                    /*
                     * 最大值最小值判断
                     */
                    if (scaleFactor * scale < initScale) {
                        scaleFactor = initScale / scale;
                    }
                    if (scaleFactor * scale > SCALE_MAX) {
                        scaleFactor = SCALE_MAX / scale;
                    }
                    /*
                     * 设置缩放比例
                     */
                    mMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        setAnimationMatrix(mMatrix);
                    }
                }

                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;//记得改成ture；
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {

            }

        });

    }

    /**
     * 是否是推动行为
     *
     * @param dx x
     * @param dy y
     * @return boolean
     */
    private boolean isCanDrag(float dx, float dy) {
        return Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
    }

    public void setPause(boolean b) {
        this.isPause=b;
    }

    public void setRandom() {
        helper.randomData(sendDune);
    }

    public void setIsDrawChange(boolean isDrawChange) {
        this.isDrawChange = isDrawChange;
    }
    public boolean isDrawChange() {
        return isDrawChange;
    }

    public int getTime_in_frame() {
        return time_in_frame;
    }

    public void setTime_in_frame(int time_in_frame) {
        this.time_in_frame = time_in_frame;
    }

    public void close() {
        mIsDrawing=false;
        thread.interrupt();
    }
}
