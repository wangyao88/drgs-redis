package cn.com.cis.utils;

import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
	
	public static void writeContentToFile(String fileName,String content,boolean isAppend) throws IOException{
		FileWriter fw = new FileWriter(fileName, isAppend);
		fw.write(content);
		fw.close();
	}

}
