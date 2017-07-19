package cn.com.cis.utils.encryptUtil;

import java.io.InputStream;

import javax.crypto.Cipher;


/**
 * Created by wanglei on 16/3/17.
 */
public class ScriptFileEncryptor {

    public static String encrypt(String data, String key) throws Exception {
        Cipher advancedCipher = CipherGenerator.cipherInit2(key.getBytes(), Cipher.ENCRYPT_MODE);

        return Encryptor.encrypt(advancedCipher, data);
    }

    public static String decrypt(String data, String key) throws Exception {
        Cipher advancedCipher = CipherGenerator.cipherInit2(key.getBytes(), Cipher.DECRYPT_MODE);

        return Encryptor.decrypt(advancedCipher, data);
    }
    
    public static String decryptInputStream(InputStream data , String key) throws Exception{
    	Cipher advancedCipher = CipherGenerator.cipherInit2(key.getBytes("UTF-8"), Cipher.DECRYPT_MODE);
        return Encryptor.decryptInputStream(advancedCipher, data);
    }

    public static void main(String[] args) throws Exception {
//		File file = new File("/Users/wanglei/Downloads/湖北省直医保项目药品目录版本号-6.sql");
//
//		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
//
//		OracleDataSource dataSource = new OracleDataSource();
//		dataSource.setUser("system");
//		dataSource.setPassword("system");
//		dataSource.setURL("dbc:oracle:thin:@10.117.198.62:1521:orcl");
//
//		final Connection connection = dataSource.getConnection();
//
//		String line = null;
//		while (null != (line = bufferedReader.readLine())) {
////			String text = DesUtil.decrypt(line, "HZYB@CIS");
//			String text = DesUtil.decrypt(line, "SHHBSZ@C");
//			final ScriptRunner scriptRunner = new ScriptRunner(connection);
//			scriptRunner.setAutoCommit(false);
//			scriptRunner.setSendFullScript(false);
//			scriptRunner.setStopOnError(false);
//			scriptRunner.runScript(new StringReader(text));
//
//			System.out.println();
//
//		}
//
//		bufferedReader.close();
    }
}
