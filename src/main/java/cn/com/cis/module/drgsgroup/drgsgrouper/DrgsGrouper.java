package cn.com.cis.module.drgsgroup.drgsgrouper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;

import org.apache.commons.lang3.StringUtils;

import cn.com.cis.cache.DataCache;
import cn.com.cis.module.drgsgroup.entity.Disease;
import cn.com.cis.module.drgsgroup.entity.DrgsData;
import cn.com.cis.module.drgsgroup.entity.RedisBean;
import cn.com.cis.module.drgsgroup.manager.AdrgManager;
import cn.com.cis.module.drgsgroup.manager.AdrgMatcher;
import cn.com.cis.module.drgsgroup.manager.ComplicationManager;
import cn.com.cis.module.drgsgroup.manager.DiseaseManager;
import cn.com.cis.module.drgsgroup.manager.OperationManager;
import cn.com.cis.module.drgsgroup.manager.OtherFactorManager;
import cn.com.cis.module.drgsgroup.manager.ResultManager;
import cn.com.cis.utils.ServiceAndMapperUtil;

@Data
public abstract class DrgsGrouper {
	
	protected DrgsData drgsData;
	protected Set<String> diseaseMdcAdrgSet;
	
	public DrgsGrouper(DrgsData drgsData) {
		this.drgsData = drgsData;
	}

	public void startGrouping() {
		if(!StringUtils.isEmpty(DiseaseManager.newManager(drgsData).setAndGetIcd10())){
			if(drgsData.getLongterm()==0){
				setDfxzycsFlag();
				diseaseMdcAdrgSet = AdrgManager.newManager(drgsData).findMainDiseaseAdrgs();
				if(drgsData.getIntermediateResult().isHavingDiseaseAdrg()){
					OperationManager.newManager(this).configure();
					ComplicationManager.newManager(drgsData).setCcFlag();
					AdrgManager.newManager(drgsData).setOperationAndDefaultAdrgs();
					OtherFactorManager.newManager(drgsData).findOtherFactors();
					AdrgMatcher.newMatcher(drgsData).match();
				}
			}else if(drgsData.getLongterm()==2){
				ResultManager.newManager(drgsData).setDrgCode();
			}
		}
		if(getCleanIcd9Flag()){
			OperationManager.newManager(drgsData).cleanMainOperations();
		}
		ResultManager.newManager(drgsData).setIntermediateResultAndDrgsGroupResult();
	}

	private boolean getCleanIcd9Flag() {
		return StringUtils.isEmpty(drgsData.getIntermediateResult().getIcd10()) || 
				!drgsData.getIntermediateResult().isHavingDiseaseAdrg();
	}

	public abstract void setMainOperationFlag();

	public abstract void getMainOperation(Set<String> diseaseMdcAdrgSet);

	private void setDfxzycsFlag() {
		List<Disease> subDiseases = DiseaseManager.newManager(drgsData).getSubDisease();
		if(ServiceAndMapperUtil.getService().getRedisOperationService().mapContainsStringKey(DataCache.DFXZYCS_MAP, drgsData.getIntermediateResult().getIcd10())){
			Set<RedisBean> dfxzycsSet = ServiceAndMapperUtil.getService().getRedisOperationService().getMapSetRedisBeanValue(DataCache.DFXZYCS_MAP, drgsData.getIntermediateResult().getIcd10());
			for(Disease disease : subDiseases){
				if(ServiceAndMapperUtil.getService().getRedisOperationService().mapContainsStringKey(DataCache.DFXZYCS_MAP, disease.getDiseaseId())){
					dfxzycsSet.addAll(ServiceAndMapperUtil.getService().getRedisOperationService().getMapSetRedisBeanValue(DataCache.DFXZYCS_MAP, disease.getDiseaseId()));
				}
			}
			if(dfxzycsSet.size()>1){
				drgsData.getIntermediateResult().setDfxzycsFlag("1");
			}else{
				drgsData.getIntermediateResult().setDfxzycsFlag("0");
			}
		}else{
			drgsData.getIntermediateResult().setDfxzycsFlag("0");
		}
	}
	
	protected Set<String> getMdcSet(Set<String> adrgSet){
		Set<String> mdcSet = new HashSet<String>();
		for(String adrg : adrgSet){
			String mdcStrings = ServiceAndMapperUtil.getService().getRedisOperationService().getMapStringValue(DataCache.MDC_MAP, adrg.toUpperCase());
			if(!StringUtils.isEmpty(mdcStrings)){//若找到的MDC不为空
				String[] mdcArr = mdcStrings.split("#");
				mdcSet.addAll(new ArrayList<String>(Arrays.asList(mdcArr)));
			}
		}
		return mdcSet;
	}
	
}
