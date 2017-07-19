package cn.com.cis.module.drgsgroup.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import cn.com.cis.module.drgsgroup.entity.DrgsGroupResult;
import cn.com.cis.module.drgsgroup.entity.IntermediateResult;

public interface IResultDataDao {
	
	void batchInsertResult(List<IntermediateResult> intermediateResults
			,List<DrgsGroupResult> drgsGroupResults) throws DataAccessException;
}
