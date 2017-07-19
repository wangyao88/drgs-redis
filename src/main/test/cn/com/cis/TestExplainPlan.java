package cn.com.cis;

import cn.com.cis.utils.sql.explainplan.ExplainPlan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author David Kong
 * @version 1.0
 */
public class TestExplainPlan {
    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver is not found!");
        }
    }

    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//10.117.130.29:1521/ora29a", "kongdewen", "kongdewen");
        ExplainPlan<String> explainPlan = ExplainPlan.newStringExplainPlan(conn);
        String sql =
                "SELECT b.patient_name \"姓名\",\n" +
                "  b.bill_no \"单据号\",\n" +
                "  b.id,\n" +
                "  b.hospital_name \"机构名称\",\n" +
                "  b.ADMISSION_DATE \"入院日期\",\n" +
                "  b.discharge_date \"出院日期\"\n" +
                "FROM bmi14.ad_auditresult_related re,\n" +
                "  bmi14.dw_bill b\n" +
                "WHERE re.rule_code = '150801'\n" +
                "AND re.related     = b.id\n" +
                "AND re.table_par   = b.table_par\n" +
                "AND EXISTS\n" +
                "  (SELECT 1\n" +
                "  FROM dw_bill b1\n" +
                "  WHERE b1.table_par >= '20130101'\n" +
                "  AND b1.table_par    < '20130201'\n" +
                "  AND re.claim_id     = b1.id\n" +
                "  )\n" +
                "ORDER BY b.patient_id,\n" +
                "  re.claim_id,\n" +
                "  b.discharge_date;";
        String s = explainPlan.viewPlan(sql);
        System.out.println(s);

        ExplainPlan<String> xml = ExplainPlan.newXmlExplainPlan(conn);
        String xmlStr = xml.viewPlan(sql);
        System.out.println(xmlStr);
    }
}
