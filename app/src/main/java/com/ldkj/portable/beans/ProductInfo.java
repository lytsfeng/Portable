package com.ldkj.portable.beans;

import com.ldkj.portable.exceptions.CustomException;

public class ProductInfo {
	public String 	manufacturer;//制造商
	public String  	product_type;//产品型号
	public String	product_serial_number;//产品序列号;
	public String   software_version; // 软件版本号
	public String	IP;//设备IP
	public String	port;//设备端口号
	
	
	public void setParam(String pParamStr) throws CustomException{
		String[] _Params = pParamStr.split(",");
		if(_Params.length == 0 && _Params.length == 6){
			throw(new CustomException("参数设置错误"));
		}
		this.manufacturer = _Params[0];
		this.product_type = _Params[1];
		this.product_serial_number = _Params[2];
		this.software_version = _Params[3];
		this.IP = _Params[4];
		this.port = _Params[5];
	}
	
	
	
	
}
