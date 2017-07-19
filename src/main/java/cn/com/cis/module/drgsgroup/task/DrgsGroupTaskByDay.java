package cn.com.cis.module.drgsgroup.task;

import cn.com.cis.module.drgsgroup.entity.DrgsGroupType;
import cn.com.cis.utils.DateUtil;
import cn.com.cis.utils.ServiceAndMapperUtil;


public class DrgsGroupTaskByDay implements Runnable{

	@Override
	public void run() {
		ServiceAndMapperUtil.getService().getDrgsGroupService().drgsGroupForBalance(DrgsGroupType.DAY,DateUtil.getNowDateInString());
	}
}
