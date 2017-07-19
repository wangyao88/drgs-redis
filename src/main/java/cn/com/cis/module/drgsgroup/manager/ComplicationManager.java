package cn.com.cis.module.drgsgroup.manager;

import java.util.HashSet;
import java.util.List;

import cn.com.cis.cache.DataCache;
import cn.com.cis.cache.redis.service.RedisOperationService;
import cn.com.cis.module.drgsgroup.entity.Disease;
import cn.com.cis.module.drgsgroup.entity.DrgsData;
import cn.com.cis.utils.ServiceAndMapperUtil;

public class ComplicationManager {
	
	private DrgsData drgsData;
	private RedisOperationService redisOperationService;

	private ComplicationManager(DrgsData drgsData) {
		this.drgsData = drgsData;
		this.redisOperationService = ServiceAndMapperUtil.getService().getRedisOperationService();
	}

	public static ComplicationManager newManager(DrgsData drgsData) {
		return new ComplicationManager(drgsData);
	}

	public void setCcFlag() {
		boolean isContinueFilter = true;
		
		HashSet<String> mccStdCodes = drgsData.getMccStdCodes();
		for(String stdCode : mccStdCodes){
			if(redisOperationService.setContains(DataCache.HBZ_MCC_SET, stdCode)){
				drgsData.getIntermediateResult().setCcFlag("2");//严重合并症
				isContinueFilter = false;
				break;
			}
		}
		String diseaseId = drgsData.getIntermediateResult().getIcd10();
		String originDiseaseId = drgsData.getIntermediateResult().getOriginalIcd10();
		
		List<Disease> subDiseases = DiseaseManager.newManager(drgsData).getSubDisease();
		//过滤从诊断,选出合并症标识为2的,选出后去合并症排除表过滤
		if(isContinueFilter){
			for(Disease disease : subDiseases){
				//以原主诊断+除备选主诊断以外的次要诊断判断CC/MCC
				if(drgsData.getSecondaryDisease() != null 
						&& drgsData.getSecondaryDisease().getIcdCode().equals(disease.getDiseaseId())){
					continue;
				}
				String diagnosisForMcc = disease.getDiseaseId();
				if(diagnosisForMcc.equals(diseaseId) || diagnosisForMcc.equals(originDiseaseId)){
					continue;
				}
				if(redisOperationService.setContains(DataCache.HBZ_MCC_SET, diagnosisForMcc)){//合并症为2的 
					String exception =  originDiseaseId + "2" + "#" + diagnosisForMcc;
					if(!redisOperationService.setContains(DataCache.HBZ_EXCEPTION_SET, exception) && !redisOperationService.setContains(DataCache.HBZ_EXCEPTION_SUBDISEASE_SET, diagnosisForMcc)){//排除表不包含,则不排除
						drgsData.getIntermediateResult().setCcFlag("2");
						isContinueFilter = false;
						break;
					}
				}
			}
		}
		//过滤从诊断,选出合并症标识为1的,选出后去合并症排除表过滤
		if(isContinueFilter){
			for(Disease disease : subDiseases){
				String diagnosisForChargeMcc = disease.getDiseaseId();
				if(diagnosisForChargeMcc.equals(diseaseId) || diagnosisForChargeMcc.equals(originDiseaseId)){
					continue;
				}
				if(redisOperationService.setContains(DataCache.HBZ_CC_SET, diagnosisForChargeMcc)){//合并症为2的
					String exception = originDiseaseId + "1" + "#" + diagnosisForChargeMcc;
					if(!redisOperationService.setContains(DataCache.HBZ_EXCEPTION_SET, exception) && !redisOperationService.setContains(DataCache.HBZ_EXCEPTION_SUBDISEASE_SET, diagnosisForChargeMcc)){//不排除
						drgsData.getIntermediateResult().setCcFlag("1");
						isContinueFilter = false;
						break;
					}
				}
			}
		}
		
		if(isContinueFilter){
			drgsData.getIntermediateResult().setCcFlag("0");
		}
	}

}
