package cn.com.cis.common.utils;

/**
 * 与环境和系统相关的实用方法集合。
 * 
 */
public class SystemUtil {

    /**
     * 获取当前操作系统的换行符。
     * 
     * @return 当前操作系统的换行符。
     */
    public static String getEndLine() {
        return System.getProperty("line.separator");
    }

}
