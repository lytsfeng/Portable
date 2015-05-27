package com.ldkj.portable;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.ldkj.portable.beans.DeviceConfig;
import com.ldkj.portable.beans.SingleBean;
import com.ldkj.portable.services.ProtableService;
import com.ldkj.portable.tools.Util;
import com.ldkj.portable.xmlparsers.DeviceConfigParser;
import com.ldkj.portable.xmlparsers.ParamParser;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by john on 15-2-10.
 */
public class PortableApplication extends Application {

    private Handler handler;
    private static Context context;
    private static ExecutorService threadPool;


    @Override
    public void onCreate() {
        super.onCreate();
        InitFolder();
        InitXML();
        this.context = getApplicationContext();
        threadPool = Executors.newCachedThreadPool();
        threadPool.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				 startService(new Intent(PortableApplication.this, ProtableService.class));
			}
		});
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private void InitFolder() {
        Util.onCreateFolder(Util.FOLDER_PATH_ROOT);
        Util.onCreateFolder(Util.FOLDER_PATH_CONFIG);
    }

    private void InitXML() {
        try {
            DeviceConfigParser.getInstance(Util.PATH_CONFIG).initXML(new DeviceConfig());
            ParamParser.getInstance(this, Util.PATH_SERIAL).initXML(new SingleBean());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void sendMsg(int pMsg) {
        if (handler != null)
            handler.obtainMessage(pMsg).sendToTarget();
    }


    /**
     * 可以通过这里  全局获取context
     * @return
     */
    public static Context getContext(){
        return context;
    }

    /**
     * 获取线程池  全局的线程池
     * @return
     */
    public static ExecutorService getThreadPool(){
        return threadPool;
    }

}
