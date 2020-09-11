package com.baymax.game.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;

import com.baymax.game.helper.LifeGameHelper;
import com.baymax.game.utils.Utils;


/**
 * 生命游戏View （继承自View）
 * @Created by  Baymax on 2020/9/2.
 * Copyright © 2020/9/2 Hyperspace Technology(Beijing)Co.,Ltd. All rights reserved.
 * @e-mail 761241917@qq.com
 */
public class LifeGameView extends View implements Runnable,View.OnTouchListener {
    private LifeGameHelper gameHelper;
    /**
     * 放大缩小手势
     */
    private ScaleGestureDetector scaleGestureDetector;

    /**
     * 最大放大倍数
     */
    public static final float SCALE_MAX = 20f;
    /**
     * 最小缩放倍数
     */
    private float initScale = 0.5f;

    /**
     * 用于存放矩阵的9个值
     */
    private final float[] matrixValues = new float[9];

    /**
     * cell的大小 同时也是单位长度
     */
    private float mSize = 2f;

    /**
     * cell的颜色
     */
    private int mCellColor=Color.RED;
    /**
     * 绘制线程控制
     */
    private boolean mIsDrawing=false;
    /**
     * 图表中的原始触摸矩阵
     */
    private Matrix mMatrix ;
    /**
     * 网格背景画笔
     */
    private Paint gridPaint;
    /**
     * cell画笔
     */
    private Paint cellPaint;

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
     * 网格轴背景颜色
     */
    private int axisBackgroundColor=Color.parseColor("#05000000");

    /**
     * View 背景颜色
     */
    private int viewBackgroundColor=Color.WHITE;
    /**
     * 当代细胞数组
     */
    private int[][] cells;
    /**
     * 宽高
     */
    private int width;

    private int height;

    private int mTouchSlop;
    /**
     * 迭代次数
     */
    private int iterationCount=1;

    /**
     * 迭代方向 默认为正向
     */
    private boolean  iterationDirection=true;
    /**
     * 迭代时间间隔 单位 S(秒)
     */
    public static final int TIME_IN_FRAME = 34;
    /**
     * 是否暂停
     */
    private boolean isPause =true;

    private boolean isDrawPause =true;

    private Thread thread;
    private Handler mHandler;


    public LifeGameView(Context context) {
        this(context,null,0);
    }

    public LifeGameView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LifeGameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        mMatrix=getMatrix();
    }

    private void init(Context context) {
        gridPaint =new Paint();
        gridPaint.setAntiAlias(true);
        gridPaint.setColor(axisBackgroundColor);
        gridPaint.setStrokeWidth(0.01f);
        cellPaint=new Paint();
        cellPaint.setAntiAlias(true);
        cellPaint.setColor(mCellColor);

        width = Utils.getDisplayWidth(getContext());
        height = Utils.getDisplayHeight(getContext());

        initAxis();

        initScale();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mHandler=new Handler();


        gameHelper=new LifeGameHelper();

        cells= gameHelper.createGame(axisNumber,axisNumber);

        for (int i =0; i < axisNumber; i++) {
            gameHelper.addCell(axisNumber/2,i);
        }
        thread=new Thread(this);
    }

    private void initAxis() {
        axisNumber= (int) (height / mSize);

        gridNumber=(axisNumber-1)*(axisNumber-1);
    }



    /**
     * 绘制
     * @param canvas
     */
    private void drawUi(Canvas canvas) {
        canvas.drawColor(viewBackgroundColor);
        drawCells(canvas);
        drawGrid(canvas);
    }

    /**
     * 绘制cell
     * @param canvas
     */
    RectF mRectF=new RectF();
    private void drawCells(Canvas canvas) {

        if (cells==null)return;
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                if (cells[x][y]==3){
                    mRectF.set(x* mSize,y* mSize,x* mSize + mSize,y* mSize + mSize);
                    canvas.drawRoundRect(mRectF,x* mSize,y* mSize,cellPaint);
                }
            }
        }


    }

    /**
     * 开始迭代
     */
    public void start(){
//        mHandler.post(runnable);
        isPause=false;
        if (mIsDrawing){
            return;
        }
        mIsDrawing=true;
        thread.start();
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

    @Override
    protected void onDraw(Canvas canvas) {
        long startTime = System.currentTimeMillis();
        isDrawPause=true;
        drawUi(canvas);
        isDrawPause=false;
        long endTime = System.currentTimeMillis();
        Log.i("time","绘制耗时="+(endTime-startTime));
    }

    /**
     * 获得当前的缩放比例
     * @return
     */
    public final float getScale() {
        mMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }
    private void initScale(){
        setOnTouchListener(this);

        scaleGestureDetector=new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scale = getScale();
                float scaleFactor = detector.getScaleFactor();
                /**
                 * 缩放的范围控制
                 */
                if ((scale < SCALE_MAX && scaleFactor > 1.0f) || (scale > initScale && scaleFactor < 1.0f)) {
                    /**
                     * 最大值最小值判断
                     */
                    if (scaleFactor * scale < initScale) {
                        scaleFactor = initScale / scale;
                    }
                    if (scaleFactor * scale > SCALE_MAX) {
                        scaleFactor = SCALE_MAX / scale;
                    }
                    /**
                     * 设置缩放比例
                     */
                    mMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        setAnimationMatrix(mMatrix);
                    }else {
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

    /**
     * 移动时，进行边界判断
     */
    private void checkMatrixBounds(){

    }


    /**
     * 是否是推动行为
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isCanDrag(float dx, float dy) {
        return Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
    }

    @Override
    public void run() {
        while (mIsDrawing){
            iterationCount++;
            /*取得刷新之前的时间**/
            long startTime = System.currentTimeMillis();
            nextCell();

            long endTime = System.currentTimeMillis();
            Log.i("time","计算下一代耗时="+(endTime-startTime));
            while (isPause||isDrawPause){
                Thread.yield();
            }
        }
    }
    /**
     * 计算下一代
     */
    private void nextCell() {
        cells = gameHelper.calculationNextCell();
        postInvalidate();
    }

    public void clear(){
        mIsDrawing = false;
        thread.interrupt();
    }

    /**
     * 上一代
     */
    public void last() {
//        if (mIsDrawing){
//            return;
//        }
//        iterationCount--;
//        cells = gameHelper.lastHabit();
//        postInvalidate();
    }

    /**
     * 下一代
     */
    public void next() {
        if (mIsDrawing){
            return;
        }
        iterationCount++;
        nextCell();
    }

    /**
     * 是否是编辑模式
     * @param pause
     */
    public void setPause(boolean pause) {
        isPause = pause;
    }

    public boolean isPause() {
        return isPause;
    }


}
