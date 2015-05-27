package com.ldkj.portable.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.ldkj.portable.PortableApplication;
import com.ldkj.portable.R;
import com.ldkj.portable.activitys.Base.ActivityBase;
import com.ldkj.portable.beans.DeviceConfig;
import com.ldkj.portable.services.ProtableService;
import com.ldkj.portable.tools.Util;
import com.ldkj.portable.xmlparsers.DeviceConfigParser;

import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingActivity extends ActivityBase {
    private static final String REGEXIP = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
    private EditText etIP, etUDP, etTCP;
    private DeviceConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        stopService(new Intent(this, ProtableService.class));
        initContorl();
        initConfig();
    }


    private void initContorl() {
        etIP = (EditText) findViewById(R.id.setting_server_ip);
        etTCP = (EditText) findViewById(R.id.setting_server_tcp_port);
        etUDP = (EditText) findViewById(R.id.setting_server_udp_port);


    }





    private void initConfig() {
        try {
            config = DeviceConfigParser.getInstance(Util.PATH_CONFIG)
                    .getConfig();
        } catch (DocumentException e) {
            e.printStackTrace();
            config = new DeviceConfig();
        }
        etIP.setText(config.serverIP);
        etUDP.setText(config.UDPPort + "");
        etTCP.setText(config.TcpPort + "");
    }


    public void onClick(View view) {
        int id = view.getId();
        boolean isTrue = false;
        switch (id) {
            case R.id.setting_ok:

                String _IP = etIP.getText().toString();
                String _tcp = etTCP.getText().toString().trim();
                String _upd = etUDP.getText().toString().trim();

                if(isIP(_IP) && isPort(_tcp,false)&& isPort(_upd,true)){
                    config.serverIP = _IP;
                    config.TcpPort = Integer.parseInt(_tcp);
                    config.UDPPort = Integer.parseInt(_upd);
                    try {
                        DeviceConfigParser.getInstance(Util.PATH_CONFIG).saveConfig(config);
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    isTrue = true;
                }
                break;
            default:
                isTrue = true;
                break;
        }
        if(isTrue){
            finish();
            close();
        }else {
            ShowCustonToast("填写错误,请重新填写");
        }

    }

    private void close() {
        PortableApplication.getThreadPool().execute(
                new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        startService(new Intent(SettingActivity.this, ProtableService.class));
                        startActivity(new Intent(SettingActivity.this, Single.class));
                    }
                });
    }


    private boolean isIP(String ip){
        Pattern _Pattern = Pattern.compile(REGEXIP);
        Matcher _Matcher = _Pattern.matcher(ip);
        boolean _isTrue = _Matcher.matches();

        if(_isTrue){
            etIP.setTextColor(Color.BLACK);
        }else {
            etIP.setTextColor(Color.RED);
        }


        return _isTrue;
    }

    private boolean isPort(String port,boolean isUpd){
        int _port = Integer.parseInt(port);
        boolean _isTrue = false;
        if(_port > 1 && _port < 65535){
            _isTrue = true;
        }
        if(_isTrue && isUpd){
           etUDP.setTextColor(Color.BLACK);
        }else if (_isTrue && !isUpd){
            etTCP.setTextColor(Color.BLACK);
        }else if (!_isTrue && isUpd){
            etUDP.setTextColor(Color.RED);
        }else if(!_isTrue && !isUpd){
            etTCP.setTextColor(Color.RED);
        }
        return _isTrue;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            close();
        }
        return false;
    }

}
