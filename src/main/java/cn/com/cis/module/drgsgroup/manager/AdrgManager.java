package cn.com.cis.module.drgsgroup.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import cn.com.cis.cache.DataCache;
import cn.com.cis.cache.redis.service.RedisOperationService;
import cn.com.cis.common.utils.StringUtil;
import cn.com.cis.module.drgsgroup.entity.DrgsData;
import cn.com.cis.module.drgsgroup.entity.Operation;
import cn.com.cis.utils.ServiceAndMapperUtil;

public class AdrgManager {
	
	private DrgsData drgsData;
	private RedisOperationService redisOperationService;

	private AdrgManager(DrgsData drgsData) {
		this.drgsData = drgsData;
		this.redisOperationService = ServiceAndMapperUtil.getService().getRedisOperationService();
	}

	public static AdrgManager newManager(DrgsData drgsData) {
		return new AdrgManager(drgsData);
	}
	
	public void setDiseaseAdrgs(String adrgStrings){
		drgsData.getIntermediateResult().setDiseaseAdrgs(adrgStrings.replaceAll("#", ","));
	}
	
	public void setOperationAdrgs(){
		if(!drgsData.getMainOperations().isEmpty()){
			Operation operation = drgsData.getMainOperations().get(0);
			if(!StringUtils.isEmpty(operation.getAdrg())){
				String[] adrgs = operation.getAdrg().split("#");
				StringBuilder mainOperationAdrgs = new StringBuilder();
				for(String adrg : adrgs){
					drgsData.getIntermediateResult().getAdrgs().add(adrg);
					mainOperationAdrgs.append(adrg).append(",");
				}
				mainOperationAdrgs.deleteCharAt(mainOperationAdrgs.length()-1);
				drgsData.getIntermediateResult().setMainOprAdrgs(mainOperationAdrgs.toString());
			}
		}
	}

	public void setDefaultAdrgs(){
		if(!drgsData.getMainOperations().isEmpty()){
			String icd9 = drgsData.getMainOperations().get(0).getIcd9().toUpperCase();
			String icd10 = drgsData.getIntermediateResult().getIcd10().toUpperCase();
			String key = (icd9+"#"+icd10).toUpperCase();
			if(redisOperationService.mapContainsStringKey(DataCache.DEFAULT_ADRG_MAP, key)){
				String defaultAdrg = redisOperationService.getMapStringValue(DataCache.DEFAULT_ADRG_MAP, key);
				drgsData.getIntermediateResult().getAdrgs().add(defaultAdrg);
				drgsData.getIntermediateResult().setDefaultAdrgs(defaultAdrg);
			}
		}
	}

	public void setOperationAndDefaultAdrgs() {
		setOperationAdrgs();
		setDefaultAdrgs();
	}

	public Set<String> findMainDiseaseAdrgs() {
		HashSet<String> diseaseMdcAdrgSet = new HashSet<String>();
		String adrgMdcStrings = redisOperationService.getMapStringValue(DataCache.DISGROUPMAP_KEY, drgsData.getIntermediateResult().getOriginalIcd10().toUpperCase());
		if(!StringUtil.isEmpty(adrgMdcStrings)){
			String[] adrgArr = adrgMdcStrings.split("#");
			diseaseMdcAdrgSet.addAll(new ArrayList<String>(Arrays.asList(adrgArr)));
		}
		
		String adrgStrings = redisOperationService.getMapStringValue(DataCache.DISGROUPMAP_KEY, drgsData.getIntermediateResult().getIcd10().toUpperCase());
		if(!StringUtil.isEmpty(adrgStrings)){
			String[] adrgArr = adrgStrings.split("#");
			drgsData.getIntermediateResult().getAdrgs().addAll(new ArrayList<String>(Arrays.asList(adrgArr)));
			AdrgManager.newManager(drgsData).setDiseaseAdrgs(adrgStrings.replaceAll("#", ","));
		}else{
			//如果诊断对应adrg为空,则程序中断
			drgsData.getIntermediateResult().setHavingDiseaseAdrg(false);
		}
		return diseaseMdcAdrgSet;
	}

	public void setOperationAdrgByIcd9Code() {
		for(Operation operation : drgsData.getSubOperations()){
			if(redisOperationService.mapContainsStringKey(DataCache.OPR_GROUP_MAP, operation.getIcd9().toUpperCase())){
				operation.setAdrg(redisOperationService.getMapStringValue(DataCache.OPR_GROUP_MAP, operation.getIcd9().toUpperCase()));
			}
		}
		for(Operation operation : drgsData.getMainOperations()){
			if(redisOperationService.mapContainsStringKey(DataCache.OPR_GROUP_MAP, operation.getIcd9().toUpperCase())){
				operation.setAdrg(redisOperationService.getMapStringValue(DataCache.OPR_GROUP_MAP, operation.getIcd9().toUpperCase()));
			}
		}
	}

}
