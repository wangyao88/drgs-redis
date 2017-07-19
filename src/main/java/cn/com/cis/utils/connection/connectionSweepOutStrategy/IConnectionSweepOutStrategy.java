package cn.com.cis.utils.connection.connectionSweepOutStrategy;

import cn.com.cis.utils.connection.SQLPlusConnection;

/**
 * Created by wanglei on 16/4/22.
 */
public interface IConnectionSweepOutStrategy {

    // 判断connection是否应该销毁
    boolean goToDie(SQLPlusConnection connection);
}
