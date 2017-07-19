package cn.com.cis.utils;

import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class Logger {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class);

    public static void info(String s){
        logger.info(s);
    }

    public static void info(String s,Object o){
        logger.info(s,o);
    }
    public static void info(String s,Object... o){
        logger.info(s,o);
    }

    public static void info(String s,Throwable t){
        logger.info(s,t);
    }

    public static void info(Marker marker,String s){
        logger.info(marker,s);
    }

    public static void info(Marker m,String s,Object... o){
        logger.info(m,s,o);
    }
    public static void info(Marker m,String s,Object o){
        logger.info(m,s,o);
    }

    public static void error(Marker marker,String s){
        logger.error(marker,s);
    }

    public static void error(String s,Throwable t){
        logger.error(s,t);
    }

    public static void error(String s,Object o){
        logger.error(s,o);
    }

    public static void error(String s,Object... o){
        logger.error(s,o);
    }
}
