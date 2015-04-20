package com.ldkj.portable.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ldkj.portable.PortableApplication;
import com.ldkj.portable.R;
import com.ldkj.portable.beans.DeviceConfig;
import com.ldkj.portable.services.ProtableService;
import com.ldkj.portable.tools.Util;
import com.ldkj.portable.xmlparsers.DeviceConfigParser;
import com.ldkj.portable.xmlparsers.ParamParser;

import org.dom4j.DocumentException;

import java.io.IOException;

public class SettingActivity extends Activity {


    private EditText etIP, etUDP, etTCP;
    private Button btnOk, btnCancel;
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
        btnOk = (Button) findViewById(R.id.setting_ok);
        btnCancel = (Button) findViewById(R.id.setting_cancel);
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
        switch (id) {
            case R.id.setting_ok:

                config.serverIP = etIP.getText().toString();
                config.TcpPort = Integer.parseInt(etTCP.getText().toString().trim());
                config.UDPPort = Integer.parseInt(etUDP.getText().toString().trim());
                try {
                    DeviceConfigParser.getInstance(Util.PATH_CONFIG).saveConfig(config);
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        finish();
        PortableApplication.getThreadPool().execute(
                new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        startService(new Intent(SettingActivity.this, ProtableService.class));
                    }
                });
    }


}
