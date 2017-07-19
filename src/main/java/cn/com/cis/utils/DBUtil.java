package cn.com.cis.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import cn.com.cis.common.entity.ConnParam;

@Slf4j
public class DBUtil {
	
	private static ConnParam connParam;
	
	public static Connection getConnection(){
		initParameters();
		Connection connection = null;
		try {
			Class.forName(connParam.getDriverClass());
			connection = DriverManager.getConnection(connParam.getUrl(), connParam.getUserName(), connParam.getPassword());
		} catch (SQLException e) {
			log.error(e.getMessage());
		}catch (ClassNotFoundException e) {
			log.error(e.getMessage());
		}
		return connection;
	}
	
	private static void initParameters() {
		if(connParam == null){
			connParam = new ConnParam();
			Map<String,String> params = PropertyUtil.getPropertiesAllValue("jdbc.properties");
			connParam.setDriverClass(params.get("jdbc.driverClassName"));
			connParam.setPassword(params.get("jdbc.password"));
			connParam.setUrl(params.get("jdbc.url"));
			connParam.setUserName(params.get("jdbc.username"));
		}
	}

	public static PreparedStatement getPreparedStatement(Connection connection,String sql){
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
		return prepareStatement;
	}
	
	public static void close(Connection connection,PreparedStatement prepareStatement){
		try {
			if(prepareStatement != null){
				prepareStatement.close();
			}
			
			if(connection != null){
				connection.close();
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

}
