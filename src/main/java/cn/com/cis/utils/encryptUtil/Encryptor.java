package cn.com.cis.utils.encryptUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;

/**
 * Created by wanglei on 16/3/17.
 */
public class Encryptor {

    /**
     * 根据键值进行加密
     */
    public static String encrypt(Cipher cipher, String data) throws Exception {
        if (null == data) {
            return null;
        }

        byte[] bt = cipher.doFinal(data.getBytes());
        String strs = new BASE64Encoder().encode(bt);
        return strs;
    }

    /**
     * 根据键值进行解密
     */
    public static String decrypt(Cipher cipher, String data) throws Exception {
        if (data == null) {
            return null;
        }

        BASE64Decoder decoder = new BASE64Decoder();
        byte[] buf = decoder.decodeBuffer(data);
        byte[] bt = cipher.doFinal(buf);
        return new String(bt,"utf8");
    }
    
    /**
     * 根据键值进行解密
     */
    public static String decryptInputStream(Cipher cipher, InputStream data) throws Exception {
        if (data == null) {
            return null;
        }
        byte[] buf = toByteArray(data);
        byte[] bt = cipher.doFinal(buf);
        return new String(bt,"UTF-8");
    }
    
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }
    
}
