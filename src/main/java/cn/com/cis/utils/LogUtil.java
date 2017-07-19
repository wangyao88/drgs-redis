package cn.com.cis.utils;

import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;


public class LogUtil {
	
	private static final String path = "log.properties";
	
	private static boolean isDebug = false;
	private static Logger log;
	
    private LogUtil(){
		
	} 
	
	public static LogUtil getInstance(Class<?> clazz){
		Map<String,String> propertiesMap = PropertyUtil.getPropertiesAllValue(path);
		isDebug = Boolean.valueOf(propertiesMap.get("debug"));
		log = LoggerFactory.getLogger(clazz);
		return Singleton.logUtil;
	}
	
	private static class Singleton{
		private static final LogUtil logUtil = new LogUtil();
	}
	
	public void info(String msg){
		if(isDebug){
			log.info(msg);
		}
	}

	public void error(String msg, Throwable fillInStackTrace) {
		if(isDebug){
			log.error(msg,fillInStackTrace);
		}
	}
}
