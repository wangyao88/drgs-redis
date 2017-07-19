package cn.com.cis.module.drgsgroup.dao;

import java.util.concurrent.BlockingQueue;

import cn.com.cis.module.drgsgroup.entity.AtomicCount;
import cn.com.cis.module.drgsgroup.entity.DrgsData;

public interface IDrgsDataDao {
	
	void putAllDrgsDatasToQueue(BlockingQueue<DrgsData> blockingQueue,Long groupNo);
	
    void setAllDrgsDataSize(AtomicCount atomicCount,long groupNo);

}
