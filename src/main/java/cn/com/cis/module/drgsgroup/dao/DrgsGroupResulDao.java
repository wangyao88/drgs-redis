package cn.com.cis.module.drgsgroup.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import lombok.Cleanup;

import org.springframework.stereotype.Repository;

import cn.com.cis.module.drgsgroup.entity.DrgsGroupResult;
import cn.com.cis.utils.DBUtil;
import cn.com.cis.utils.DateUtil;

@Repository
public class DrgsGroupResulDao implements IDrgsGroupResulDao{

	@Override
	public void batchInsertDrgsGroupResults(
			List<DrgsGroupResult> drgsGroupResults) {
		try {
			@Cleanup
			Connection connection = DBUtil.getConnection();
			@Cleanup
			PreparedStatement preparedStatement = connection.prepareStatement(getInsertDrgsGroupResultSql());
			setParameters(preparedStatement,drgsGroupResults);
			preparedStatement.executeBatch();
			connection.commit();
		}catch(Exception e){
			e.printStackTrace();
		}
			
	}
	
	private String getInsertDrgsGroupResultSql() {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO TD_DRGS_GROUP_RESULT ")
		   .append("(GROUP_NO, HISID, UPLOAD_DATE, DRGS_CODE, ICD10,  ")
		   .append("ICD10_1TO16, OPR_MAIN_ICD9, NMAIN_OPR_STR, ")
		   .append("GROUP_DATE, UPLOAD_FLAG, MATCH_COUNT)")
		   .append("VALUES ")
		   .append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		return sql.toString();
	}
	
	private void setParameters(PreparedStatement preparedStatement,List<DrgsGroupResult> drgsGroupResults) {
		try {
			for(DrgsGroupResult drgsGroupResult : drgsGroupResults){
				preparedStatement.setLong(1, drgsGroupResult.getGroupNo());
				preparedStatement.setString(2, drgsGroupResult.getHisId());
				preparedStatement.setDate(3, DateUtil.changeUtilDateToSqlDate(drgsGroupResult.getUploadDate()));
				preparedStatement.setString(4, drgsGroupResult.getDrgsCode());
				preparedStatement.setString(5, drgsGroupResult.getIcd10());
				preparedStatement.setString(6, drgsGroupResult.getPrimaryDiagnosisCode());
				preparedStatement.setString(7, drgsGroupResult.getIcd9());
				preparedStatement.setString(8, drgsGroupResult.getUnMainOprStr());
				preparedStatement.setDate(9, DateUtil.changeUtilDateToSqlDate(drgsGroupResult.getGroupDate()));
				preparedStatement.setInt(10, drgsGroupResult.getLoadFlag());
				preparedStatement.setInt(11, drgsGroupResult.getMatchCount());
				preparedStatement.addBatch();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
