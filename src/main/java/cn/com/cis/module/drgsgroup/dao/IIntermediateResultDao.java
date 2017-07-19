package cn.com.cis.module.drgsgroup.dao;

import java.util.List;

import cn.com.cis.module.drgsgroup.entity.IntermediateResult;

public interface IIntermediateResultDao {

	void batchInsertIntermediateResults(List<IntermediateResult> intermediateResults);

	int getInsertedNum(long groupNo);
}
