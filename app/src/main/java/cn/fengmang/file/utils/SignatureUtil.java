package cn.fengmang.file.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import cn.fengmang.baselib.IOUtil;

/**
 * Created by Administrator on 2018/7/4.
 */

public class SignatureUtil {

    private static final int SIZE_1MB = 1024 * 1024;  //1MB

    protected static char[] hexDigits = {
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f'};


    public static String getFileSha1(String filePath) {
        InputStream fis = null;
        String md5 = "";
        try {
            MessageDigest messagedigest = MessageDigest.getInstance("SHA1");
            fis = new FileInputStream(filePath);
            byte[] buffer = new byte[SIZE_1MB];
            int numRead = 0;
            while ((numRead = fis.read(buffer, 0, SIZE_1MB)) != -1) {
                messagedigest.update(buffer, 0, numRead);
            }
            md5 = bufferToHex(messagedigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(fis);
        }
        return md5;
    }

    public static String getFileMD5(String filePath) {
        InputStream fis = null;
        String md5 = "";
        try {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(filePath);
            byte[] buffer = new byte[SIZE_1MB];
            int numRead = 0;
            while ((numRead = fis.read(buffer, 0, SIZE_1MB)) != -1) {
                messagedigest.update(buffer, 0, numRead);
            }
            md5 = bufferToHex(messagedigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(fis);
        }
        return md5;
    }

    private static String bufferToHex(byte[] bytes) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte[] bytes, int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    public static final int INT_0XF0 = 0xf0;
    public static final int INT_4 = 4;
    public static final int INT_0XF = 0xf;

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        // 取字节中高 4 位的数字转换, >>>
        // 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
        char c0 = hexDigits[(bt & INT_0XF0) >> INT_4];
        // 取字节中低 4 位的数字转换
        char c1 = hexDigits[bt & INT_0XF];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }
}
