package cn.com.cis.module.drgsgroup.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import cn.com.cis.module.drgsgroup.entity.IntermediateResult;

public interface IntermediateResultMapper {
	
	void insertIntermediateResult(IntermediateResult intermediateResult) throws DataAccessException;
	void batchInsertIntermediateResult(List<IntermediateResult> intermediateResults) throws DataAccessException;
}
