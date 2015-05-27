package com.ldkj.portable.net.tcp;

import android.os.Handler;
import android.util.Log;

import com.ldkj.portable.tools.Util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by john on 15-3-11.
 */
public class TCPClient implements Runnable{
    private String address = "192.168.100.232";
    private int port = 65000;
    private int timeout = 5000;

    private BufferedInputStream inputStream;
    private BufferedOutputStream outputStream;
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
                inputStream =  new BufferedInputStream(socket.getInputStream());
                outputStream = new BufferedOutputStream(socket.getOutputStream());
                isconn = true;
//                sendCmd(Util.DELETEUDP);
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


    protected void resetInput() {
       if(inputStream != null){
           try {
               int _len = inputStream.available();
               byte[] _buf = null;
               if (_len > 0) {
                   _buf = readTcpData(_len);
               }
           } catch (IOException e) {
               e.printStackTrace();
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
    public synchronized byte[] readCMD(String pCMD) {
        try{
            byte[] _Data = null;
            resetInput();
            if (sendCmd(pCMD)) {
                byte[] _HeadArray = new byte[7];
                if (readData(_HeadArray, 0, 1) == 1) {
                    if (_HeadArray[0] == 35) {
                        if (readData(_HeadArray, 0, 1) == 1) {
                            if (_HeadArray[0] > 48 && _HeadArray[0] < 57) {
                                int _dataOffset = Integer.parseInt(((char) _HeadArray[0]) + "");
                                if (_dataOffset == readData(_HeadArray, 0, _dataOffset)) {
                                    String _DataLengthStr = new String(_HeadArray, 0, _dataOffset).trim();
                                    if (Util.isNumeric(_DataLengthStr)) {
                                        int _DataLength = Integer.parseInt(_DataLengthStr) + 2;
                                        byte[] _buf = readTcpData(_DataLength);
                                        if (_buf != null) {
                                            if ((_buf[_DataLength - 2] == 13) && (_buf[_DataLength - 1] == 10)) {
                                                _Data = new byte[_DataLength - 2];
                                                System.arraycopy(_buf, 0, _Data, 0, _DataLength - 2);
                                                return _Data;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            resetInput();
            return null;
        }catch (Exception e){
            e.toString();
        }
        return null;
    }



    protected int readData(byte[] _buf, int _PerIndex, int _DataLength) {
        int _Rec = 0;
        if (inputStream == null || !isconn) {
            return _Rec;
        }
        try {
            _Rec = inputStream.read(_buf, _PerIndex, _DataLength);
        } catch (SocketTimeoutException e) {
            _Rec = 0;
        } catch (IOException e) {
            _Rec = 0;
        }
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


    protected byte[] readTcpData(int datalength) {
        byte[] _Data = new byte[datalength];
        int _index = 0;
        int readLength = 0;
        while (readLength != datalength) {
            int _recLen = datalength - readLength;
            int _rec = readData(_Data, _index + readLength, _recLen);
            if (_rec == 0) {
                readLength = datalength;
                _Data = null;
            } else {
                readLength += _rec;
            }
        }
        return _Data;
    }
}
