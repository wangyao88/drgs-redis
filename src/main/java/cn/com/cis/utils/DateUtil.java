package cn.com.cis.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;

import cn.com.cis.common.Constants;

@Slf4j
public class DateUtil {
	
    public static String now() {
        return new DateTime().toString(Constants.TIME_SHOW_FORMAT);
    }

    public static String getPrintString(Date date) {
        return new DateTime(date).toString(Constants.TIME_SHOW_FORMAT);
    }
    
    public static Date getNowDate(){
    	return new Date();
    }
    
    public static long getTimeBetweenStartAndNow(long start){
    	long time = getNowTime() - start;
    	return time == 0 ? 1 : time;
    }

	public static long getNowTime() {
		return System.currentTimeMillis();
	}
	
	public static Date getNullDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse("0000-00-00");
		} catch (ParseException e) {
			log.error("转换日期对象出错.错误信息:{}",e.getMessage());
		}
		return date;
	}

	public static String getNowDateInString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(getNowDate());
	}
	
	public static java.sql.Date changeUtilDateToSqlDate(java.util.Date date){
		if(date == null){
			return null;
		}
		return new java.sql.Date(date.getTime());
	}
}
