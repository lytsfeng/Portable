package com.ldkj.portable.xmlparsers;


import android.content.Context;

import com.ldkj.portable.beans.DeviceConfig;

import org.dom4j.Document;
import org.dom4j.Element;


public class DeviceConfigParser extends XmlParser<DeviceConfig> {

    private static final String DEVICE_IP = "设备地址";
    private static final String DEVICE_TCP_PORT = "TCP端口";
    private static final String DEVICE_UDP_PORT = "UDP端口";


    private static DeviceConfigParser config = null;

    public static DeviceConfigParser getInstance(String pXmlPath) {
        if (config == null) {
            config = new DeviceConfigParser(pXmlPath);
        }
        return config;
    }

    private DeviceConfigParser(String pXmlPath) {
        super(pXmlPath);
//        initXML();
    }

    @Override
    protected DeviceConfig getValue(Document pDocument) {

        Element _RootEle = pDocument.getRootElement();
        DeviceConfig _ServerConfig = new DeviceConfig();
        _ServerConfig.serverIP = _RootEle.elementText(DEVICE_IP);
        _ServerConfig.TcpPort = Integer.parseInt(((String) _RootEle.elementText(DEVICE_TCP_PORT)).trim());
        _ServerConfig.UDPPort = Integer.parseInt((String) _RootEle.elementText(DEVICE_UDP_PORT).trim());
        return _ServerConfig;
    }

    @Override
    protected void setValue(Document pDocument, DeviceConfig pObj) {
        Element _RootEle = pDocument.getRootElement();
        _RootEle.element(DEVICE_IP).setText(pObj.serverIP);
        _RootEle.element(DEVICE_TCP_PORT).setText(pObj.TcpPort + "");
        _RootEle.element(DEVICE_UDP_PORT).setText(pObj.UDPPort + "");
    }

    @Override
    protected void saveValue(Document pDocument, DeviceConfig pObj) {
        Element _RootEle = pDocument.addElement("Device");
        _RootEle.addElement(DEVICE_IP).setText(pObj.serverIP);
        _RootEle.addElement(DEVICE_TCP_PORT).setText(pObj.TcpPort + "");
        _RootEle.addElement(DEVICE_UDP_PORT).setText(pObj.UDPPort + "");
    }
}
