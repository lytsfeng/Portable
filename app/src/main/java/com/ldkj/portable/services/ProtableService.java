package com.ldkj.portable.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.ldkj.portable.PortableApplication;
import com.ldkj.portable.beans.DeviceConfig;
import com.ldkj.portable.net.tcp.TCPClient;
import com.ldkj.portable.net.udp.UDPServer;
import com.ldkj.portable.services.base.Callback;
import com.ldkj.portable.services.base.UDPCallBack;
import com.ldkj.portable.tools.DataConversion;
import com.ldkj.portable.tools.MyAudioTrack;
import com.ldkj.portable.tools.Util;
import com.ldkj.portable.xmlparsers.DeviceConfigParser;

import org.dom4j.DocumentException;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by john on 15-2-12.
 */
public class ProtableService extends Service implements UDPCallBack {

    private MyHandler handler;
    private Callback callback = null;
    private TCPClient client;
    private MyAudioTrack audioTrack;
    private UDPServer udpServer;
    private boolean isSound = true;

    @Override
    public void onCreate() {
        super.onCreate();
        this.handler = new MyHandler(this);
        initPlay();
        if (client == null) {
            try {
                DeviceConfig _Config = DeviceConfigParser.getInstance(Util.PATH_CONFIG).getConfig();
                client = new TCPClient(handler, _Config.serverIP, _Config.TcpPort);
                PortableApplication.getThreadPool().execute(client);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {

        Log.e("log", "services ondestroy");

        super.onDestroy();
        if (client != null) {
            client.sendCmd(Util.DELETEUDP);
            client.close();
            client = null;
        }
        if (udpServer != null) {
            udpServer.close();
            udpServer = null;
        }
        if (audioTrack != null) {
            audioTrack.release();
            audioTrack = null;
        }
    }

    /**
     * 往回调接口集合中添加一个实现类
     *
     * @param callback
     */
    public void addCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * 发送命令不需要回复
     *
     * @param pCmd
     * @return
     */
    public boolean SendCMD(String pCmd) {
        if (client != null)
            return client.sendCmd(pCmd);
        return false;
    }

    /**
     * 发送命令需回复
     *
     * @param pCmd
     * @return
     */
    public byte[] ReadCMD(String pCmd) {
        return client.readCMD(pCmd);
    }


    @Override
    public void udpDate(byte[] pDate) {
        if (client.isConn() && isSound)
            soundDataAnalysis(pDate);
    }
    
  
    public void setSound(boolean isSound) {
		this.isSound = isSound;
	}

	protected void soundDataAnalysis(Object obj) {
        byte[] _b = (byte[]) obj;
        ByteBuffer _BB = ByteBuffer.wrap(_b);
        _BB.order(ByteOrder.LITTLE_ENDIAN);
        _BB.position(20);
        try {
            playSound(_BB);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //播放声音
    private void playSound(ByteBuffer ioBuffer) throws UnsupportedEncodingException {
        short tag = ioBuffer.getShort();
        if (tag != 401) {
            return;
        }
        int _len = ioBuffer.getInt();
        String _ChannelStr = DataConversion.Byte2Hex(ioBuffer.get());
        String freqStr = ioBuffer.getLong() + "";
        int bw = ioBuffer.getInt();
        byte[] c = new byte[4];
        ioBuffer.get(c);
        int count = _len - 23;
        if (count < 0) {
            return;
        }
        byte[] radio = new byte[count];
        ioBuffer.get(radio);
        audioTrack.playAudioTrack(radio, 0, count);
//        ioBuffer.reset();
    }

    private void initPlay() {
        if (audioTrack == null) {
            audioTrack = new MyAudioTrack();
            audioTrack.init();
        }
    }

    class MyHandler extends Handler {
        private WeakReference<ProtableService> service;
        public MyHandler(ProtableService service, Looper looper) {
            super(looper);
            this.service = new WeakReference<ProtableService>(service);
        }
        public MyHandler(ProtableService service) {
            this.service = new WeakReference<ProtableService>(service);
        }
        @Override
        public void handleMessage(Message msg) {
            if (callback != null) {
                int _what = msg.what;
                switch (_what) {
                    case Util.MSG_NET_ERROR:
                        service.get().callback.netStatus(_what);
                        break;
                    case Util.MSG_NET_OK:
                        service.get().callback.netStatus(_what);
                        startUDPServer();
                        break;
                    default:
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    public final class LocalBinder extends Binder {
        public ProtableService getService() {
            return ProtableService.this;
        }
    }

    private void startUDPServer() {
        if (udpServer == null){
            try {
                udpServer = new UDPServer(9999, ProtableService.this);
                PortableApplication.getThreadPool().execute(udpServer);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

    }
    
    

}
