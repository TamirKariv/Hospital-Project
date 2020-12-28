package com.example.hospital_proj;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import java.util.Random;

public class MyDrawView extends View {

    private static Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private RectF oval;
    private RectF oval_bigger;
    private RectF oval_smaller;


    public MyDrawView(Context c) {
        super(c);

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF000000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(8);
    }

    public MyDrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF000000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(8);
    }

    public MyDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF000000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(8);
    }

    public MyDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF000000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(8);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
        float width = (float) getScreenWidth();
        float height = (float) getScreenHeight();
        this.oval = new RectF((float) (width * 0.85), (float) (height * 0.75), (float) (width * 0.85 + 150), (float) (height * 0.75 + 150));
        this.oval_bigger = new RectF((float) (width * 0.005), (float) (height * 0.75), (float) (width * 0.005 + 150), (float) (height * 0.75 + 150));
        this.oval_smaller = new RectF((float) (width * 0.150), (float) (height * 0.75), (float) (width * 0.150 + 150), (float) (height * 0.75 + 150));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawOval(oval, mPaint);
        canvas.drawRect(oval_bigger, mPaint);
        canvas.drawRect(oval_smaller, mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (InsideOval(x, y)) {
            clear();
        }
        if (InsideOvalBigger(x, y)) {
            if (mPaint.getStrokeWidth() < 100) {
                mPaint.setStrokeWidth(mPaint.getStrokeWidth() * 110 / 100);
            }
        }
        if (InsideOvalSmaller(x, y)) {
            if (mPaint.getStrokeWidth() > 5) {
                mPaint.setStrokeWidth(mPaint.getStrokeWidth() * 90 / 100);
            }
        }

        // Clear screen when touching with 3 fingers.
        if (event.getPointerCount() == 3) {
            clear();
            return true;
        }
        // Change the color of the line randomly when touching with 2 fingers.
        else if (event.getPointerCount() == 2) {
            Random rnd = new Random();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mPaint.setARGB(255, rnd.nextInt(256), rnd.nextInt(256),
                    rnd.nextInt(256));

            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    Boolean InsideOval(float posX, float posY) {
        // Checks if the small circle shape is contained fully inside of the oval shape.
        return this.oval.contains(posX, posY);
    }

    Boolean InsideOvalBigger(float posX, float posY) {
        // Checks if the small circle shape is contained fully inside of the oval shape.
        return this.oval_bigger.contains(posX, posY);
    }

    Boolean InsideOvalSmaller(float posX, float posY) {
        // Checks if the small circle shape is contained fully inside of the oval shape.
        return this.oval_smaller.contains(posX, posY);
    }

    public void clear() {
        mBitmap.eraseColor(Color.TRANSPARENT);
        invalidate();
        System.gc();
    }
}