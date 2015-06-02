package com.ldkj.portable.controls.charts;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by john on 15-4-15.
 */
public class ChartLine extends ChartContainer {

    private HashMap<Float, Float> xMap = new HashMap<>();
    private HashMap<Float, Float> yMap  = null;
    private Paint  paint = null;
    private static final float MARKWIDTH = 60;
    private static final float MARKHIGHT = 60;
    private float startX,startY,endX,endY;
    private float lineX = -1;




    private HashMap<Float, Float> getxMap() {
        xMap.clear();
        float xFlag = (width + x) / 4;
        float xValueFlag = (xMaxValue - xMinValue) / 4;
        xMap.put(x, xMinValue);
        for (float i = 1; i < 4; i++) {
            float _x = i * xFlag;
            float _XValue = i * xValueFlag + xMinValue;
            xMap.put(_x, _XValue);
        }
        xMap.put(width, xMaxValue);
        return xMap;
    }

    private HashMap<Float, Float> getyMap() {
        if(yMap == null){
            yMap = new HashMap<>();
        }
        yMap.clear();
        float _Vlaue = yMaxValue - yMinValue;
        for(float i = yMinValue; i <= yMaxValue; i+= 20){
            yMap.put(getPoint(i - yMinValue),i);
        }
        return yMap;
    }


    private void onDrawBG(Canvas canvas){

        paint.setColor(Color.WHITE);
        Set<Float> xKey =  xMap.keySet();
        for(Float f : xKey){
            canvas.drawLine(f,y,f,height,paint);
            canvas.drawText(xMap.get(f)+"",f,height+20,paint);
        }
        Set<Float> yKey = yMap.keySet();
        for(Float f:yKey){
            canvas.drawLine(x, f, width, f, paint);
            canvas.drawText(yMap.get(f) + "", 0, f, paint);
        }
    }

    private void onDrawLine(Canvas canvas){
        synchronized (pointList){
            int _pointSize = pointList.size();
            float _pFlag = (width -x) / (_pointSize - 1);
            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(2);
            for(int i = 0; i < _pointSize -1; i++){
                canvas.drawLine(_pFlag * i + x,getYPixelFromValue(pointList.get(i)), _pFlag * (i + 1) + x, getYPixelFromValue(pointList.get(i+1)), paint);
            }
        }


        /**
         *  int _pointSize = pointList.size();
         float _pFlag = (width -x) / (_pointSize - 1);
         for(int i = 0; i < _pointSize -1; i++){
         canvas.drawLine(_pFlag * i + x,getYPixelFromValue(pointList.get(i)), _pFlag * (i + 1) + x, getYPixelFromValue(pointList.get(i+1)), paint);
         }
         */
    }

    protected float getYPixelFromValue(float value) {
        float _valueFlag = (height - y) / (yMaxValue - yMinValue);
        return height - (value - yMinValue) * _valueFlag;
    }




    @Override
    public void onDrawCustomChild(Canvas pCanvas) {
        xMap = getxMap();
        yMap = getyMap();
        if(paint == null){
            paint = new Paint();
        }
        paint.reset();
        pCanvas.drawColor(Color.BLACK);
        onDrawBG(pCanvas);
        onDrawLine(pCanvas);
        onDrawMarker(pCanvas);
    }



    ///////////////////////////

    private void onDrawMarker(Canvas canvas){
        setLineX(getPointCenter());
//        canvas.clipRect(startX, y, endX, height);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(3);
        canvas.drawLine(lineX, y, lineX, height, paint);
        paint.setColor(Color.YELLOW);
        paint.setAlpha(100);
        canvas.drawRect(startX, startY, endX, endY, paint);
        paint.reset();
        paint.setTextSize(25);
        paint.setColor(Color.BLACK);
        canvas.drawText(getValueCenter(), startX + MARKWIDTH / 2, startY + MARKHIGHT / 2, paint);
    }
    public void setLineX(float lineX) {
        if(lineX <= x){
            lineX = x;
        }else if(lineX >= width){
            lineX = width;
        }
        this.lineX = lineX;
        startX = lineX - MARKWIDTH;
        endX = lineX + MARKWIDTH;
        startY = height - MARKHIGHT;
        endY = height;
    }

    public void update(float moveX) {
        setLineX(lineX + moveX);
    }

    public boolean isSelect(float x, float y) {
        return x > startX && x < endX && y > startY
                && y < endY;
    }

    private String getValueCenter() {
        String _Value ="";
        Object _fValue = xMap.get(lineX);
        if(null != _fValue){
            _Value = String.format ("%.2f", _fValue);
        }else {
            float _pFlag = (xMaxValue - xMinValue) /(width - x) ;
            _Value = String.format ("%.2f", (lineX - x) * _pFlag + xMinValue);
        }
        return _Value;
    }
    private float getPoint(float vlaue){
        float _valueFlag =  (height - y) / (yMaxValue - yMinValue) ;
        return height -  vlaue * _valueFlag;
    }

    public float getPointCenter() {
        return lineX > -1 ? lineX : (width +x)/2;
    }



}
