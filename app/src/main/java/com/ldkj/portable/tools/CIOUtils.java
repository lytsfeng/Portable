package com.ldkj.portable.tools;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.io.*;
import java.util.*;

/**
 * 仅仅适用于 Java 与 C++ 通讯中，网络流解析与生成使用
 * 
 * 高低位互换(Big-Endian 大头在前 &amp; Little-Endian 小头在前)。
 * 举例而言，有一个4字节的数据0x01020304，要存储在内存中或文件中编号0&tilde;3字节的位置，两种字节序的排列方式分别如下：
 * 
 * <pre>
 * Big Endian 
 *   
 * 低地址                           高地址 
 * ----------------------------------------------------&gt; 
 * 地址编号 
 * |     0      |      1     |     2       |      3    | 
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ 
 * |     01     |      02    |     03      |     04    | 
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ 
 *  
 * Little Endian 
 *  
 * 低地址                           高地址 
 * ----------------------------------------------------&gt; 
 * 地址编号 
 * |     0      |      1     |     2       |      3    | 
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ 
 * |     04     |      03    |     02      |     01    | 
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 * 
 * Java则统一使用big模式 c中的unsigned short 对应着java中的char两个字节，无符号
 * c的无符号int,short,byte字节数组，相应转换成java的long,char,short
 * 
 * @author Snowolf
 * @version 1.0
 * @since 1.0
 */
public abstract class CIOUtils {
	public static final String CHARSET = "UTF-8";

	/**
	 * 从输入流中读布尔
	 * 
	 * @param is
	 * @return
	 * @throws java.io.IOException
	 */
	public static boolean readBoolean(DataInputStream is) throws IOException {
		return is.readBoolean();
	}

	/**
	 * 从流中读定长度字节数组
	 * 
	 * @param is
	 * @param i
	 * @return
	 * @throws java.io.IOException
	 */
	public static byte[] readBytes(DataInputStream is, int i)
			throws IOException {
		byte[] data = new byte[i];
		is.readFully(data);

		return data;
	}

	/**
	 * 从输入流中读字符
	 * 
	 * @param is
	 * @return
	 * @throws java.io.IOException
	 */
	public static char readChar(DataInputStream is) throws IOException {
		return (char) readShort(is);
	}

	/**
	 * 从输入流中读双精度
	 * 
	 * @param is
	 * @return
	 * @throws java.io.IOException
	 */
	public static double readDouble(DataInputStream is) throws IOException {
		return Double.longBitsToDouble(readLong(is));
	}

	/**
	 * 从输入流中读单精度
	 * 
	 * @param is
	 * @return
	 * @throws java.io.IOException
	 */
	public static float readFloat(DataInputStream is) throws IOException {
		return Float.intBitsToFloat(readInt(is));
	}

	/**
	 * 从流中读整型
	 * 
	 * @param is
	 * @return
	 * @throws java.io.IOException
	 */
	public static int readInt(DataInputStream is) throws IOException {
		return Integer.reverseBytes(is.readInt());
	}

	/**
	 * 从流中读长整型
	 * 
	 * @param is
	 * @return
	 * @throws java.io.IOException
	 */
	public static long readLong(DataInputStream is) throws IOException {
		return Long.reverseBytes(is.readLong());
	}

	/**
	 * 从流中读短整型
	 * 
	 * @param is
	 * @return
	 * @throws java.io.IOException
	 */
	public static short readShort(DataInputStream is) throws IOException {
		return Short.reverseBytes(is.readShort());
	}

	/**
	 * 从输入流中读字符串 字符串 结构 为 一个指定字符串字节长度的短整型+实际字符串
	 * 
	 * @param is
	 * @return
	 * @throws java.io.IOException
	 */
	public static String readUTF(DataInputStream is) throws IOException {
		short s = readShort(is);
		byte[] str = new byte[s];

		is.readFully(str);

		return new String(str, CHARSET);
	}

	/**
	 * 向输出流中写布尔
	 * 
	 * @param os
	 * @param b
	 * @throws java.io.IOException
	 */
	public static void writeBoolean(DataOutputStream os, boolean b)
			throws IOException {
		os.writeBoolean(b);
	}

	/**
	 * 向输出流中写字节数组
	 * 
	 * @param os
	 * @param data
	 * @throws java.io.IOException
	 */
	public static void writeBytes(DataOutputStream os, byte[] data)
			throws IOException {
		os.write(data);
	}

	/**
	 * 向输出流中写字符
	 * 
	 * @param os
	 * @param b
	 * @throws java.io.IOException
	 */
	public static void writeChar(DataOutputStream os, char b)
			throws IOException {
		writeShort(os, (short) b);
	}

	/**
	 * 向输出流中写双精度
	 * 
	 * @param os
	 * @param d
	 * @throws java.io.IOException
	 */
	public static void writeDouble(DataOutputStream os, double d)
			throws IOException {
		writeLong(os, Double.doubleToLongBits(d));
	}

	/**
	 * 向输出流中写单精度
	 * 
	 * @param os
	 * @param f
	 * @throws java.io.IOException
	 */
	public static void writeFloat(DataOutputStream os, float f)
			throws IOException {
		writeInt(os, Float.floatToIntBits(f));
	}

	/**
	 * 向输出流中写整型
	 * 
	 * @param os
	 * @param i
	 * @throws java.io.IOException
	 */
	public static void writeInt(DataOutputStream os, int i) throws IOException {
		os.writeInt(Integer.reverseBytes(i));
	}

	/**
	 * 向输出流中写长整型
	 * 
	 * @param os
	 * @param l
	 * @throws java.io.IOException
	 */
	public static void writeLong(DataOutputStream os, long l)
			throws IOException {
		os.writeLong(Long.reverseBytes(l));
	}

	/**
	 * 向输出流中写短整型
	 * 
	 * @param os
	 * @param s
	 * @throws java.io.IOException
	 */
	public static void writeShort(DataOutputStream os, short s)
			throws IOException {
		os.writeShort(Short.reverseBytes(s));
	}

	/**
	 * 向输出流中写字符串 字符串 结构 为 一个指定字符串字节长度的短整型+实际字符串
	 * 
	 * @param os
	 * @param str
	 * @throws java.io.IOException
	 */
	public static void writeUTF(DataOutputStream os, String str)
			throws IOException {
		byte[] data = str.getBytes(CHARSET);
		writeShort(os, (short) data.length);
		os.write(data);
	}

	/**
	 * 将 int 转变低字节在前， 高字节在后的byte数据组
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] toLH(int n) {
		byte[] b = new byte[4];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> n & 0xff);
		b[2] = (byte) (n >> 16 & 0xff);
		b[3] = (byte) (n >> 24 & 0xff);
		return b;
	}

	/**
	 * 将 float 转变低字节在前， 高字节在后的byte数据组
	 * 
	 * @param f
	 * @return
	 */
	public static byte[] toLH(float f) {
		return toLH(Float.floatToIntBits(f));
	}

	/**
	 * 转换short为byte
	 * 
	 * @param b
	 * @param s
	 *            需要转换的short
	 * @param index
	 */
	public static void putShort(byte b[], short s, int index) {
		b[index + 1] = (byte) (s >> 8);
		b[index + 0] = (byte) (s >> 0);
	}

	/**
	 * 转换short[]为byte[]
	 * 
	 * @param b
	 * @param s
	 * @param index
	 */
	public static void putShort(byte b[], short[] s, int index) {
		byte bLength = 2;
		byte[] bShort = new byte[bLength];
		for (int iLoop = index; iLoop < s.length; iLoop++) {
			putShort(bShort, s[iLoop], 0);
			for (int jLoop = 0; jLoop < bLength; jLoop++) {
				b[iLoop * bLength + jLoop] = bShort[jLoop];
			}
		}
	}

	/**
	 * 通过byte数组取到short
	 * 
	 * @param b
	 * @param index
	 *            第几位开始取
	 * @return
	 */
	public static short getShort(byte[] b, int index) {
		return (short) (((b[index + 1] << 8) | b[index + 0] & 0xff));
	}

	/**
	 * 通过byte数组取到short数组
	 * 
	 * @param b
	 * @return
	 */
	public static short[] getShort(byte[] b) {
		byte bLength = 2;
		byte[] bTemp = new byte[bLength];
		short[] s = new short[b.length / bLength];
		for (int iLoop = 0; iLoop < s.length; iLoop++) {
			for (int jLoop = 0; jLoop < bLength; jLoop++) {
				bTemp[jLoop] = b[iLoop * bLength + jLoop];
			}
			s[iLoop] = getShort(bTemp, 0);
		}
		return s;
	}

	/**
	 * 转换int为byte数组
	 * 
	 * @param bb
	 * @param x
	 * @param index
	 */
	public static void putInt(byte[] bb, int x, int index) {
		bb[index + 3] = (byte) (x >> 24);
		bb[index + 2] = (byte) (x >> 16);
		bb[index + 1] = (byte) (x >> 8);
		bb[index + 0] = (byte) (x >> 0);
	}

	/**
	 * 通过byte数组取到int
	 * 
	 * @param bb
	 * @param index
	 *            第几位开始
	 * @return
	 */
	public static int getInt(byte[] bb, int index) {
		return (int) ((((bb[index + 3] & 0xff) << 24)
				| ((bb[index + 2] & 0xff) << 16)
				| ((bb[index + 1] & 0xff) << 8) | ((bb[index + 0] & 0xff) << 0)));
	}

	/**
	 * 转换long型为byte数组
	 * 
	 * @param bb
	 * @param x
	 * @param index
	 */
	public static void putLong(byte[] bb, long x, int index) {
		bb[index + 7] = (byte) (x >> 56);
		bb[index + 6] = (byte) (x >> 48);
		bb[index + 5] = (byte) (x >> 40);
		bb[index + 4] = (byte) (x >> 32);
		bb[index + 3] = (byte) (x >> 24);
		bb[index + 2] = (byte) (x >> 16);
		bb[index + 1] = (byte) (x >> 8);
		bb[index + 0] = (byte) (x >> 0);
	}

	/**
	 * 通过byte数组取到long
	 * 
	 * @param bb
	 * @param index
	 * @return
	 */
	public static long getLong(byte[] bb, int index) {
		return ((((long) bb[index + 7] & 0xff) << 56)
				| (((long) bb[index + 6] & 0xff) << 48)
				| (((long) bb[index + 5] & 0xff) << 40)
				| (((long) bb[index + 4] & 0xff) << 32)
				| (((long) bb[index + 3] & 0xff) << 24)
				| (((long) bb[index + 2] & 0xff) << 16)
				| (((long) bb[index + 1] & 0xff) << 8) | (((long) bb[index + 0] & 0xff) << 0));
	}

	/**
	 * 字符到字节转换
	 * 
	 * @param ch
	 * @return
	 */
	public static void putChar(byte[] bb, char ch, int index) {
		int temp = (int) ch;
		// byte[] b = new byte[2];
		for (int i = 0; i < 2; i++) {
			bb[index + i] = new Integer(temp & 0xff).byteValue(); // 将最高位保存在最低位
			temp = temp >> 8; // 向右移8位
		}
	}

	/**
	 * 字节到字符转换
	 * 
	 * @param b
	 * @return
	 */
	public static char getChar(byte[] b, int index) {
		int s = 0;
		if (b[index + 1] > 0)
			s += b[index + 1];
		else
			s += 256 + b[index + 0];
		s *= 256;
		if (b[index + 0] > 0)
			s += b[index + 1];
		else
			s += 256 + b[index + 0];
		char ch = (char) s;
		return ch;
	}

	/**
	 * float转换byte
	 * 
	 * @param bb
	 * @param x
	 * @param index
	 */
	public static void putFloat(byte[] bb, float x, int index) {
		// byte[] b = new byte[4];
		int l = Float.floatToIntBits(x);
		for (int i = 0; i < 4; i++) {
			bb[index + i] = new Integer(l).byteValue();
			l = l >> 8;
		}
	}

	/**
	 * 通过byte数组取得float
	 * 
	 * @param b
	 * @param index
	 * @return
	 */
	public static float getFloat(byte[] b, int index) {
		int l;
		l = b[index + 0];
		l &= 0xff;
		l |= ((long) b[index + 1] << 8);
		l &= 0xffff;
		l |= ((long) b[index + 2] << 16);
		l &= 0xffffff;
		l |= ((long) b[index + 3] << 24);
		return Float.intBitsToFloat(l);
	}

	/**
	 * double转换byte
	 * 
	 * @param d
	 * @return
	 */
	public static byte[] doubleToByte(double d) {
		byte[] b = new byte[8];
		long l = Double.doubleToRawLongBits(d);
		for (int i = 0; i < 8; i++) {
			b[i] = new Long(l).byteValue();
			l = l >> 8;
		}
		return b;
	}

	/**
	 * double转换byte
	 * 
	 * @param bb
	 * @param x
	 * @param index
	 */
	public static void putDouble(byte[] bb, double x, int index) {
		long l = Double.doubleToLongBits(x);
		for (int i = index; i < index + 8; i++) {
			bb[i] = new Long(l).byteValue();
			l = l >> 8;
		}
	}

	/**
	 * 通过byte数组取得float
	 * 
	 * @param b
	 * @param index
	 * @return
	 */
	public static double getDouble(byte[] b, int index) {
		long l;
		l = b[0];
		l &= 0xff;
		l |= ((long) b[1] << 8);
		l &= 0xffff;
		l |= ((long) b[2] << 16);
		l &= 0xffffff;
		l |= ((long) b[3] << 24);
		l &= 0xffffffffl;
		l |= ((long) b[4] << 32);
		l &= 0xffffffffffl;
		l |= ((long) b[5] << 40);
		l &= 0xffffffffffffl;
		l |= ((long) b[6] << 48);
		l &= 0xffffffffffffffl;
		l |= ((long) b[7] << 56);
		return Double.longBitsToDouble(l);
	}

	// java Date 转 C# 时间 16位
	public static void putDate(byte[] b, Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);

		putShort(b, (short) c.get(c.YEAR), 0); // 年
		putShort(b, (short) c.get(c.MONTH), 2); // 月
		putShort(b, (short) c.get(c.DAY_OF_WEEK), 4); // 周
		putShort(b, (short) c.get(c.DAY_OF_MONTH), 6); // 天
		putShort(b, (short) c.get(c.HOUR_OF_DAY), 8); // 时
		putShort(b, (short) c.get(c.MINUTE), 10); // 分
		putShort(b, (short) c.get(c.SECOND), 12); // 秒
		putShort(b, (short) c.get(c.MILLISECOND), 14); // 毫秒
	}
}