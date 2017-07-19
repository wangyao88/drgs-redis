package cn.com.cis.module.drgsgroup.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cn.com.cis.cache.DataCache;
import cn.com.cis.cache.redis.service.RedisOperationService;
import cn.com.cis.module.drgsgroup.entity.Disease;
import cn.com.cis.module.drgsgroup.entity.DrgsData;
import cn.com.cis.module.drgsgroup.entity.SecondaryDisease;
import cn.com.cis.utils.ServiceAndMapperUtil;

public class DiseaseManager {

	private DrgsData drgsData;
	private RedisOperationService redisOperationService;

	private DiseaseManager(DrgsData drgsData) {
		this.drgsData = drgsData;
		this.redisOperationService = ServiceAndMapperUtil.getService().getRedisOperationService();
	}

	public static DiseaseManager newManager(DrgsData drgsData) {
		return new DiseaseManager(drgsData);
	}

	public void setDiseaseList() {
		setAllDiseaseList();
		setSubDiseaseList();
	}
	
	private void setSecondaryDisease(){
		List<SecondaryDisease> secondaryDiseases = new ArrayList<SecondaryDisease>();
		for(Disease disease : drgsData.getIntermediateResult().getSubDisease()){
			if(redisOperationService.mapContainsStringKey(DataCache.SECONDARY_DISEASE_MAP,disease.getDiseaseId())){
				secondaryDiseases.add((SecondaryDisease)redisOperationService.getMapValue(DataCache.SECONDARY_DISEASE_MAP, disease.getDiseaseId()));
			}
		}
		if(!secondaryDiseases.isEmpty()){
			SecondaryDisease secondaryDisease = ServiceAndMapperUtil.getService().getSecondaryDiseaseService().sortScondaryDiseassesByPriod(secondaryDiseases);
			drgsData.setSecondaryDisease(secondaryDisease);
			drgsData.getIntermediateResult().setIcd10(secondaryDisease.getIcdCode());
		}
	}

	private void setAllDiseaseList() {
		List<Disease> diseases = new ArrayList<Disease>();
		if(drgsData.isSingleDataGroup()){
			String icd10 = drgsData.getIntermediateResult().getIcd10();
			diseases.add(setAndGetDisease(icd10,true));
			
			String diseaseId = drgsData.getIntermediateResult().getPrimaryDiagnosisCode();
			if(!StringUtils.isEmpty(diseaseId)){
				String[] diseaseIds = diseaseId.split(",");
				for(String diseaseIdStr : diseaseIds){
					diseases.add(setAndGetDisease(diseaseIdStr,false));
				}
			}
		}else{
			diseases = ServiceAndMapperUtil.getMapper().getDiseaseMapper().selectAllDiseasesByGroupNoAndHisId(drgsData);
		}
		drgsData.setDiseases(diseases);
	}
	
	private Disease setAndGetDisease(String diseaseId,boolean flag){
		Disease disease = new Disease();
		disease.setDiseaseId(diseaseId);
		disease.setFlag(flag);
		return disease;
	}
	
	private void setSubDiseaseList(){
		List<Disease> subDiseaseList = ServiceAndMapperUtil.getService().getDiseaseService().filterSubDisease(drgsData.getDiseases());
		drgsData.getIntermediateResult().setSubDisease(subDiseaseList);
	}

	public String setAndGetIcd10() {
		Disease disease = ServiceAndMapperUtil.getService().getDiseaseService().filterMainDisease(drgsData.getDiseases());
		if(StringUtils.isEmpty(disease.getDiseaseId())){
			return null;
		}
		setSecondaryDisease();
		if(drgsData.getSecondaryDisease() == null){
			drgsData.getIntermediateResult().setIcd10(disease.getDiseaseId());
		}
		drgsData.getIntermediateResult().setOriginalIcd10(disease.getDiseaseId());
		return drgsData.getIntermediateResult().getIcd10();
	}
	
	public List<Disease> getSubDisease(){
	    return drgsData.getIntermediateResult().getSubDisease();
	}
}
