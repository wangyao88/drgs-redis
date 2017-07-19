package cn.com.cis.utils.connection.connectionSweepOutStrategy;

import cn.com.cis.utils.connection.SQLPlusConnection;
import lombok.AllArgsConstructor;

import java.util.Date;

/**
 * Created by wanglei on 16/4/22.
 */
@AllArgsConstructor
public class SessionInvalidTimeoutStrategy implements IConnectionSweepOutStrategy {

    private long timeout = -1;

    @Override
    public boolean goToDie(SQLPlusConnection connection) {
        if (timeout <= 0 || connection.getUserSessionTimeout() == null) return false;

        Date now = new Date();
        if (now.getTime() - connection.getUserSessionTimeout().getTime() > timeout) {
            return true;
        }

        return false;
    }
}
