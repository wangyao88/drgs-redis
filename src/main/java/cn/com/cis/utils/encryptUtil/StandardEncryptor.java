package cn.com.cis.utils.encryptUtil;

import javax.crypto.Cipher;

/**
 * Created by wanglei on 16/3/17.
 */
public class StandardEncryptor {

    public static String encrypt(String data, String key) throws Exception {
        Cipher standCipher = CipherGenerator.cipherInit(key.getBytes(), Cipher.ENCRYPT_MODE);

        return Encryptor.encrypt(standCipher, data);
    }

    public static String decrypt(String data, String key) throws Exception {
        Cipher standCipher = CipherGenerator.cipherInit(key.getBytes(), Cipher.DECRYPT_MODE);

        return Encryptor.decrypt(standCipher, data);
    }
}
