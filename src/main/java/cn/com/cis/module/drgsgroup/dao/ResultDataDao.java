package cn.com.cis.module.drgsgroup.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import cn.com.cis.module.drgsgroup.entity.DrgsGroupResult;
import cn.com.cis.module.drgsgroup.entity.IntermediateResult;

@Repository
public class ResultDataDao implements IResultDataDao{
	
	@Autowired
	private IDrgsGroupResulDao drgsGroupResulDao;
	@Autowired
	private IIntermediateResultDao intermediateResultDao;

	@Override
	public void batchInsertResult(List<IntermediateResult> intermediateResults,List<DrgsGroupResult> drgsGroupResults) throws DataAccessException{
		drgsGroupResulDao.batchInsertDrgsGroupResults(drgsGroupResults);
		intermediateResultDao.batchInsertIntermediateResults(intermediateResults);
		
	}
}