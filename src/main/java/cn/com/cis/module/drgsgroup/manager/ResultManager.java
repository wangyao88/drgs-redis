package cn.com.cis.module.drgsgroup.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.com.cis.cache.DrgsGroupResultCache;
import cn.com.cis.module.drgsgroup.entity.AdrgFactor;
import cn.com.cis.module.drgsgroup.entity.COMMTbDrgsgroup;
import cn.com.cis.module.drgsgroup.entity.Disease;
import cn.com.cis.module.drgsgroup.entity.DrgsData;
import cn.com.cis.module.drgsgroup.entity.Operation;
import cn.com.cis.utils.DateUtil;
import cn.com.cis.utils.UUIDUtil;

public class ResultManager {

	private DrgsData drgsData;

	private ResultManager(DrgsData drgsData) {
		this.drgsData = drgsData;
	}

	public static ResultManager newManager(DrgsData drgsData) {
		return new ResultManager(drgsData);
	}

	public void setIntermediateResultAndDrgsGroupResult() {
		setIntermediateResult();
		setDrgsGroupResult();
	}
	
	private void setIntermediateResult(){
		drgsData.getIntermediateResult().setId(UUIDUtil.getUUID());
		
		//设置附属诊断
		List<Disease> subDiseases = DiseaseManager.newManager(drgsData).getSubDisease();
		StringBuilder primaryDiagnosisCode = new StringBuilder();
		if(!subDiseases.isEmpty()){
			for(Disease disease : subDiseases){
				primaryDiagnosisCode.append(disease.getDiseaseId()).append(",");
			}
			primaryDiagnosisCode.deleteCharAt(primaryDiagnosisCode.length()-1);
		}
		drgsData.getIntermediateResult().setPrimaryDiagnosisCode(primaryDiagnosisCode.toString());
		
		//设置主手术对象
		List<Operation> mainOperations = drgsData.getMainOperations();
		if(!mainOperations.isEmpty()){
			drgsData.getIntermediateResult().setIcd9(mainOperations.get(0).getIcd9());
			drgsData.getIntermediateResult().setMainOprCosts(mainOperations.get(0).getCosts()==null?0:mainOperations.get(0).getCosts());
			drgsData.getIntermediateResult().setMainOprDate(mainOperations.get(0).getItemDate()==null?DateUtil.getNullDate():mainOperations.get(0).getItemDate());
			drgsData.getIntermediateResult().setMainOprIcd10(mainOperations.get(0).getIcd10());
		}
		
		//设置非主手术列表
		List<Operation> subOperations = drgsData.getSubOperations();
		Set<String> subOperationIcd9Codes = new HashSet<String>();
		StringBuilder unMainOprStr = new StringBuilder();
		if(!subOperations.isEmpty()){
			for(Operation operation : subOperations){
				subOperationIcd9Codes.add(operation.getIcd9());
			}
			for(String icd9 : subOperationIcd9Codes){
				unMainOprStr.append(icd9).append(",");
			}
			unMainOprStr.deleteCharAt(unMainOprStr.lastIndexOf(","));
			drgsData.getIntermediateResult().setUnMainOprStr(unMainOprStr.toString());
		}
		
		drgsData.getIntermediateResult().getDrgsGroupResult().setIcd10(drgsData.getIntermediateResult().getIcd10());
		drgsData.getIntermediateResult().setIsHasMainOpr(mainOperations.isEmpty()?"0":"1");
		drgsData.getIntermediateResult().setAgeType(drgsData.getAgeType());
		drgsData.getIntermediateResult().setWeightType(drgsData.getWeightType());
		drgsData.getIntermediateResult().setSex(drgsData.getPatientSex());
		drgsData.getIntermediateResult().setUploadDate(drgsData.getUploadDate());
		drgsData.getIntermediateResult().setDrgGroupDate(drgsData.getGroupDate());
		drgsData.getIntermediateResult().setHisId(drgsData.getHisId());
		drgsData.getIntermediateResult().setGroupNo(drgsData.getGroupNo());
	}
	
	private void setDrgsGroupResult(){
		drgsData.getIntermediateResult().getDrgsGroupResult().setGroupNo(drgsData.getGroupNo());
		drgsData.getIntermediateResult().getDrgsGroupResult().setHisId(drgsData.getHisId());
		drgsData.getIntermediateResult().getDrgsGroupResult().setUploadDate(drgsData.getUploadDate());
		drgsData.getIntermediateResult().getDrgsGroupResult().setDrgsCode(drgsData.getIntermediateResult().getDrgCode());
		drgsData.getIntermediateResult().getDrgsGroupResult().setIcd10(drgsData.getIntermediateResult().getIcd10());
		drgsData.getIntermediateResult().getDrgsGroupResult().setPrimaryDiagnosisCode(drgsData.getIntermediateResult().getPrimaryDiagnosisCode());
		drgsData.getIntermediateResult().getDrgsGroupResult().setIcd9(drgsData.getIntermediateResult().getIcd9());
		drgsData.getIntermediateResult().getDrgsGroupResult().setUnMainOprStr(drgsData.getIntermediateResult().getUnMainOprStr());
		drgsData.getIntermediateResult().getDrgsGroupResult().setLoadFlag(drgsData.getLoadFlag());
		drgsData.getIntermediateResult().getDrgsGroupResult().setGroupDate(drgsData.getIntermediateResult().getDrgGroupDate());
		drgsData.getIntermediateResult().getDrgsGroupResult().setMatchCount(drgsData.getIntermediateResult().getMatchCount());
	}
	
	public void setDrgCode(){
		drgsData.getIntermediateResult().setDrgCode("GHBZ");
	}
	
	public void adapterAndSetDrgsMidResult(List<COMMTbDrgsgroup> results) {
		COMMTbDrgsgroup result = results.get(0);
		//中间结果
		drgsData.getIntermediateResult().setMatchCount(result.getMatchCount());
		drgsData.getIntermediateResult().setDrgType(result.getDrgType());
		drgsData.getIntermediateResult().setDrgCode(result.getDrgCnCode());
		String resultAdrg = result.getOprgroupCode();
		drgsData.getIntermediateResult().setResultAdrg(resultAdrg);
		for(AdrgFactor af : drgsData.getIntermediateResult().getOtherFactors()){
			if(af.getAdrg().equals(resultAdrg)){
				drgsData.getIntermediateResult().setOtherFactor1(af.getFactorList().get(0));
				drgsData.getIntermediateResult().setOtherFactor2(af.getFactorList().get(1));
				drgsData.getIntermediateResult().setOtherFactor3(af.getFactorList().get(2));
			}
		}
		if(drgsData.isNeedReturnResult()){
			DrgsGroupResultCache.getInstance().setCache(results,drgsData.getIntermediateResult());
		}
	}
}
