package cn.com.cis.utils.sql.explainplan;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.sql.Connection;

/**
 * Sql 执行计划接口类
 * 暂时以字符串形式显示执行计划
 *
 * @author David Kong
 * @version 1.0
 * @see StringExplainPlan
 */
public abstract class ExplainPlan<T extends Serializable> {

    public static final String EXPLAIN_PLAN_PREFIX = "EXPLAIN PLAN FOR ";

    /**
     * 显示执行计划
     *
     * @return 泛型，返回String或者是Table
     */
    public abstract T viewPlan(String sql);

    protected static String formatSql(String sql) {
        Assert.notNull(sql);
        sql = sql.trim();
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }
        return sql;
    }

    public static ExplainPlan newStringExplainPlan(Connection connection) {
        return new StringExplainPlan(connection);
    }

    public static ExplainPlan newXmlExplainPlan(Connection connection) {
        return new XmlExplainPlan(connection);
    }
}
