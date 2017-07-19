package cn.com.cis.module.drgsgroup.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import lombok.Cleanup;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.com.cis.module.drgsgroup.entity.IntermediateResult;
import cn.com.cis.utils.DBUtil;
import cn.com.cis.utils.DateUtil;

@Repository
public class IntermediateResultDao implements IIntermediateResultDao{
	
	@Autowired
	private BasicDataSource dataSource;

	@Override
	public void batchInsertIntermediateResults(
			List<IntermediateResult> intermediateResults) {
		try {
			@Cleanup
			Connection connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			@Cleanup
			PreparedStatement preparedStatement = connection.prepareStatement(getInsertIntermediateResultSql());
			setParameters(preparedStatement,intermediateResults);
			preparedStatement.executeBatch();
			connection.commit();
		}catch(Exception e){
			e.printStackTrace();
		}
			
	}

	private String getInsertIntermediateResultSql() {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO TD_INTERMEDIATE_RESULT  ")
		   .append("(ID, HISID, GROUP_NO, MAIN_OPR_ADRGS, DISEASE_ADRGS, DEFAULT_ADRGS, ")
		   .append("RESULT_ADRG, ICD10, PRIMARY_DIAGNOSIS_CODE, ICD9, IS_HAS_MAIN_OPR, MAIN_OPR_COSTS,")
		   .append("MAIN_OPR_DATE, DISEASE_MDC, CCFALG, OTHER_FACTOR_1, OTHER_FACTOR_2, OTHER_FACTOR_3,")
		   .append("AGE_TYPE, WEIGHT_TYPE, SEX, MATCH_COUNT, UPLOAD_DATE, DRG_GROUP_DATE, ")
		   .append(" DRG_CODE, UN_MAIN_OPR_STR, DRG_TYPE) ")
		   .append("VALUES ")
		   .append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		return sql.toString();
	}
	
	private void setParameters(PreparedStatement preparedStatement,List<IntermediateResult> intermediateResults) {
		try {
			for(IntermediateResult intermediateResult : intermediateResults){
				preparedStatement.setString(1, intermediateResult.getId());
				preparedStatement.setString(2, intermediateResult.getHisId());
				preparedStatement.setLong(3, intermediateResult.getGroupNo());
				preparedStatement.setString(4, intermediateResult.getMainOprAdrgs());
				preparedStatement.setString(5, intermediateResult.getDiseaseAdrgs());
				preparedStatement.setString(6, intermediateResult.getDefaultAdrgs());
				preparedStatement.setString(7, intermediateResult.getResultAdrg());
				preparedStatement.setString(8, intermediateResult.getIcd10());
				preparedStatement.setString(9, intermediateResult.getPrimaryDiagnosisCode());
				preparedStatement.setString(10, intermediateResult.getIcd9());
				preparedStatement.setString(11, intermediateResult.getIsHasMainOpr());
				preparedStatement.setFloat(12, intermediateResult.getMainOprCosts());
				preparedStatement.setDate(13, DateUtil.changeUtilDateToSqlDate(intermediateResult.getMainOprDate()));
				preparedStatement.setString(14, intermediateResult.getDiseaseMdc());
				preparedStatement.setString(15, intermediateResult.getCcFlag());
				preparedStatement.setString(16, intermediateResult.getOtherFactor1());
				preparedStatement.setString(17, intermediateResult.getOtherFactor2());
				preparedStatement.setString(18, intermediateResult.getOtherFactor3());
				preparedStatement.setInt(19, intermediateResult.getAgeType());
				preparedStatement.setInt(20, intermediateResult.getWeightType());
				preparedStatement.setString(21, intermediateResult.getSex());
				preparedStatement.setInt(22, intermediateResult.getMatchCount());
				preparedStatement.setDate(23, DateUtil.changeUtilDateToSqlDate(intermediateResult.getUploadDate()));
				preparedStatement.setDate(24, DateUtil.changeUtilDateToSqlDate(intermediateResult.getDrgGroupDate()));
				preparedStatement.setString(25, intermediateResult.getDrgCode());
				preparedStatement.setString(26, intermediateResult.getUnMainOprStr());
				preparedStatement.setString(27, intermediateResult.getDrgType()==null?null:intermediateResult.getDrgType().toString());
				preparedStatement.addBatch();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getInsertedNum(long groupNo){
		try {
			@Cleanup
			Connection connection = DBUtil.getConnection();
			@Cleanup
			PreparedStatement preparedStatement = connection.prepareStatement(getQuerySizeByGroupNoSql());
			preparedStatement.setLong(1, groupNo);
			@Cleanup
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				 return resultSet.getInt("num");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private String getQuerySizeByGroupNoSql() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(1) AS num FROM TD_INTERMEDIATE_RESULT WHERE GROUP_NO=?");
		return sql.toString();
	}
}