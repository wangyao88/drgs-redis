package cn.com.cis.utils.connection;

import cn.com.cis.utils.connection.adapter.StatementAdapter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by wanglei on 16/4/21.
 */
public class SQLPlusStatement extends StatementAdapter{

    private SQLPlusConnection connection;

    public SQLPlusStatement(SQLPlusConnection connection, Statement statement) {
        super(statement);

        this.connection = connection;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }
}
