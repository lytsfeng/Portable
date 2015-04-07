package com.ldkj.portable.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ldkj.portable.R;
import com.ldkj.portable.beans.ProductInfo;

/**
 * 产品列表 adapter
 * 
 * @author zyf
 * @date 2015年02月09日
 */
public class ProductAdapter extends BaseAdapter {

	class Holder {
		public TextView manufacturer;// 制造商
		public TextView product_type;// 产品型号
		public TextView product_serial_number;// 产品序列号;
		public TextView software_version; // 软件版本号
		public TextView IP;// 设备IP
		public TextView port;// 设备端口号
	}

	private ArrayList<ProductInfo> products = null;
	private Context context = null;

	public ProductAdapter(Context pContext, ArrayList<ProductInfo> pProducts) {
		this.context = pContext;
		this.products = pProducts;
	}

	@Override
	public int getCount() {
		return products.size();
	}

	@Override
	public Object getItem(int position) {
		return products.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Holder _Holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_products, null);
			_Holder = new Holder();
			_Holder.manufacturer = (TextView) convertView
					.findViewById(R.id.item_product_manufacturer);
			_Holder.product_type = (TextView) convertView
					.findViewById(R.id.item_product_product_type);
			_Holder.product_serial_number = (TextView) convertView
					.findViewById(R.id.item_product_product_serial_number);
			_Holder.software_version = (TextView) convertView
					.findViewById(R.id.item_software_version);
			_Holder.IP = (TextView) convertView
					.findViewById(R.id.item_product_IP);
			_Holder.port = (TextView) convertView
					.findViewById(R.id.item_product_port);
			convertView.setTag(_Holder);
		}
		_Holder = (Holder) convertView.getTag();
		ProductInfo _info = products.get(position);
		_Holder.manufacturer.setText(_info.manufacturer);
		_Holder.product_type.setText(_info.product_type);
		_Holder.product_serial_number.setText(_info.product_serial_number);
		_Holder.software_version.setText(_info.software_version);
		_Holder.IP.setText(_info.IP);
		_Holder.port.setText(_info.port);
		return convertView;
	}

}
