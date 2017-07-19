package cn.com.cis.module.drgsgroup.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DrgsGroupTaskMain {

	public static void startDrgsGroupTask(){
		long initialDelay = 0; 
		long period = 1;
		DrgsGroupTaskByDay drgsGroupTaskByDay = new DrgsGroupTaskByDay();
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(drgsGroupTaskByDay,initialDelay,period,TimeUnit.DAYS);
		
	}
}
