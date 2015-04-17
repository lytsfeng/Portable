package com.ldkj.portable.controls.charts;

import android.graphics.Canvas;

import java.util.ArrayList;

/**
 * Created by john on 15-4-14.
 */
public class ChartContainer {

    public static final float PADDING = 32;
    protected float x = PADDING, y = PADDING, width = 0, height = 0;
    protected float xMinValue = (float) 88, xMaxValue = (float) 108;
    protected float yMinValue = -40, yMaxValue = 100;
    private ArrayList<ChartContainer> containers = null;
    protected ArrayList<Float> pointList  = null;

    public ChartContainer() {
        this.containers = new ArrayList<>();
        pointList = new ArrayList<>();
    }

    public void addChild(ChartContainer pChild) {
        if (containers != null) {
            containers.add(pChild);
        }
    }

    public void removeChild(ChartContainer pChild) {
        if (containers != null) {
            containers.remove(pChild);
        }
    }

    public void onDraw(Canvas pCanvas) {
        pCanvas.save();
        width = pCanvas.getWidth() - PADDING;
        height = pCanvas.getHeight() - PADDING;
        onDrawCustomChild(pCanvas);
        pCanvas.restore();
    }

    public void bindData(ArrayList<Float> data){
       synchronized (pointList){
           pointList.clear();
           pointList.addAll(data);
       }
    }

    public void onDrawCustomChild(Canvas pCanvas){

    }




    public boolean isSelect(){
        return false;
    }
    public void setxMinValue(float xMinValue) {
        this.xMinValue = xMinValue;
    }

    public void setxMaxValue(float xMaxValue) {
        this.xMaxValue = xMaxValue;
    }

    public void setyMinValue(float yMinValue) {
        this.yMinValue = yMinValue;
    }

    public void setyMaxValue(float yMaxValue) {
        this.yMaxValue = yMaxValue;
    }
}
