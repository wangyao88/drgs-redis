package cn.com.cis.utils.connection;

import cn.com.cis.utils.Exceptions;
import cn.com.cis.utils.connection.adapter.ConnectionAdapter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by wanglei on 16/4/20.
 */
@Log4j
public class SQLPlusConnection extends ConnectionAdapter {

    private static final int TRY_GET_STATEMENT_TIMEOUT = 3;

    @Getter
    private Date lastUseTime;

    @Getter
    @Setter
    private Date userSessionTimeout;

    private Set<Statement> statements = Collections.newSetFromMap(new ConcurrentHashMap<Statement, Boolean>());

    public SQLPlusConnection(Connection connection) {
        super(connection);
        lastUseTime = new Date();
    }

    private Statement createStatementByParent() throws SQLException {
        return super.createStatement();
    }

    @Override
    public Statement createStatement() throws SQLException {

        final SQLPlusConnection thisConnection = this;

        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Statement> future = service.submit(new Callable<Statement>() {
            @Override
            public Statement call() throws Exception {
                return thisConnection.createStatementByParent();
            }
        });

        try {
            Statement statement = future.get(TRY_GET_STATEMENT_TIMEOUT, TimeUnit.SECONDS);

            lastUseTime = new Date();
            Statement sqlPlusStatement = new SQLPlusStatement(this, statement);
            statements.add(sqlPlusStatement);
            return sqlPlusStatement;

        } catch (InterruptedException e) {
            throw new SQLException("获取statement出错!");
        } catch (ExecutionException e) {
            throw new SQLException("获取statement出错!");
        } catch (TimeoutException e) {
            throw new SQLException("获取statement超时!");
        }
    }

    @Override
    public void close() throws SQLException {
        for (Statement statement : statements) {
            if (statement.isClosed()) {
                statements.remove(statement);
            }
        }
    }

    public void closeConnection() {
        for (Statement statement : statements) {
            try {
                if (!statement.isClosed()) {
                    statement.cancel();
                    statement.close();
                }
            } catch (SQLException e) {
                log.info("处理与connection关联的statement时出错! 错误堆栈: " + Exceptions.getStackTraceAsString(e));
            }
        }
        try {
            super.getConnection().close();
        } catch (SQLException e) {
            log.info("关闭数据库连接时出错! 错误堆栈: " + Exceptions.getStackTraceAsString(e));
        }
    }
}
