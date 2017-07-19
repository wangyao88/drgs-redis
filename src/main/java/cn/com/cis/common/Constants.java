package cn.com.cis.common;

import java.util.HashSet;
import java.util.Set;

public abstract class Constants {

    /**
     * 批量更新数
     */
    public static final int  BATCH_UPDATE_SIZE = 10000;
    /**
     * 批量提交数量
     */
    public static final int BATCH_COMMIT_SIZE = 20000;
    
    public static final int SUM_BATCH_COMMIT_SIZE_BY_SECOND = BATCH_COMMIT_SIZE*1000;//抽取速度 将毫秒转化为秒
    /**
     * 控制台或日志中显示加载条数
     */
    public static final int BATCH_VIEW_SIZE = 100000;

    public static final int DEFAULT_MIN_SPLIT = 5;

    public static final String WEBSOCKET_USERNAME = "WEBSOCKET_USERNAME";

    public static final String GLOBAL = "GLOBAL";

    public static final String LOCAL = "LOCAL";

    public static final Set<String> PARAMETER_SCOPE =  new HashSet<String>();

    /**
     * 用于加密数据库密码的密钥
     */
    public final static String DEFAULT_DB_PASS = "gHA3!%mvkIhfM4JR";

    public final static int TRY_CONNECT_TIME_OUT = 10;

    public final static int TRY_GET_CONNECTION_TIME_OUT = 3;

    public final static String DB_VERSION = "1.4";

    public final static int MAX_FETCH_SIZE = 1000;

    public final static String SUCCESS = "success";

    public final static String FAILED = "failed";

    public final static String VALID_NAME_RULE = "^[\\u4e00-\\u9fa5a-zA-Z0-9]+$";

    public final static String TIME_SHOW_FORMAT = "yyyy-MM-dd HH:mm:ss";

    static {
        PARAMETER_SCOPE.add(GLOBAL);
        PARAMETER_SCOPE.add(LOCAL);
    }
}
