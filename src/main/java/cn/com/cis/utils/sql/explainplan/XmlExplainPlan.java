package cn.com.cis.utils.sql.explainplan;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * @author David Kong
 * @version 1.0
 */
public class XmlExplainPlan extends ExplainPlan<String> {

    private Connection connection;

    public XmlExplainPlan(Connection connection) {
        this.connection = connection;
    }

    @Override
    public String viewPlan(String sql) {
        String formatSql = ExplainPlan.formatSql(sql);
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        try {
            Statement stmt = connection.createStatement();
            stmt.execute("explain plan set STATEMENT_ID = '" + uuid + "' for " + formatSql);
            ResultSet rs = stmt.executeQuery(" select dbms_xplan.build_plan_xml(statement_id => '" + uuid + "').getclobval() AS XPLAN FROM dual");
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                Clob clob = rs.getClob(1);
                if (clob == null)
                    return "";
                String tmp;
                BufferedReader bufferRead = new BufferedReader(clob.getCharacterStream());
                while ((tmp = bufferRead.readLine()) != null)
                    sb.append(tmp).append("\n");
            }
            return sb.toString();
        } catch (SQLException e) {
            return "执行出错:\n\t" + e.getMessage();
        } catch (IOException e) {
            return "读取返回结果出错!";
        }
    }
}
