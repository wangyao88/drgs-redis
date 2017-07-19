package cn.com.cis.module.drgsgroup.service;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import scriptella.execution.EtlExecutorException;
import cn.com.cis.module.drgsgroup.dao.DrgsDataMapper;
import cn.com.cis.module.drgsgroup.dao.DrgsGroupRecordMapper;
import cn.com.cis.module.drgsgroup.dao.IDrgsDataDao;
import cn.com.cis.module.drgsgroup.drgsgrouper.DrgsGrouper;
import cn.com.cis.module.drgsgroup.drgsgrouper.DrgsGrouperFactory;
import cn.com.cis.module.drgsgroup.entity.AtomicCount;
import cn.com.cis.module.drgsgroup.entity.DrgsData;
import cn.com.cis.module.drgsgroup.entity.DrgsGroupRecord;
import cn.com.cis.module.drgsgroup.entity.DrgsGroupType;
import cn.com.cis.module.drgsgroup.entity.GroupState;
import cn.com.cis.module.drgsgroup.etl.DrgsPrepareData;
import cn.com.cis.module.drgsgroup.etl.EtlDrgsGroupData;
import cn.com.cis.module.drgsgroup.manager.DiseaseManager;
import cn.com.cis.module.drgsgroup.manager.DrgsItemManager;
import cn.com.cis.module.drgsgroup.manager.OperationManager;
import cn.com.cis.utils.DateUtil;

import com.google.common.collect.ComparisonChain;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

@Slf4j
@Service
public class DrgsGroupService {

	@Autowired
	private DrgsGroupRecordMapper drgsGroupRecordMapper;
	@Autowired
	private DrgsDataMapper drgsDataMapper;
	@Autowired
	private IDrgsDataDao drgsDataDao;
	private ListeningExecutorService executorService;
	private static AtomicCount atomicCount = new AtomicCount();
	private BlockingQueue<DrgsData> drgsDataQueue = new LinkedBlockingDeque<DrgsData>(2000);
	private BlockingQueue<Integer> synchronizedQueue = new LinkedBlockingDeque<Integer>(1);
	private BlockingQueue<Integer> synchronizedRunScriptQueue = new LinkedBlockingDeque<Integer>(1);
	private static final int INSERT_NUM = 5000;
	
    private DrgsGroupService(){
		int cpuCore = Runtime.getRuntime().availableProcessors();
		executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(cpuCore*2));
	}
    
    public void drgsGroupForBalanceWithoutPreparedDatas(String date, String groupNo){
    	DrgsGroupRecord drgsGroupRecord = new DrgsGroupRecord();
    	drgsGroupRecord.setGroupNo(Long.valueOf(groupNo));
    	drgsGroupRecord.setGroupState(GroupState.UNGROUP.valueOfState());
    	drgsGroupRecord.setMessage(GroupState.UNGROUP.valueOfMessage());
    	drgsGroupRecordMapper.updateDrgsGroupRecord(drgsGroupRecord);
		beginDrgsGroup(date);
	}
	
	public void drgsGroupForBalance(DrgsGroupType drgsGroupType,String date){
		//0.调用ETL准备数据
		prepareData(drgsGroupType, date);
		beginDrgsGroup(date);
	}
	
	private void prepareData(DrgsGroupType drgsGroupType, String date) {
		try {
			if(!synchronizedRunScriptQueue.isEmpty()){
				log.info("正在执行脚本");
				return;
			}
			synchronizedRunScriptQueue.put(1);
			EtlDrgsGroupData etlDrgsGroupData = new DrgsPrepareData(drgsGroupType, date);
			etlDrgsGroupData.etlExecute();
			synchronizedRunScriptQueue.take();
		} catch (MalformedURLException | EtlExecutorException | InterruptedException e) {
			log.error("准备数据出错.错误信息{}",e.getMessage());
		}
	}

	private void beginDrgsGroup(String date) {
		//1.查询td_drgs_controll中groupState为待分组的数据
		List<DrgsGroupRecord> drgsGroupRecords = drgsGroupRecordMapper.selectAllDrgsGroupRecords();
		if(drgsGroupRecords.isEmpty()){
			log.info("无分组数据");
		}
		//2遍历查询出的待分组数据列表，每一条数据为一个批次，分别做处理
		for(final DrgsGroupRecord drgsGroupRecord : drgsGroupRecords){
			try {
				if(!synchronizedQueue.isEmpty()){
					log.info("正在执行分组");
				}
				synchronizedQueue.put(0);
			} catch (InterruptedException e1) {
				log.error("同步队列提取元素出错.错误信息{}",e1.getMessage());
			}
			//2.1 处理每个批次前更新td_drgs_controll的开始分组时间
			drgsGroupRecord.setGroupDate(DateUtil.getNowDate());
			//设置分组状态为"正在分组"
			drgsGroupRecord.setGroupState(GroupState.BEGINGROUP.valueOfState());
			drgsGroupRecord.setMessage(GroupState.BEGINGROUP.valueOfMessage());
			drgsGroupRecordMapper.updateDrgsGroupRecord(drgsGroupRecord);
			//2.2 按照每条的批次号查询td_drgs_in_hospital
			atomicCount.resetTime();
			DrgsResultService.getInstance().setFinishedInsert(true);
			drgsDataDao.setAllDrgsDataSize(atomicCount,drgsGroupRecord.getGroupNo());
			drgsDataDao.putAllDrgsDatasToQueue(drgsDataQueue, drgsGroupRecord.getGroupNo());
			try {
				drgsDataGroup(drgsGroupRecord,date);
			} catch (InterruptedException e) {
				log.error("分组出错.错误信息{}",e.getMessage());
			}
		}
	}

	private void drgsDataGroup(final DrgsGroupRecord drgsGroupRecord,final String date) throws InterruptedException {
		boolean isDone = false;
		while(!isDone){
			final DrgsData drgsData = drgsDataQueue.take();
			if(drgsData.getHisId().equals("stop")){
				isDone = true;
				if(atomicCount.getQueryNum() == 0){
					synchronizedQueue.take();
				}
			}else{
				atomicCount.setStartTime(System.currentTimeMillis());
				executorService.submit(new Runnable() {
	                @Override
	                public void run() {
	                	try {
	                		//业务处理
		                	DrgsGrouper drgsGrouper = DrgsGrouperFactory.getInstance().getDrgsGrouper(drgsData);
	        				drgsGrouper.startGrouping();
	        				afterDrgsDataGroup(drgsData);
						} catch (Exception e) {
							log.error("主单号:"+drgsData.getHisId()+",分组异常:",e.fillInStackTrace());
							e.printStackTrace();
						}
        				try {
        					DrgsResultService.getInstance().putDataToQueue(drgsData.getIntermediateResult());
        					if((atomicCount.addAndGetInsertNum(1)) % INSERT_NUM == 0){
        						DrgsResultService.getInstance().batchInsertDrgsResult(atomicCount,INSERT_NUM,drgsGroupRecord.getGroupNo(),date);
        					}
						} catch (Exception e) {
							log.error("主单号:"+drgsData.getHisId()+",插入异常:",e.fillInStackTrace());
						}
        				if(isFinishedDrgsGroup()){
        					try {
        						drgsGroupRecord.setGroupTime(DateUtil.getTimeBetweenStartAndNow(atomicCount.getDrgsTime()));
                				//2.4 插入数据到结果表td_drgs_group_result  
                				//2.5 更新记录表td_drgs_controll的分组条数、分组耗时、分组状态
                    			drgsGroupRecord.setGroupCount(atomicCount.getTotal());
                    			drgsGroupRecord.setGroupState(GroupState.FINISHEDGROUP.valueOfState());
                    			drgsGroupRecord.setMessage(GroupState.FINISHEDGROUP.valueOfMessage());
                    			drgsGroupRecordMapper.updateDrgsGroupRecord(drgsGroupRecord);
                    			DrgsResultService.getInstance().batchInsertDrgsResult(atomicCount,atomicCount.getTotal()%INSERT_NUM,drgsGroupRecord.getGroupNo(),date);
                    			log.info("共处理{}条数据!耗时{}ms",atomicCount.getTotal(),DateUtil.getTimeBetweenStartAndNow(atomicCount.getDrgsTime()));
                    			atomicCount.initCountAndTotal();
							} catch (Exception e) {
								drgsGroupRecord.setGroupState(GroupState.ERRORGROUP.valueOfState());
								drgsGroupRecord.setMessage(GroupState.ERRORGROUP.valueOfMessage(e.getMessage()));
								drgsGroupRecordMapper.updateDrgsGroupRecord(drgsGroupRecord);
								log.error("主单号:"+drgsData.getHisId()+",更新数据异常:",e.fillInStackTrace());
							}finally{
								try {
									synchronizedQueue.take();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
                		}
	                }

					private synchronized boolean isFinishedDrgsGroup() {
						return atomicCount.getQueryNum() == atomicCount.addAndGetTotal(1);
					}
	            });
			}
		}
	}
	
	private static void afterDrgsDataGroup(DrgsData drgsData) {
    	if(atomicCount.addAndGetCount(1) % 1000 ==0){
    		long time = DateUtil.getTimeBetweenStartAndNow(atomicCount.getStartTime());
    		log.info("共处理了{}条数据,处理1000条耗时:{}ms",atomicCount.getTotal(),time);
    		atomicCount.initStartTime();
    	}
	}

	public String drgsGroupByHisId(String hisId) {
		long start = DateUtil.getNowTime();
		DrgsData drgsData = drgsDataMapper.selectDrgsDataByHisId(hisId);
		drgsData.setNeedReturnResult(true);
		DiseaseManager.newManager(drgsData).setDiseaseList();
		OperationManager.newManager(drgsData).getOperations();
		DrgsItemManager.newManager(drgsData).getDrgsItemData();
		
		DrgsGrouper drgsGrouper = DrgsGrouperFactory.getInstance().getDrgsGrouper(drgsData);
		drgsGrouper.startGrouping();
		log.info("对象信息:{}",drgsData.getIntermediateResult().toString());
		log.info("{}分组完成。drgsCode:{}。耗时{}ms",hisId,drgsData.getIntermediateResult().getDrgCode(),DateUtil.getTimeBetweenStartAndNow(start));
		return drgsData.getIntermediateResult().getDrgCode();
	}
	
	public String drgsGroupByParameters(String hisId,String icd9,String unMainOpr,String icd10,String subDisease) {
		long start = DateUtil.getNowTime();
		DrgsData drgsData = drgsDataMapper.selectDrgsDataByHisId(hisId);
		drgsData.setSingleDataGroup(true);
		drgsData.setNeedReturnResult(true);
		drgsData.getIntermediateResult().setIcd9(icd9);
		drgsData.getIntermediateResult().setIcd10(icd10);
		drgsData.getIntermediateResult().setUnMainOprStr(unMainOpr);
		drgsData.getIntermediateResult().setPrimaryDiagnosisCode(subDisease);
		DiseaseManager.newManager(drgsData).setDiseaseList();
		OperationManager.newManager(drgsData).getOperations();
		
		DrgsGrouper drgsGrouper = DrgsGrouperFactory.getInstance().getDrgsGrouper(drgsData);
		drgsGrouper.startGrouping();
		log.info("{}分组完成。drgsCode:{}。耗时{}ms",hisId,drgsData.getIntermediateResult().getDrgCode(),DateUtil.getTimeBetweenStartAndNow(start));
		return drgsData.getIntermediateResult().getDrgCode();
	}
	
	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("j0008");
		list.add("Q0008");
		Collections.sort(list, new Comparator<String>() {
			public int compare(String o1, String o2) {
            	//o1在前为升序，o2在前为降序
                return ComparisonChain.start()
                        .compare(o1, o2)
                        .result();
            }
        });
		System.out.println();
	}
	
}