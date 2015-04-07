package com.ldkj.portable.tools;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


import com.ldkj.portable.R;

import java.io.File;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static final Boolean APP_DEBUG = false;

    public static final String KEY_TCP_RECEIVE_TYPE = "key_ReceiveType";

    public static final String CMD_TCP_NULL = "null";
    public static final String CMD_TCP_RECEIVE = "cmd_receive";  //参数查询命令回复
    public static final String CMD_TCP_RECEIVE_IQ = "cmd_receive_iq"; //获取IQ数据的查询回复
    public static final String CMD_TCP_RECEIVE_ITU = "cmd_receive_itu";//获取ITU数据的查询回复
    public static final String CMD_TCP_RECEIVE_SPEC = "cmd_receive_spec";//获取频谱数据的查询回复
    public static final String CMD_UDP_RECEIVE_DATA = "cmd_udp_receive";

    public static final int MSG_CMD_TCP_RECEIVE = 10010;  //参数查询命令回复消息
    public static final int MSG_CMD_TCP_RECEIVE_IQ = 10011; //获取IQ数据的查询回复消息
    public static final int MSG_CMD_TCP_RECEIVE_ITU = 10012;//获取ITU数据的查询回复消息
    public static final int MSG_CMD_TCP_RECEIVE_SPEC = 10013;//获取频谱数据的查询回复消息
    public static final int MSG_UPD_RECEIVE = 10014;
    public static final int MSG_UDP_RECEIVE1 = 10015;


    public static final int MSG_NET_ERROR = 20010;
    public static final int MSG_NET_OK = 20011;

    public static final short TAG_FSCAN = 101;
    public static final short TAG_MSCAN = 201;
    public static final short TAG_DSCAN = 301;
    public static final short TAG_AUDIO = 401;
    public static final short TAG_IFPAN = 501;
    public static final short TAG_IF = 901;


    // 文件夹路径
    public static final String EXTERNALSTORAGEDIRECTORY = Environment
            .getExternalStorageDirectory().getPath();
    public static final String FOLDER_PATH_ROOT = EXTERNALSTORAGEDIRECTORY
            + "/零点科技/";
    public static final String FOLDER_PATH_CONFIG = FOLDER_PATH_ROOT
            + "config/";
    public static final String APK_PATH = FOLDER_PATH_ROOT + "tts.apk";

    //文件路径
    public static final String PATH_CONFIG = FOLDER_PATH_CONFIG + "config.xml";
    public static final String PATH_SERIAL = FOLDER_PATH_CONFIG + "params.xml";


    public static final String VERTION = "*idn?\n"; // 版本询问命令
    public static final String OPTION = "*opt?\n"; // 选件命令
    public static final String FORMATBINARYPACK = "FORM PACK;:FORM:BORD SWAP\n"; // 定义接收机数据以二进制格式发送,并设定网络字节顺序，及高字节在高位
    public static final String FORMATASCIIPACK = "FORM ASC\n";

    public static final String QUERYCENTERFREQ = "SENS:FREQ?\n"; // 询问中心频率
    public static final String QUERYFSCANSTART = "FREQ:START?\n"; // 询问FSACN频率扫描开始频率
    public static final String QUERYFSCANSTOP = "FREQ:STOP?\n"; // 询问FSACN频率扫描结束频率
    public static final String QUERYFSCANSTEP = "FREQ:STEP?\n"; // 询问FSCAN频率扫描结步进
    public static final String QUERYDSCANSTART = "FREQ:DSC:START?\n"; // 询问DSCAN频率扫描开始频率
    public static final String QUERYDSCANSTOP = "FREQ:DSC:STOP?\n"; // 询问DSCAN频率扫描结束频率
    public static final String QUERYDSCANSTEP = "FREQ:DSC:STEP?\n"; // 询问DSCAN频率扫描结步进

    public static final String QUERYLEVEL = "SENS:DATA?\n"; // 查询电平ITU
    public static final String SPECTRUMDATA = "TRACe:DATA? IFPAN\n"; // 获取频谱数据
    public static final String SCANDATA = "TRACE:DATA? MTRACE\n"; // 获取扫描数据
    public static final String[] IQMDATAS = {"TRACe:DATA? IF 0\n",
            "TRACe:DATA? IF 1\n", "TRACe:DATA? IF 2\n", "TRACe:DATA? IF 3\n"};
    public static final String IQMDATA1 = "TRACe:DATA? IF 0\n"; // 获取IQ数据
    public static final String IQMDATA2 = "TRACe:DATA? IF 1\n";
    public static final String IQMDATA3 = "TRACe:DATA? IF 2\n";
    public static final String IQMDATA4 = "TRACe:DATA? IF 3\n";

    public static final String DELETEUDP = "TRAC:UDP:DEL ALL\n"; // 删除所有UDP选项
    public static final String STARTSCAN = "INIT\n"; // 开始扫描
    public static final String STOPSCAN = "ABORT\n"; // 停止扫描



    private static final String MAGIC = "EEEEEEEE";
    private static final String MINOR = "0100";
    private static final String MAJOR = "0200";


    /**
     * 根据传入的文件夹路径 创建文件夹，并返回当前文件夹得路径
     *
     * @param pFolderPath 文件夹得路径
     * @return 文件夹得路径
     * @author 张亚峰
     */
    public static String onCreateFolder(String pFolderPath) {
        File _file = new File(pFolderPath);
        boolean _flag = true;
        if (!_file.exists()) {
            _flag = _file.mkdir();
        }
        return _file.getPath();
    }
    /**
     * 验证数据头是否正确
     * @param pbuf
     * @return 返回数据长度
     */
    public static int isHead(byte[] pbuf) {

        int _Result = 0;
        if (pbuf.length < 20) {
            return _Result;
        }
        ByteBuffer in = ByteBuffer.wrap(pbuf);
        in.order(ByteOrder.LITTLE_ENDIAN);
        byte[] _magicArr = new byte[4];
        byte[] _minorArr = new byte[2];
        byte[] _majorArr = new byte[2];
        in.get(_magicArr);
        in.get(_minorArr);
        in.get(_majorArr);
        String _magic = DataConversion.ByteArrToHex(_magicArr);
        String _minor = DataConversion.ByteArrToHex(_minorArr);
        String _major = DataConversion.ByteArrToHex(_majorArr);
        if (MAGIC.equalsIgnoreCase(_magic)) {
            if (MAJOR.equalsIgnoreCase(_major) && MINOR.equalsIgnoreCase(_minor)) {
                in.position(16);
                _Result = in.getInt();
            }
        }
        return _Result;
    }

    /**
     * 判断字符串是否为全数字组成
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str.trim());
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }


    /**
     * 获取中心频率    转化为 Hz
     * @param context
     * @param pValue
     * @return
     */
    public static String getCenterFreq(Context context, String pValue) {
        int _Length = pValue.length();
        String _Unit = pValue.substring(_Length - 3, _Length);
        double freq = Double.valueOf(pValue.substring(0, _Length - 3));
        BigDecimal _BigDecimal = null;
        if (_Unit.equalsIgnoreCase(context.getResources().getString(R.string.ButtonTextMHZ))) {
            _BigDecimal = new BigDecimal(freq * 1000000);
        } else if (_Unit.equalsIgnoreCase(context.getResources().getString(R.string.ButtonTextGHZ))) {
            _BigDecimal = new BigDecimal(freq * 1000000000);
        } else if (_Unit.equalsIgnoreCase(context.getResources().getString(R.string.ButtonTextKHZ))) {
            _BigDecimal = new BigDecimal(freq * 1000);
        }
        return _BigDecimal.toString() + "Hz";
    }


    /**
     * 获取中心频率    转化为 Hz
     * @param context
     * @param pValue
     * @return
     */
    public static double getValue(Context context, String pValue) {
        int _Length = pValue.length();
        String _Unit = pValue.substring(_Length - 3, _Length);
        double freq = Double.valueOf(pValue.substring(0, _Length - 3));
        BigDecimal _BigDecimal = null;
        if (_Unit.equalsIgnoreCase(context.getResources().getString(R.string.ButtonTextMHZ))) {
            _BigDecimal = new BigDecimal(freq);
        } else if (_Unit.equalsIgnoreCase(context.getResources().getString(R.string.ButtonTextGHZ))) {
            _BigDecimal = new BigDecimal(freq * 1000);
        } else if (_Unit.equalsIgnoreCase(context.getResources().getString(R.string.ButtonTextKHZ))) {
            _BigDecimal = new BigDecimal(freq / 1000);
        }
        return _BigDecimal.doubleValue();
    }


    /**
     * 通过IQ数据计算电平值
     * @param pDate
     * @return
     */
    public static double getLevel(byte[] pDate) {
        short[] _IQ = CIOUtils.getShort(pDate);
        int _len = _IQ.length / 2;
        double _TmpCount = 0;
        for(int i = 0; i < _len; i++) {
            _TmpCount += Math.pow(_IQ[i], 2) + Math.pow(_IQ[_len + i], 2);
        }
        return 10 * Math.log10(_TmpCount/(2*_len));
    }

    /**
     * 获取map 缓存和读取目录
     */
    public static String getSdCacheDir(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            java.io.File minimapDir = new java.io.File(
                    FOLDER_PATH_ROOT, "map");
            boolean result = false;
            if (!minimapDir.exists()) {
                result = minimapDir.mkdir();
            }
            return minimapDir.toString() + "/";
        } else {
            return "";
        }
    }

    public static double getDistatce(double lat1, double lat2, double lon1,    double lon2) {
        double R = 6371;
        double distance = 0.0;
        double dLat = (lat2 - lat1) * Math.PI / 180;
        double dLon = (lon2 - lon1) * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1 * Math.PI / 180)
                * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        distance = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * R;
        return distance;
    }

}
