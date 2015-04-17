package com.ldkj.portable.controls.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ldkj.portable.tools.CIOUtils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by john on 15-4-14.
 */
public class ChartSurfaceView extends SurfaceView implements SurfaceHolder.Callback {


    private ChartLine container;

    private Canvas canvas;
    Handler m = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            draw();
        }
    };
    private float oldTouchX = 0;
    private Timer onDrawTimer = null;
    private boolean isChange = false;
    private Timer timer;
    private ArrayList<Float> f = new ArrayList<>();

    public ChartSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        container = new ChartLine();
    }

    public ChartSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
        container = new ChartLine();
    }


    private void draw() {
        canvas = getHolder().lockCanvas();
        if (container != null) {
            container.onDraw(canvas);
        }
        getHolder().unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        startDraw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopDraw();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float _EventX = event.getX();
        float _EventY = event.getY();

        if (container.isSelect(_EventX, _EventY)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    oldTouchX = _EventX;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float _MoveX = _EventX - oldTouchX;
                    container.update(_MoveX);
                    isChange = true;
                    isdraw = false;
                    oldTouchX = _EventX;
                    break;
                default:
                    break;
            }
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    private boolean isdraw = true;
    public void startDraw() {
        if (onDrawTimer != null) {
            return;
        }
        onDrawTimer = new Timer();
        onDrawTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isChange && !isdraw) {
                    draw();
                    isChange = false;
                    isdraw = true;
                }
            }
        }, 5, 20);
    }

    public void stopDraw() {
        if (onDrawTimer != null) {
            onDrawTimer.cancel();
            onDrawTimer = null;
        }
    }


    private double centerFreq = 91.4;
    private double bandwidth = 20;

    public void setCenterFreq(double centerFreq) {
        this.centerFreq = centerFreq;
        setXLabel();
    }

    public void setBandwidth(double bandwidth) {
        this.bandwidth = bandwidth;
        setXLabel();
    }

    public void bindDate(Object object) {
        if (!isChange && isdraw) {
            byte[] _byteArr = (byte[]) object;

            byte bLength = 2;
            byte[] bTemp = new byte[bLength];
            int dateLength = _byteArr.length / bLength;
            ArrayList<Float> data = new ArrayList<>();
            for (int iLoop = 0; iLoop < dateLength; iLoop++) {
                for (int jLoop = 0; jLoop < bLength; jLoop++) {
                    bTemp[jLoop] = _byteArr[iLoop * bLength + jLoop];
                }
                data.add((float) (CIOUtils.getShort(bTemp, 0) / 100.0));
            }
            container.bindData(data);
            isChange = true;
            isdraw = false;
        }

    }


    private void setXLabel() {
        container.setxMaxValue((float) (centerFreq + bandwidth));
        container.setxMinValue((float) (centerFreq - bandwidth));
    }

}
