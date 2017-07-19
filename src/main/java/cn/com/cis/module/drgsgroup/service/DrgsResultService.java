package cn.com.cis.module.drgsgroup.service;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessException;

import scriptella.execution.EtlExecutorException;
import cn.com.cis.module.drgsgroup.entity.AtomicCount;
import cn.com.cis.module.drgsgroup.entity.DrgsGroupResult;
import cn.com.cis.module.drgsgroup.entity.IntermediateResult;
import cn.com.cis.module.drgsgroup.entity.Message;
import cn.com.cis.module.drgsgroup.etl.DealDrgsResultData;
import cn.com.cis.module.drgsgroup.etl.EtlDrgsGroupData;
import cn.com.cis.utils.DateUtil;
import cn.com.cis.utils.ServiceAndMapperUtil;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

@Slf4j
public class DrgsResultService {
	
	private ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(2));
	private ConcurrentLinkedQueue<IntermediateResult> queue  = new ConcurrentLinkedQueue<IntermediateResult>();
	private boolean isFinishedInsert = true; 
	
    private DrgsResultService(){
	} 
	
	public static DrgsResultService getInstance(){
		return Singleton.drgsResultService;
	}
	
	private static class Singleton{
		private static final DrgsResultService drgsResultService = new DrgsResultService();
	}
	
	public void putDataToQueue(IntermediateResult intermediateResult){
		queue.add(intermediateResult);
	}
	
	public void batchInsertDrgsResult(final AtomicCount atomicCount, final int batchNum,final Long groupNo, final String date){
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				List<IntermediateResult> intermediateResults = new ArrayList<IntermediateResult>();
				List<DrgsGroupResult> drgsGroupResults = new ArrayList<DrgsGroupResult>();
				IntermediateResult intermediateResult = null;
				for(int i = 0; i < batchNum; i++){
					intermediateResult = queue.poll();
					intermediateResults.add(intermediateResult);
					drgsGroupResults.add(intermediateResult.getDrgsGroupResult());
				}
				long start = DateUtil.getNowTime();
				try {
					ServiceAndMapperUtil.getMapper().getResultDataDao().batchInsertResult(intermediateResults, drgsGroupResults);
				} catch (DataAccessException e) {
					log.error("批量插入出错");
					e.printStackTrace();
				}
				log.info("批量插入{}条数据已完成!耗时{}ms",batchNum,DateUtil.getTimeBetweenStartAndNow(start));
				etlDrgsResult(atomicCount, groupNo, date);
			}
		});
	}
	
	private synchronized void etlDrgsResult(final AtomicCount atomicCount, final Long groupNo,final String date) {
		if(isFinishedInsert){
			int num = ServiceAndMapperUtil.getMapper().getIntermediateResultDao().getInsertedNum(groupNo);
        	if(atomicCount.getQueryNum() == num){
        		try {
    				//2.N 调用ETL处理最终结果数据，传批次号参数
        			EtlDrgsGroupData etlDrgsGroupData = new DealDrgsResultData(groupNo.toString(), date);
					etlDrgsGroupData.etlExecute();
					atomicCount.resetQueryNum();
					log.info("DRGs分组和ETL均已完成!总共耗时{}ms",DateUtil.getTimeBetweenStartAndNow(atomicCount.getGlobalTime()));
				    String channel = "user:topic";  
				    //其中channel必须为string，而且“序列化”策略也是StringSerializer  
				    //消息内容，将会根据配置文件中指定的valueSerializer进行序列化  
				    //本例中，默认全部采用StringSerializer  
				    //那么在消息的subscribe端也要对“发序列化”保持一致。  
				    Message msg = new Message();
				    msg.setChannel(channel);
				    msg.setContent("from app 1");
				    msg.setSendDate(new Date());
				    ServiceAndMapperUtil.getService().getRedisOperationService().convertAndSend(channel, msg);  
				} catch (MalformedURLException | EtlExecutorException e) {
					log.error("调用ETL处理最终结果数据异常:",e.fillInStackTrace());
				}finally{
					isFinishedInsert = false;
				}
        	}
		}
	}

	public boolean isFinishedInsert() {
		return isFinishedInsert;
	}

	public void setFinishedInsert(boolean isFinishedInsert) {
		this.isFinishedInsert = isFinishedInsert;
	}
	
}
