package com.ldkj.portable.xmlparsers;

import android.content.Context;

import com.ldkj.portable.R;
import com.ldkj.portable.beans.SingleBean;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * Created by john on 15-3-16.
 */
public class ParamParser extends XmlParser<SingleBean> {


    private static ParamParser paramParser = null;
    private Context context;

    private String centFreqStr;            //中心频率
    private String freqBandWidthStr;        //频谱带宽
    private String filterBandwidthStr;      //滤波带宽
    private String attcontrolStr;           //衰减控制
    private String demodulationModeStr;     //解调模式


    public static ParamParser getInstance(Context context, String pXmlPath) {
        if (paramParser == null) {
            paramParser = new ParamParser(context, pXmlPath);
        }


        return paramParser;
    }

    private ParamParser(Context context, String pXmlPath) {
        super(pXmlPath);
        this.context = context;
        centFreqStr = context.getResources().getString(R.string.center_freq);
        freqBandWidthStr = context.getResources().getString(R.string.freq_bandwidth);
        filterBandwidthStr = context.getResources().getString(R.string.filter_bandwidth);
        attcontrolStr = context.getResources().getString(R.string.attenuation_control);
        demodulationModeStr = context.getResources().getString(R.string.demodulation_mode);
    }

    @Override
    protected SingleBean getValue(Document pDocument) {
        Element _Element = pDocument.getRootElement();
        SingleBean _Bean = new SingleBean();
        _Bean.centFreq = _Element.elementText(centFreqStr);
        _Bean.freqBandWidth = _Element.elementText(freqBandWidthStr);
        _Bean.filterBandwidth = _Element.elementText(filterBandwidthStr);
        _Bean.attcontrol = _Element.elementText(attcontrolStr);
        _Bean.demodulationMode = _Element.elementText(demodulationModeStr);
        return _Bean;
    }

    @Override
    protected void setValue(Document pDocument, SingleBean pObj) {
        Element _Element = pDocument.getRootElement();

        _Element.element(centFreqStr).setText(pObj.centFreq);
        _Element.element(freqBandWidthStr).setText(pObj.freqBandWidth);
        _Element.element(filterBandwidthStr).setText(pObj.filterBandwidth);
        _Element.element(attcontrolStr).setText(pObj.attcontrol);
        _Element.element(demodulationModeStr).setText(pObj.demodulationMode);

    }

    @Override
    protected void saveValue(Document pDocument, SingleBean pObj) {
        Element _Element = pDocument.addElement("params");
        _Element.addElement(centFreqStr).setText(pObj.centFreq);
        _Element.addElement(freqBandWidthStr).setText(pObj.freqBandWidth);
        _Element.addElement(filterBandwidthStr).setText(pObj.filterBandwidth);
        _Element.addElement(attcontrolStr).setText(pObj.attcontrol);
        _Element.addElement(demodulationModeStr).setText(pObj.demodulationMode);
    }


}
