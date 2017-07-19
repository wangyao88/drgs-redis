package cn.com.cis.module.drgsgroup.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.com.cis.module.drgsgroup.entity.AtomicCount;
import cn.com.cis.module.drgsgroup.entity.DrgsData;
import cn.com.cis.module.drgsgroup.manager.DiseaseManager;
import cn.com.cis.module.drgsgroup.manager.DrgsItemManager;
import cn.com.cis.module.drgsgroup.manager.OperationManager;
import cn.com.cis.utils.DBUtil;

@Slf4j
@Repository
public class DrgsDataDao implements IDrgsDataDao{
	
	@Autowired
	private BasicDataSource dataSource;

	@Override
	public void putAllDrgsDatasToQueue(final BlockingQueue<DrgsData> blockingQueue,final Long groupNo) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					@Cleanup
					Connection connection = dataSource.getConnection();
					@Cleanup
					PreparedStatement preparedStatement = connection.prepareStatement(getQueryAllDrgsDataByGroupNoSql(),ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
					preparedStatement.setLong(1, groupNo);
					preparedStatement.setFetchSize(2000);  
					preparedStatement.setFetchDirection(ResultSet.FETCH_FORWARD);
					@Cleanup
					ResultSet resultSet = preparedStatement.executeQuery();
					while(resultSet.next()){
						blockingQueue.put(convertResultToDrgsDataQueue(resultSet));
					}
				} catch (SQLException e) {
					log.error(e.getMessage());
				} catch (InterruptedException e) {
					log.error(e.getMessage());
				}finally{
					DrgsData drgsData = new DrgsData();
					drgsData.setHisId("stop");
					try {
						blockingQueue.put(drgsData);
					} catch (InterruptedException e) {
						log.error(e.getMessage());
					}
				}
			}
		}).start();
	}
	
	private DrgsData convertResultToDrgsDataQueue(ResultSet resultSet) throws SQLException {
		DrgsData drgsData = new DrgsData();
		drgsData.setHisId(resultSet.getString("HISID"));
		drgsData.setGroupNo(resultSet.getLong("GROUP_NO"));
		drgsData.setPatientSex(resultSet.getString("PATIENT_SEX"));
		drgsData.setAgeType(resultSet.getInt("AGE_TYPE"));
		drgsData.setWeightType(resultSet.getInt("WEIGHT_TYPE"));
		drgsData.setLoadFlag(resultSet.getInt("LOAD_FLAG"));
		drgsData.setUploadDate(resultSet.getDate("UPLOAD_DATE"));
		drgsData.setBillDate(resultSet.getDate("BILL_DATE"));
		drgsData.setGroupDate(resultSet.getDate("GROUP_DATE"));
		drgsData.setLongterm(resultSet.getInt("LONGTERM"));
		DiseaseManager.newManager(drgsData).setDiseaseList();
		OperationManager.newManager(drgsData).getOperations();
		DrgsItemManager.newManager(drgsData).getDrgsItemData();
		return drgsData;
	}
	
	public void setAllDrgsDataSize(AtomicCount atomicCount,long groupNo){
		try {
			@Cleanup
			Connection connection = DBUtil.getConnection();
			@Cleanup
			PreparedStatement preparedStatement = connection.prepareStatement(getQuerySizeByGroupNoSql());
			preparedStatement.setLong(1, groupNo);
			@Cleanup
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				atomicCount.setQueryNum(resultSet.getInt("num"));
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}
	
	private String getQueryColumns(){
		String columns = "HISID,GROUP_NO,PATIENT_SEX,AGE_TYPE,WEIGHT_TYPE,LOAD_FLAG,UPLOAD_DATE,BILL_DATE,GROUP_DATE,LONGTERM ";
		return columns;
	}
	
	private String getQueryNumColumn(){
		return "count(1) as num ";
	}
	
	private String getQueryAllDrgsDataByGroupNoSql() {
		return getQueryByGroupNoSql(getQueryColumns());
	}
	
	private String getQuerySizeByGroupNoSql() {
		return getQueryByGroupNoSql(getQueryNumColumn());
	}
	
	private String getQueryByGroupNoSql(String columns) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ")
		   .append(columns)
		   .append("FROM TD_DRGS_IN_HOSPITAL where GROUP_NO = ?");
		return sql.toString();
	}
	
}
