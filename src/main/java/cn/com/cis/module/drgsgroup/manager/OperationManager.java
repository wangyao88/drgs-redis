package cn.com.cis.module.drgsgroup.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import cn.com.cis.cache.DataCache;
import cn.com.cis.cache.redis.service.RedisOperationService;
import cn.com.cis.module.drgsgroup.drgsgrouper.DrgsGrouper;
import cn.com.cis.module.drgsgroup.entity.DrgsData;
import cn.com.cis.module.drgsgroup.entity.Operation;
import cn.com.cis.utils.ServiceAndMapperUtil;

public class OperationManager {
	
	private DrgsGrouper drgsGrouper;
	private DrgsData drgsData;
	private RedisOperationService redisOperationService;
	
	private OperationManager(DrgsGrouper drgsGrouper){
		this.drgsGrouper = drgsGrouper;
		this.drgsData = drgsGrouper.getDrgsData();
		this.redisOperationService = ServiceAndMapperUtil.getService().getRedisOperationService();
	}
	
	private OperationManager(DrgsData drgsData){
		this.drgsData = drgsData;
	}

	public static OperationManager newManager(DrgsGrouper drgsGrouper) {
		return new OperationManager(drgsGrouper);
	}
	
	public static OperationManager newManager(DrgsData drgsData) {
		return new OperationManager(drgsData);
	}

	public void configure() {
		if(drgsData.isSingleDataGroup()){
			setOperationsInSinglePattern();
			return;
		}
		if(!drgsData.getSubOperations().isEmpty()){//当手术列表不为空的时候
			setMainOperationFlag();
			//通过手术编码（icd9Code）获取手术adrg
			AdrgManager.newManager(drgsData).setOperationAdrgByIcd9Code();
			//过滤主手术
			filterMainOperation();
			//获取主手术
			getMainOperation(drgsGrouper.getDiseaseMdcAdrgSet());
			//过滤子手术
			filterUnMainOprList();
		}
	}

	private void setOperationsInSinglePattern() {
		Operation mainOperation = InitOperationByIcd9AndAdrgs(drgsData.getIntermediateResult().getIcd9());
		drgsData.getMainOperations().add(mainOperation);
		
		String unMainOpr = drgsData.getIntermediateResult().getUnMainOprStr();
		if(!StringUtils.isEmpty(unMainOpr)){
			String[] subIcd9s= unMainOpr.split(",");
			for(String icd9 : subIcd9s){
				Operation operation = InitOperationByIcd9AndAdrgs(icd9);
				drgsData.getSubOperations().add(operation);
			}
		}
	}

	private Operation InitOperationByIcd9AndAdrgs(String icd9) {
		Operation operation = new Operation();
		operation.setIcd9(icd9);
		if(redisOperationService.mapContainsStringKey(DataCache.OPR_GROUP_MAP, operation.getIcd9().toUpperCase())){
			operation.setAdrg(redisOperationService.getMapStringValue(DataCache.OPR_GROUP_MAP, operation.getIcd9().toUpperCase()));
		}
		return operation;
	}
	
	private void getMainOperation(Set<String> diseaseMdcAdrgSet) {
		drgsGrouper.getMainOperation(diseaseMdcAdrgSet);
	}

	private void setMainOperationFlag() {
		drgsGrouper.setMainOperationFlag();
	}

	private void filterUnMainOprList() {
		List<Operation> oprList = drgsData.getSubOperations();
		filterSubOperationsByPaternity(oprList);
		List<Operation> subOperations = ServiceAndMapperUtil.getService().getOperationService().removeMainOperationFromSubOperations(drgsData,oprList);
		drgsData.setSubOperations(subOperations);
	}

	private void filterSubOperationsByPaternity(List<Operation> oprList) {
		Set<String> chidrenOpr = new HashSet<String>();
		//遍历从手术,选出从手术所对应的子手术
		for(Operation operation : oprList){
			if(redisOperationService.mapContainsStringKey(DataCache.PATERNITY_OPR_MAP, operation.getSeqId())){
				chidrenOpr.addAll(redisOperationService.getMapListStringValue(DataCache.PATERNITY_OPR_MAP, operation.getSeqId()));
			}
		}
		//从手术列表排除子手术
		for(int i = 0; i < oprList.size(); i++){
			Operation tempOpr = oprList.get(i);
			if(chidrenOpr.contains(tempOpr.getSeqId())){
				oprList.remove(tempOpr);
				i--;
			}
		}
		chidrenOpr.clear();
	}
	
	public void getOperations() {
		List<Operation> operations = ServiceAndMapperUtil.getMapper().getOperationMapper().selectAllOperationsByGroupNoAndHisId(drgsData);
		drgsData.setSubOperations(operations);
		Collections.addAll(drgsData.getMainOperations(),new Operation[operations.size()]); 
		Collections.copy(drgsData.getMainOperations(), operations);
	}
	
	public void cleanMainOperations(){
		drgsData.setMainOperations(new ArrayList<Operation>());
	}
	
	private void filterMainOperation(){
		List<Operation> mainOprList = drgsData.getMainOperations();
		String icd9 = "";
		String icd10 = "";
		for(int i = 0 ;i<mainOprList.size();i++){
			Operation operation = mainOprList.get(i);
			icd9 = operation.getIcd9();
			icd10 = operation.getIcd10();
			
			if(redisOperationService.setContainsStringValue(DataCache.MAIN_OPR_EXCEPTION_SET, icd9+"#"+icd10)){
				mainOprList.remove(i);
				i--;
			}
		}
		drgsData.setMainOperations(mainOprList);
	}

}
