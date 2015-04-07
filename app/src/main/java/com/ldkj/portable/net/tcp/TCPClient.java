package com.ldkj.portable.net.tcp;

import android.os.Handler;
import android.util.Log;

import com.ldkj.portable.tools.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by john on 15-3-11.
 */
public class TCPClient implements Runnable{
    private String address = "192.168.100.232";
    private int port = 65000;
    private int timeout = 5000;

    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean isconn = false;
    private Timer checkNetTimer = null;
    private Handler handler;
    private Socket socket;

    public TCPClient(Handler handler) {
        this.handler = handler;
    }

    public TCPClient(Handler handler, String address, int port) {
        this(handler);
        this.address = address;
        this.port = port;
    }

    public TCPClient(Handler handler, String address, int port, int timeout) {
        this(handler, address, port);
        this.timeout = timeout;
    }

    public void conn() {

        while (!isconn) {
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(InetAddress.getByName(address), port), timeout);
                socket.setKeepAlive(true);
                socket.setSoTimeout(5000);
                int i = socket.getReceiveBufferSize();
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
                isconn = true;
                handler.sendEmptyMessage(Util.MSG_NET_OK);
            } catch (IOException e) {
                e.printStackTrace();
                socket = null;
                handler.sendEmptyMessage(Util.MSG_NET_ERROR);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void startTimer() {
        stopTimer();
        checkNetTimer = new Timer();
        checkNetTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkNet();
            }
        }, 1000, 1000);
    }

    private void stopTimer() {
        if (checkNetTimer != null) {
            checkNetTimer.cancel();
            checkNetTimer = null;
        }
    }

    public boolean isConn() {
        return isconn;
    }

    public synchronized void close() {
        Log.wtf("ldkjlog", "socket close");
        stopTimer();
        handler.sendEmptyMessage(Util.MSG_NET_ERROR);
        if (isConn()) {
            isconn = false;
            try {
                outputStream.close();
                outputStream = null;
                inputStream.close();
                outputStream = null;
                socket.close();
                socket = null;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean sendCmd(String pCmd) {
        if (!isConn()) {
            return false;
        }
        if (pCmd.indexOf("\n") == -1) {
            pCmd += "\n";
        }
        try {
            byte[] _messageBuffer = pCmd.getBytes("ASCII");
            outputStream.write(_messageBuffer);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            resetConn();
            return false;
        }
        return true;
    }


    public byte[] readCmd(String pCmd) {
        byte[] _Data = null;
        if (!sendCmd(pCmd)) {
            return _Data;
        }
        try {
            int _DataLength = 0;
            byte[] buf = new byte[10240];
            if (readData(buf, 0, 2) == 0) {
                return _Data;
            }
            if (buf[0] == 35) {

                if(buf[1] < 48 || buf[1] > 57){
                    return _Data;
                }
                int _tmpLen = Integer.parseInt(((char) buf[1])+"");
                if (readData(buf, 0, _tmpLen) == 0) {
                    return _Data;
                }
                String _Str = new String(buf, 0, _tmpLen);
                if (!Util.isNumeric(_Str))
                    return _Data;
                _DataLength = new Integer(_Str.trim()) + 2;
                int _Rec = 0;
                int _PerIndex = 0;
                while (_DataLength > 0) {
                    _Rec = readData(buf, _PerIndex, _DataLength);
                    if (_Rec == 0) {
                        return _Data;
                    }
                    _DataLength -= _Rec;
                    _PerIndex += _Rec;
                }
                if (buf[_PerIndex - 2] == 13 && buf[_PerIndex - 1] == 10) {
                    _Data = new byte[_PerIndex - 2];
                    System.arraycopy(buf, 0, _Data, 0, _PerIndex - 2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            resetConn();
            return _Data;
        }
        return _Data;
    }

    private synchronized int readData(byte[] _buf, int _PerIndex, int _DataLength) throws IOException {
        int _Rec = 0;

        if (inputStream == null || !isconn) {
            return _Rec;
        }
        _Rec = inputStream.read(_buf, _PerIndex, _DataLength);
//        inputStream.reset();
        return _Rec;
    }

    private void checkNet() {
        try {
            socket.sendUrgentData(0xFF);
        } catch (Exception ex) {
            conn();
            handler.sendEmptyMessage(Util.MSG_NET_ERROR);
        }
    }


    public void resetConn(){
        close();
        conn();
    }

    @Override
    public void run() {
        conn();
    }
}
