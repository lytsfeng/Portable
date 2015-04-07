package com.ldkj.portable.controls;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.ldkj.portable.activitys.Single;
import com.ldkj.portable.tools.CIOUtils;
import com.ldkj.portable.tools.DataConversion;
import com.ldkj.portable.tools.Util;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.Serializable;

/**
 * Created by john on 15-3-5.
 */
public class Spectrum implements Serializable {

    private String title;
    private XYSeries series;
    private XYMultipleSeriesDataset mDataset;
    private GraphicalView chart;
    private XYMultipleSeriesRenderer renderer;
    private Context context;
    private double xMax = 111.4;
    private double xMin = 71.4;


    private double centerFreq = 91.4;
    private double bandwidth = 20;
    private double step = 1;

    private int pointTotal = 200;


    public void setCenterFreq(double centerFreq) {
        this.centerFreq = centerFreq;
        setXLabel();
    }

    public void setBandwidth(double bandwidth) {
        this.bandwidth = bandwidth;
        setXLabel();
    }


    private void setXLabel() {

        xMax = centerFreq + bandwidth;
        xMin = centerFreq - bandwidth;
        step = (2 * bandwidth) / pointTotal;
        renderer.setXAxisMax(xMax);
        renderer.setXAxisMin(xMin);
    }

    public Spectrum(Context context) {
        this(context, null);
    }

    public Spectrum(Context context, String title) {
        this.context = context;
        this.title = title;
    }

    public GraphicalView getChart() {
        if (chart == null) {
            // 这个类用来放置曲线上的所有点，是一个点的集合，根据这些点画出曲线
            series = new XYSeries(title);
            // 创建一个数据集的实例，这个数据集将被用来创建图表
            mDataset = new XYMultipleSeriesDataset();
            // 将点集添加到这个数据集中
            mDataset.addSeries(series);
            // 以下都是曲线的样式和属性等等的设置，renderer相当于一个用来给图表做渲染的句柄
            int color = Color.GREEN;
            PointStyle style = PointStyle.CIRCLE;
            renderer = buildRenderer(color, style, true);
            // 设置好图表的样式
            setChartSettings(renderer, "X", "Y", xMin, xMax, -60, 60, Color.WHITE,
                    Color.WHITE);
            // 生成图表
            chart = ChartFactory.getLineChartView(context, mDataset, renderer);
        }
        return chart;
    }

    public void updateChart(Object pObj) {
//        setXLabel();
        series.clear();
        byte[] _byteArr = (byte[]) pObj;
        byte bLength = 2;
        byte[] bTemp = new byte[bLength];
        int _length = _byteArr.length / bLength;
        int _f = _length / pointTotal;
        double x = xMin;
        for (int iLoop = 0; iLoop < _length; iLoop += _f) {
            for (int jLoop = 0; jLoop < bLength; jLoop++) {
                bTemp[jLoop] = _byteArr[iLoop * bLength + jLoop];
            }
            double y = CIOUtils.getShort(bTemp, 0) / 100.0;
            x += step;
            series.add(x, y);
        }
//        chart.postInvalidate();
        chart.invalidate();

//        short[] _Value = (short[]) pObj;
//        int length = _Value.length;
//
//
//        for (int i = 0; i < length; i += _f) {
//            double y = _Value[i] / 100.0;
//
//        }
//        chart.invalidate();
        // 在数据集中添加新的点集
        //    mDataset.addSeries(series);
        // 视图更新，没有这一步，曲线不会呈现动态
        // 如果在UI主线程中，需要调用invalidate()，具体参考api
        // chart.invalidate();
//        chart.postInvalidate();
    }

    private XYMultipleSeriesRenderer buildRenderer(int color, PointStyle style, boolean fill) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

        // 设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(color);
        r.setPointStyle(style);
        r.setFillPoints(fill);
        r.setLineWidth(3);
        renderer.addSeriesRenderer(r);
        return renderer;
    }

    private void setChartSettings(XYMultipleSeriesRenderer renderer,
                                  String xTitle, String yTitle, double xMin, double xMax,
                                  double yMin, double yMax, int axesColor, int labelsColor) {
        // 有关对图表的渲染可参看api文档
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);

        float size = renderer.getPointSize();
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
        renderer.setShowGrid(true);
        renderer.setGridColor(Color.GREEN);
        renderer.setXLabels(4);
        renderer.setYLabels(4);
        renderer.setXTitle("频率");
        renderer.setYTitle("dBm");
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
        renderer.setXLabelsAlign(Paint.Align.CENTER);
        renderer.setZoomButtonsVisible(false);//设置可以缩放
      //  renderer.setPanLimits(new double[]{0,0, 0, 0});//设置拉动的范围
       // renderer.setZoomLimits(new double[]{1, 1, 1, 1});//设置缩放的范围
    //    renderer.setRange(new double[]{0d, 0d, 0d, 0d}); //设置chart的视图范围
        renderer.setPointSize((float) 2);
        renderer.setShowLegend(false);
        renderer.setZoomEnabled(false);
        renderer.setZoomEnabled(false);
        renderer.setFitLegend(true);
        renderer.setClickEnabled(false);


    }


}
