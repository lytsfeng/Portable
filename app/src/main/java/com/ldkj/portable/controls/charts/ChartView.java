package com.ldkj.portable.controls.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import com.ldkj.portable.tools.CIOUtils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by john on 15-4-14.
 */
public class ChartView{


    private ChartLine container;

    private float oldTouchX = 0;
    private Timer onDrawTimer = null;
    private boolean isChange = false;
    private boolean isdraw = true;
    private ChartV chartV;

    public ChartView() {
        container = new ChartLine();
    }

    public ChartV getChart(Context context,boolean isRemove){
        if(chartV == null && !isRemove){
            chartV = new ChartV(context);
        }
        return chartV;
    }
    public void destroyChart(){
        stopDraw();
        chartV = null;
    }
    public void startDraw() {
        if (onDrawTimer != null) {
            return;
        }
        onDrawTimer = new Timer();
        onDrawTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isChange && !isdraw) {
                    chartV.postInvalidate();
                    isChange = false;
                    isdraw = true;
                }
            }
        }, 5, 10);
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



    final class ChartV extends  View{
        public ChartV(Context context) {
            super(context);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (container != null) {
                container.onDraw(canvas);
            }
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


    }




}
