package cn.com.cis.module.drgsgroup.dao;

import java.util.List;

import cn.com.cis.module.drgsgroup.entity.DrgsGroupRecord;

public interface DrgsGroupRecordMapper {
	
	List<DrgsGroupRecord> selectAllDrgsGroupRecords();
	
	void updateDrgsGroupRecord(DrgsGroupRecord drgsGroupRecord);

}
