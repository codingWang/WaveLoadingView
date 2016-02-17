package com.waveloadingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 杜伟 on 2016/2/17.
 */
public class WavaLoadingView extends View {
    private List<Point> triPoints = new ArrayList<>();
    private static final int TRI_POINT_NUMBER = 17;
    private static final float TIME_STEP = 20;

    private float x = 550;
    private float y = 550;
    private float radius = 180;
    private float triStep = 16.875f;
    private float waveHeight = 15;
    private float smallRadius = 5;
    private int currentTime = 0;

    private Path triPath;
    private Paint triPaint;
    private Paint smallPaint;

    public WavaLoadingView(Context context) {
        super(context);
        init();
    }

    public WavaLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WavaLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        x = getWidth()/2;
        y = getHeight()/2;
    }

    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight()
                : getPaddingTop() + getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY) {//确切的尺寸就用测量得到的
            result = size;
        } else {///AT_MOST///UNSPECIFIED
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        loading(canvas);
    }

    private void init() {
        triPath = new Path();

        triPaint = new Paint();
        triPaint.setAntiAlias(true);
        triPaint.setStyle(Paint.Style.STROKE);
        triPaint.setStrokeWidth(10);
        triPaint.setColor(Color.parseColor("#2ea4f2"));

        smallPaint = new Paint();
        smallPaint.setAntiAlias(true);
        smallPaint.setStyle(Paint.Style.FILL);
        smallPaint.setColor(Color.parseColor("#2ea4f2"));

        initializePoints();
    }

    protected void initializePoints() {//初始化点
        for (int i = 0; i < TRI_POINT_NUMBER; i++) {
            Point point = new Point();
            point.x = (x - 3 * radius / 4) + triStep * i;//16.875*i
            point.y = y + calculateTri(TIME_STEP * i ,currentTime);//20*i
            triPoints.add(point);
        }
    }

    private float calculateTri(float originalTime, float currentTime) {
        return (float) (waveHeight * Math.sin((Math.PI / 80) * (originalTime + currentTime)));
    }

    private void loading(Canvas canvas) {
        Point currentPoint = triPoints.get(0);
        Point nextPoint;
        //遍历所有的点进行放置
        for (int i = 0; i < TRI_POINT_NUMBER; i++) {//17个点，从0开始
            Point p = triPoints.get(i);
            p.x = (x - 3 * radius / 4) + triStep * i;
            p.y = y + calculateTri(TIME_STEP * i, currentTime);
        }
        //连起所有的点
        for (int i = 1; i < TRI_POINT_NUMBER; i++) {//17个点，从1开始
            nextPoint = triPoints.get(i);
            triPath.reset();
            triPath.moveTo(currentPoint.x, currentPoint.y);
            triPath.lineTo(nextPoint.x, nextPoint.y);
            canvas.drawCircle(nextPoint.x, nextPoint.y, smallRadius, smallPaint);//为了让点看起来更圆滑？
            canvas.drawPath(triPath, triPaint);//连起来
            currentPoint = nextPoint;//后移
        }
        currentTime = (int) (currentTime + TIME_STEP);
        postInvalidateDelayed(20);
    }

    static class Point {
        public float x;
        public float y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public Point() {
            x = -1;
            y = -1;
        }
    }
}
