package com.ldkj.portable.net.udp;

import android.os.Handler;

import com.ldkj.portable.activitys.Base.ActivityFrame;
import com.ldkj.portable.services.base.Callback;
import com.ldkj.portable.services.base.UDPCallBack;
import com.ldkj.portable.tools.Util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by john on 15-2-14.
 */
public class UDPServer implements Runnable {
    private int bufferSize;
    protected DatagramSocket socket;
    private UDPCallBack callback;
    private boolean isStart = false;

    private int datalen = 0;
    private int recvLen = 0;
    private byte[] recvDate = null;

    public UDPServer(int port, int bufferSize, UDPCallBack callback) throws SocketException {
        this.bufferSize = bufferSize;
        this.socket = new DatagramSocket(port);
        this.callback = callback;
        isStart = true;
    }

    public UDPServer(int port, UDPCallBack callback) throws SocketException {
        this(port, 1024, callback);
    }

    @Override
    public void run() {
        byte[] _buffer = new byte[bufferSize];
        DatagramPacket _inComing = new DatagramPacket(_buffer, bufferSize);
        while (isStart) {
            try {
                socket.receive(_inComing);
                dataAnalysis(_inComing.getData(), _inComing.getLength());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void close() {
        isStart = false;
    }
    private synchronized void dataAnalysis(byte[] data, int pDatalen) {
        if (datalen == 0) {
            datalen = Util.isHead(data);
            if (datalen > 0) {
                recvDate = new byte[datalen];
                recvLen = 0;
            }
        }
        if (recvDate != null) {
            int _l = recvDate.length;
            if((recvLen + pDatalen) >_l){
                recvLen = 0;
                return;
            }

            System.arraycopy(data, 0, recvDate, recvLen, pDatalen);
            recvLen += pDatalen;
            if ((recvLen == datalen) && recvLen != 0) {
                recvLen = 0;
                datalen = 0;
                callback.udpDate(recvDate);
            }
        }
    }


}
