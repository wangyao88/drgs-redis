package cn.com.cis.module.drgsgroup.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import cn.com.cis.module.drgsgroup.entity.DrgsGroupResult;

public interface DrgsGroupResultMapper {
	
	List<DrgsGroupResult> selectAllDrgsGroupResults();
	
	void insertDrgsGroupResult(DrgsGroupResult drgsGroupResult);
	
	void batchInsertDrgsGroupResults(List<DrgsGroupResult> drgsGroupResults) throws DataAccessException;

}
