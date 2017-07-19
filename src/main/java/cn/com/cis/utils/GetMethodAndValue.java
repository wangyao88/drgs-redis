package cn.com.cis.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import lombok.extern.java.Log;

@Log
public class GetMethodAndValue {

	/**
	 * 根据属性名获取属性值 
	 * @param fieldName
	 * @param o
	 * @return
	 */
	public static Object getFieldValueByName(String fieldName, Object o) {  
       try {    
           String firstLetter = fieldName.substring(0, 1).toUpperCase();    
           String getter = "get" + firstLetter + fieldName.substring(1);    
           Method method = o.getClass().getMethod(getter, new Class[] {});    
           Object value = method.invoke(o, new Object[] {});    
           return value;    
       } catch (Exception e) {    
           log.info(e.getMessage());    
           return null;    
       }    
	}   
	     
	/** 
    **获取属性名数组 
    **/  
	public static String[] getFiledName(Object o){  
	   	Field[] fields=o.getClass().getDeclaredFields();  
        String[] fieldNames=new String[fields.length];  
	    for(int i=0;i<fields.length;i++){  
	        //System.out.println(fields[i].getType());  
	        fieldNames[i]=fields[i].getName();  
	    }  
	    return fieldNames;  
	}  
}
