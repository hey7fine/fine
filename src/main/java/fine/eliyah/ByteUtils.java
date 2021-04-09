package fine.eliyah;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 * 一个工具类，为了支持和C#程序通信交互的转换工具类
 */
public class ByteUtils {
    public static byte[] getCRCData(byte[] bytes) {
        byte[] crc = getCRC(bytes);
        byte[] data = Arrays.copyOf(bytes, bytes.length + 2);
        data[data.length - 2] = crc[0];
        data[data.length - 1] = crc[1];
        return data;
    }

    public static byte[] getCRC(byte[] bytes) {
        int len = bytes.length;

        int crc = 0xFFFF;
        int i, j;
        for (i = 0; i < len; i++) {
            crc = ((crc & 0xFF00) | (crc & 0x00FF) ^ (bytes[i] & 0xFF));
            for (j = 0; j < 8; j++) {
                if ((crc & 0x0001) > 0) {
                    crc = crc >> 1;
                    crc = crc ^ 0xA001;
                } else
                    crc = crc >> 1;
            }
        }

        byte[] crcByte = new byte[2];
        crcByte[1] = (byte) ((crc >> 8) & 0xff);
        crcByte[0] = (byte) (crc & 0xff);
        return crcByte;
    }


    public static boolean checkCrc(byte[] data) {
        if (data.length < 2) return false;
        byte[] crc = getCRC(Arrays.copyOf(data, data.length - 2));
        return crc[0] == data[data.length - 2] && crc[1] == data[data.length - 1];

    }

    /**
     * 将short数据类型转化成Byte数组
     *
     * @param data short值
     * @return byte[]数组
     */
    public static byte[] getBytes(short data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        return bytes;
    }

    /**
     * 将int数据类型转化成Byte数组
     *
     * @param data int值
     * @return byte[]数组
     */
    public static byte[] getBytes(int data) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >> 8) & 0xff);
        bytes[2] = (byte) ((data >> 16) & 0xff);
        bytes[3] = (byte) ((data >> 24) & 0xff);
        return bytes;
    }

    /**
     * 将long数据类型转化成Byte数组
     *
     * @param data long值
     * @return byte[]数组
     */
    public static byte[] getBytes(long data) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >> 8) & 0xff);
        bytes[2] = (byte) ((data >> 16) & 0xff);
        bytes[3] = (byte) ((data >> 24) & 0xff);
        bytes[4] = (byte) ((data >> 32) & 0xff);
        bytes[5] = (byte) ((data >> 40) & 0xff);
        bytes[6] = (byte) ((data >> 48) & 0xff);
        bytes[7] = (byte) ((data >> 56) & 0xff);
        return bytes;
    }

    /**
     * 将float数据类型转化成Byte数组
     *
     * @param data float值
     * @return byte[]数组
     */
    public static byte[] getBytes(float data) {
        int intBits = Float.floatToIntBits(data);
        return getBytes(intBits);
    }

    /**
     * 将double数据类型转化成Byte数组
     *
     * @param data double值
     * @return byte[]数组
     */
    public static byte[] getBytes(double data) {
        long intBits = Double.doubleToLongBits(data);
        return getBytes(intBits);
    }


    /**
     * 将字符串转换成byte[]数组
     *
     * @param data        字符串值
     * @param charsetName 编码方式
     * @return 字节数组
     */
    public static byte[] getBytes(String data, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        return data.getBytes(charset);
    }


    /**
     * 将int转换成一个字节的数据
     *
     * @param num int数据
     * @return 一个字节的数据
     */
    public static byte int2OneByte(int num) {
        return (byte) (num & 0x000000ff);
    }


    /**
     * 将一个有符号的byte转换成一个int数据对象
     *
     * @param byteNum 有符号的字节对象
     * @return int数据类型
     */
    public static int oneByte2Int(byte byteNum) {
        //针对正数的int
        return byteNum > 0 ? byteNum : 256 + byteNum;
    }


    /**
     * 将字节数组转换成short数据
     *
     * @param bytes 字节数组
     * @param index 起始位置
     * @return short值
     */
    public static short getShort(byte[] bytes, int index) {
        return (short) ((0xff & bytes[0 + index]) | (0xff00 & (bytes[1 + index] << 8)));
    }

    public static int getInt(byte byteL, byte byteH) {
        return ((0xff & byteL) | (0xff00 & byteH << 8));
    }

    public static int getInt2Bytes(byte[] bytes, int index) {
        return (int) ((0xffL & (int) bytes[index + 1]) |
                (0xff00L & ((int) bytes[index] << 8)));
    }

    public static int getInt(byte byte1, byte byte2, byte byte3, byte byte4) {
        return (int) ((0xffL & (long) byte1) |
                (0xff00L & ((long) byte2 << 8)) |
                (0xff0000L & ((long) byte3 << 16)) |
                (0xff000000L & ((long) byte4 << 24)));
    }

    public static int getInt(byte[] bytes, int index) {
        return (int) ((0xffL & (long) bytes[index + 3]) |
                (0xff00L & ((long) bytes[index + 2] << 8)) |
                (0xff0000L & ((long) bytes[index + 1] << 16)) |
                (0xff000000L & ((long) bytes[index] << 24)));
    }

    public static long getLong(byte byte1, byte byte2, byte byte3, byte byte4) {
        return (0xffL & (long) byte1) |
                (0xff00L & ((long) byte2 << 8)) |
                (0xff0000L & ((long) byte3 << 16)) |
                (0xff000000L & ((long) byte4 << 24));
    }


    /**
     * 将字节数组转换成long数据
     *
     * @param bytes 字节数组
     * @param index 起始位置
     * @return long值
     */
    public static long getLong(byte[] bytes, int index) {
        return (0xffL & (long) bytes[index]) |
                (0xff00L & ((long) bytes[1 + index] << 8)) |
                (0xff0000L & ((long) bytes[2 + index] << 16)) |
                (0xff000000L & ((long) bytes[3 + index] << 24)) |
                (0xff00000000L & ((long) bytes[4 + index] << 32)) |
                (0xff0000000000L & ((long) bytes[5 + index] << 40)) |
                (0xff000000000000L & ((long) bytes[6 + index] << 48)) |
                (0xff00000000000000L & ((long) bytes[7 + index] << 56));
    }

    public static long getLong4(byte[] bytes, int index) {
        return (0xffL & (long) bytes[index + 3] << 32) |
                (0xff00L & ((long) bytes[2 + index] << 40)) |
                (0xff0000L & ((long) bytes[1 + index] << 48)) |
                (0xff000000L & ((long) bytes[index] << 56));
    }

    public static long getLong7(byte[] bytes, int index) {
        return (0xffL & (long) bytes[index]) |
                (0xff00L & ((long) bytes[1 + index] << 8)) |
                (0xff0000L & ((long) bytes[2 + index] << 16)) |
                (0xff000000L & ((long) bytes[3 + index] << 24)) |
                (0xff00000000L & ((long) bytes[4 + index] << 32)) |
                (0xff0000000000L & ((long) bytes[5 + index] << 40)) |
                (0xff000000000000L & ((long) bytes[6 + index] << 48));
    }


    /**
     * 将字节数组转换成double数据
     *
     * @param bytes 字节数组
     * @param index 起始位置
     * @return double值
     */
    public static double getDouble(byte[] bytes, int index) {
        long l = getLong(bytes, index);
        System.out.println(l);
        return Double.longBitsToDouble(l);
    }

    public static double getDouble4(byte[] bytes, int index) {
        long l = getLong4(bytes, index);
        System.out.println(l);

        return Double.longBitsToDouble(l);
    }

    public static float getFloat(byte[] bytes, int index) {
        int l = getInt(bytes, index);
        return Float.intBitsToFloat(l);
    }

    /**
     * 将字节数组转换成string数据
     *
     * @param bytes       字节数组
     * @param charsetName 字符编码
     * @return string值
     */
    public static String getString(byte[] bytes, String charsetName) {
        return new String(bytes, Charset.forName(charsetName));
    }


    /**
     * 将字节数组转换成string数据
     *
     * @param bytes       字节数组
     * @param index       起始位置
     * @param length      数据长度
     * @param charsetName 字符编码
     * @return string值
     */
    public static String getString(byte[] bytes, int index, int length, String charsetName) {
        return new String(bytes, index, length, Charset.forName(charsetName));
    }


    /**
     * 将一个byte[]数组转换成uuid对象
     *
     * @param data 字节数组
     * @return uuid对象
     */
    public static UUID Byte2UUID(byte[] data) {
        if (data.length != 16) {
            throw new IllegalArgumentException("Invalid UUID byte[]");
        }
        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++)
            msb = (msb << 8) | (data[i] & 0xff);
        for (int i = 8; i < 16; i++)
            lsb = (lsb << 8) | (data[i] & 0xff);

        return new UUID(msb, lsb);
    }


    /**
     * 将一个uuid对象转化成byte[]
     *
     * @param uuid uuid对象
     * @return 字节数组
     */
    public static byte[] UUID2Byte(UUID uuid) {
        ByteArrayOutputStream ba = new ByteArrayOutputStream(16);
        DataOutputStream da = new DataOutputStream(ba);
        try {
            da.writeLong(uuid.getMostSignificantBits());
            da.writeLong(uuid.getLeastSignificantBits());
            ba.close();
            da.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] buffer = ba.toByteArray();
        // 进行错位
        byte temp = buffer[0];
        buffer[0] = buffer[3];
        buffer[3] = temp;
        temp = buffer[1];
        buffer[1] = buffer[2];
        buffer[2] = temp;

        temp = buffer[4];
        buffer[4] = buffer[5];
        buffer[5] = temp;

        temp = buffer[6];
        buffer[6] = buffer[7];
        buffer[7] = temp;

        return buffer;
    }


    /**
     * 将字符串数据转换成字节数组，主要转换由C#的字符串的数据
     *
     * @param str 字符串信息
     * @return 转化后的字节数组
     */
    public static byte[] string2Byte(String str) {
        if (str == null) {
            return null;
        }
        byte[] byteArray;
        try {
            byteArray = str.getBytes("unicode");
        } catch (Exception ex) {
            byteArray = str.getBytes();
        }

        if (byteArray.length >= 2) {
            if (byteArray[0] == -1 && byteArray[1] == -2) {
                byte[] newArray = new byte[byteArray.length - 2];
                System.arraycopy(byteArray, 2, newArray, 0, newArray.length);
                byteArray = newArray;
            } else if (byteArray[0] == -2 && byteArray[1] == -1) {
                for (int i = 0; i < byteArray.length; i++) {
                    byte temp = byteArray[i];
                    byteArray[i] = byteArray[i + 1];
                    byteArray[i + 1] = temp;
                    i++;
                }


                byte[] newArray = new byte[byteArray.length - 2];
                System.arraycopy(byteArray, 2, newArray, 0, newArray.length);
                byteArray = newArray;
            }
        }

        return byteArray;
    }


    /**
     * 将字节数组转换成字符串对象，主要转换由C#的字符串的数据
     *
     * @param byteArray
     * @return
     */
    public static String byte2String(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }

        for (int i = 0; i < byteArray.length; i++) {
            byte temp = byteArray[i];
            byteArray[i] = byteArray[i + 1];
            byteArray[i + 1] = temp;
            i++;
        }
        String str;
        try {
            str = new String(byteArray, "unicode");
        } catch (Exception ex) {
            str = new String(byteArray);
        }
        return str;
    }

    /**
     * 将byte[]数组的数据进行翻转
     *
     * @param reverse 等待反转的字符串
     */
    public static void bytesReverse(byte[] reverse) {
        if (reverse != null) {
            byte tmp = 0;
            for (int i = 0; i < reverse.length / 2; i++) {
                tmp = reverse[i];
                reverse[i] = reverse[reverse.length - 1 - i];
                reverse[reverse.length - 1 - i] = tmp;
            }
        }
    }


    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    /**
     * 将字节数组转换成十六进制的字符串形式
     *
     * @param bytes 原始的字节数组
     * @return 字符串信息
     */
    public static String bytes2HexString(byte[] bytes) {
        char[] buf = new char[bytes.length * 3];
        int index = 0;
        for (byte b : bytes) { // 利用位运算进行转换，可以看作方法一的变种
            buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
            buf[index++] = HEX_CHAR[b & 0xf];
            buf[index++] = ' ';
        }

        return new String(buf);
    }

    public static byte[] hexStringToBytes(String hexString) {
        hexString = hexString.replace(" ", "");
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 获取指定时间的指定格式的字符串
     *
     * @param date   指定的时间
     * @param format 指定的格式
     * @return 最后字符串信息
     */
    public static String getStringDateShort(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(date);
        return dateString;
    }


}
