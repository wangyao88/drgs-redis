package cn.com.cis.module.drgsgroup.dao;

import java.util.List;

import cn.com.cis.module.drgsgroup.entity.DrgsData;
import cn.com.cis.module.drgsgroup.entity.Operation;

public interface OperationMapper {
	
	List<Operation> selectAllOperationsByGroupNoAndHisId(DrgsData drgsData);
}
