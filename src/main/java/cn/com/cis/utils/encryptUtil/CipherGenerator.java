package cn.com.cis.utils.encryptUtil;

import cn.com.cis.common.Constants;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;

/**
 * Created by wanglei on 16/3/17.
 */
public class CipherGenerator {

    public static final String DES = "DES";

    public static final String DES_CBC_PKCS5PADDING = "DES/CBC/PKCS5Padding";

    public static Cipher cipherInit(byte[] key, int cipherValue) throws Exception {

        /** 生成一个可信任的随机数源 **/
        SecureRandom sr = new SecureRandom();

        /** 从原始密钥数据创建DESKeySpec对象 **/
        DESKeySpec dks = new DESKeySpec(key);

        /** 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象 **/
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);

        SecretKey securekey = keyFactory.generateSecret(dks);

        /** Cipher对象实际完成加密或解密操作 **/
        Cipher cipher = Cipher.getInstance(DES);

        /** 用密钥初始化Cipher对象 **/
        cipher.init(cipherValue, securekey, sr);

        return cipher;
    }

	public static Cipher cipherInit2(byte[] key, int cipherValue) throws Exception {

		/** 生成一个可信任的随机数源 **/
		IvParameterSpec iv = new IvParameterSpec(key);

		/** 从原始密钥数据创建DESKeySpec对象 **/
		DESKeySpec dks = new DESKeySpec(key);

		/** 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象 **/
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);

		SecretKey securekey = keyFactory.generateSecret(dks);

		/** Cipher对象实际完成加密或解密操作 **/
		Cipher cipher = Cipher.getInstance(DES_CBC_PKCS5PADDING);

		/** 用密钥初始化Cipher对象 **/
		cipher.init(cipherValue, securekey, iv);

		return cipher;
	}
}
