package cn.com.cis.utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlScriptUtil {
    public static String getInsertSql(String[] columnNames, String tableName) {
        StringBuilder stringBuffer = new StringBuilder();
        StringBuilder parameterStringBuffer = new StringBuilder();
        if (columnNames.length < 1) {
            return "";
        }
        stringBuffer.append("INSERT INTO ").append(tableName).append(" (");

        for (int i = 0; i < columnNames.length; i++) {
            if (i != 0) {
                parameterStringBuffer.append(",").append("?");
                stringBuffer.append(",").append(columnNames[i]);
            } else {
                parameterStringBuffer.append("?");
                stringBuffer.append(columnNames[i]);
            }
        }
        stringBuffer.append(")  VALUES (").append(parameterStringBuffer.toString()).append(")");
        return stringBuffer.toString();
    }


    public static void getSqlParameter(Set<String> param, String sql) {
        if (param == null) {
            param = new HashSet<String>();
        }
        if (sql == null)
            throw new NullPointerException();
        if (sql.indexOf("#{") > 0) {
            String temp = sql.substring(sql.indexOf("#{") + 2);
            String s = temp.substring(0, temp.indexOf("}"));
            param.add(s);
            getSqlParameter(param, temp);
        }
    }

    public static void getSqlParameter(HashMap<String, Integer> param, String sql, int start) {
        if (start <= 0) {
            start = 1;
        }
        if (param == null) {
            param = new HashMap<String, Integer>();
        }
        if (sql == null)
            throw new NullPointerException();
        if (sql.indexOf("#{") > 0) {
            String temp = sql.substring(sql.indexOf("#{") + 2);
            String s = temp.substring(0, temp.indexOf("}"));
            param.put(s, start);
            start++;
            getSqlParameter(param, temp, start);
        }
    }

    public static String replaceSql(String sql) {
        Set<String> paramNames = new HashSet<String>();
        getSqlParameter(paramNames, sql);
        if (paramNames.size() > 0) {
            for (String s : paramNames) {
                sql = sql.replace("#{" + s + "}", " ? ");
            }
            return sql;
        } else {
            return sql;
        }
    }

    public static String normalizeSQL(String sqlWithParams) {
        return sqlWithParams.replaceAll("#\\{.*?}", "?");
    }

    public static List<String> getSqlParameter(String sql) {
        Objects.requireNonNull(sql);

        List<String> paramNameList = new ArrayList<>();
        Pattern pattern = Pattern.compile("#\\{.*?}");
        Matcher matcher = pattern.matcher(sql);

        while (matcher.find()) {
            String param = matcher.group();

            paramNameList.add(param.substring(2, param.length() - 1)); // #{X}  ==> X
        }

        return paramNameList;
    }
}
