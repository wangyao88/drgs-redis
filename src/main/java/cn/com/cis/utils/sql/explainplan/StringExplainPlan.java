package cn.com.cis.utils.sql.explainplan;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author David Kong
 * @version 1.0
 */
public class StringExplainPlan extends ExplainPlan<String> {

    private Connection conn;

    public StringExplainPlan(Connection conn) {
        this.conn = conn;
    }

    @Override
    public String viewPlan(String sql) {
        String formatSql = ExplainPlan.formatSql(sql);
        try {
            Statement statement = conn.createStatement();
            statement.execute(EXPLAIN_PLAN_PREFIX + " " + formatSql);
            ResultSet rs = statement.executeQuery("select plan_table_output from table(dbms_xplan.display())");
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append(rs.getString(1)).append("\n");
            }
            return sb.toString();
        } catch (SQLException e) {
            return "执行出错:\n\t" + e.getMessage();
        }
    }
}
