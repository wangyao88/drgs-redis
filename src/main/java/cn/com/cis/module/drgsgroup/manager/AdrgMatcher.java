package cn.com.cis.module.drgsgroup.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import cn.com.cis.cache.DataCache;
import cn.com.cis.cache.redis.service.RedisOperationService;
import cn.com.cis.common.utils.StringUtil;
import cn.com.cis.module.drgsgroup.entity.AdrgFactor;
import cn.com.cis.module.drgsgroup.entity.COMMTbDrgsgroup;
import cn.com.cis.module.drgsgroup.entity.DrgsData;
import cn.com.cis.utils.ServiceAndMapperUtil;

import com.google.common.collect.ComparisonChain;

public class AdrgMatcher {

	private DrgsData drgsData;
	private RedisOperationService redisOperationService;

	private AdrgMatcher(DrgsData drgsData) {
		this.drgsData = drgsData;
		this.redisOperationService = ServiceAndMapperUtil.getService().getRedisOperationService();
	}

	public static AdrgMatcher newMatcher(DrgsData drgsData) {
		return new AdrgMatcher(drgsData);
	}

	public void match() {
		List<COMMTbDrgsgroup> results = new ArrayList<COMMTbDrgsgroup>();
		HashMap<String,COMMTbDrgsgroup> map = new HashMap<String,COMMTbDrgsgroup>();//存放所有adrg对应的匹配数量最多的分组结果
		//遍历主单其他因素列表,去adrg匹配表进行匹配,将匹配结果放入drgCnCodeList中.
		for(AdrgFactor factors : drgsData.getIntermediateResult().getOtherFactors()){
			List<String> drgCnCodeList = new ArrayList<String>();
			String tempAdrg = factors.getAdrg().toUpperCase();
			String key = tempAdrg+"#ccFlag#"+drgsData.getIntermediateResult().getCcFlag();
			if(redisOperationService.mapContainsStringKey(DataCache.DRGS_GROUP_MATCH_MAP, key)){
				drgCnCodeList.addAll(redisOperationService.getMapSetStringValue(DataCache.DRGS_GROUP_MATCH_MAP, key));
			}
			
			//-----------------otherFactor START-------------------------
			String otherFactorForAll = "";
			List<String> otherFactorForTwo = new ArrayList<String>();
			List<String> otherFactorForOne = new ArrayList<String>();
			List<String> factorList = new ArrayList<String>();
			if(!StringUtil.isEmpty(factors.getFactorList().get(0))){
				factorList.add(factors.getFactorList().get(0));
			}
			if(!StringUtil.isEmpty(factors.getFactorList().get(1))){
				factorList.add(factors.getFactorList().get(1));
			}
			if(!StringUtil.isEmpty(factors.getFactorList().get(2))){
				factorList.add(factors.getFactorList().get(2));
			}
			if(factorList.size() == 0){
				otherFactorForAll = tempAdrg+"#otherFactor#null#null#null";
				otherFactorForTwo.add(tempAdrg+"#otherFactorForTwo#null#null");
				otherFactorForOne.add(tempAdrg+"#otherFactorForOne#null");
			}else if(factorList.size() == 1){
				otherFactorForAll = tempAdrg+"#otherFactor#"+factorList.get(0)+"#null#null";
				otherFactorForTwo.add(tempAdrg+"#otherFactorForTwo#null#null");
				otherFactorForTwo.add(tempAdrg+"#otherFactorForTwo#"+factorList.get(0)+"#null");
				otherFactorForOne.add(tempAdrg+"#otherFactorForOne#null");
				otherFactorForOne.add(tempAdrg+"#otherFactorForOne#"+factorList.get(0));
			}else if(factorList.size() == 2){
				otherFactorForAll = tempAdrg+"#otherFactor#"+factorList.get(0)+"#"+factorList.get(1)+"#null";
				otherFactorForTwo.add(tempAdrg+"#otherFactorForTwo#"+factorList.get(0)+"#null");
				otherFactorForTwo.add(tempAdrg+"#otherFactorForTwo#"+factorList.get(0)+"#"+factorList.get(1));
				otherFactorForTwo.add(tempAdrg+"#otherFactorForTwo#"+factorList.get(1)+"#null");
				otherFactorForOne.add(tempAdrg+"#otherFactorForOne#null");
				otherFactorForOne.add(tempAdrg+"#otherFactorForOne#"+factorList.get(0));
				otherFactorForOne.add(tempAdrg+"#otherFactorForOne#"+factorList.get(1));
			}else{
				otherFactorForAll = tempAdrg+"#otherFactor#"+factorList.get(0)+"#"+factorList.get(1)+"#"+factorList.get(2);
				otherFactorForTwo.add(tempAdrg+"#otherFactorForTwo#"+factorList.get(0)+"#"+factorList.get(1));
				otherFactorForTwo.add(tempAdrg+"#otherFactorForTwo#"+factorList.get(0)+"#"+factorList.get(2));
				otherFactorForTwo.add(tempAdrg+"#otherFactorForTwo#"+factorList.get(1)+"#"+factorList.get(2));
				otherFactorForOne.add(tempAdrg+"#otherFactorForOne#"+factorList.get(0));
				otherFactorForOne.add(tempAdrg+"#otherFactorForOne#"+factorList.get(1));
				otherFactorForOne.add(tempAdrg+"#otherFactorForOne#"+factorList.get(2));
			}
			
			if(redisOperationService.mapContainsStringKey(DataCache.DRGS_GROUP_MATCH_MAP, otherFactorForAll)){
				drgCnCodeList.addAll(redisOperationService.getMapSetStringValue(DataCache.DRGS_GROUP_MATCH_MAP, otherFactorForAll));
				drgCnCodeList.addAll(redisOperationService.getMapSetStringValue(DataCache.DRGS_GROUP_MATCH_MAP, otherFactorForAll));
				drgCnCodeList.addAll(redisOperationService.getMapSetStringValue(DataCache.DRGS_GROUP_MATCH_MAP, otherFactorForAll));
			}else{
				boolean isMacthTwoFactor = false;
				for(String strForTwo : otherFactorForTwo){
					if(redisOperationService.mapContainsStringKey(DataCache.DRGS_GROUP_MATCH_MAP, strForTwo)){
						drgCnCodeList.addAll(redisOperationService.getMapSetStringValue(DataCache.DRGS_GROUP_MATCH_MAP, strForTwo));
						drgCnCodeList.addAll(redisOperationService.getMapSetStringValue(DataCache.DRGS_GROUP_MATCH_MAP, strForTwo));
						isMacthTwoFactor = true;
						break;
					}
				}
				if(!isMacthTwoFactor){
					for(String strForOne : otherFactorForOne){
						if(redisOperationService.mapContainsStringKey(DataCache.DRGS_GROUP_MATCH_MAP, strForOne)){
							drgCnCodeList.addAll(redisOperationService.getMapSetStringValue(DataCache.DRGS_GROUP_MATCH_MAP, strForOne));
							break;
						}
					}
				}
			}
			//-----------------otherFactor END-------------------------
			
			if(redisOperationService.mapContainsStringKey(DataCache.DRGS_GROUP_MATCH_MAP, tempAdrg+"#drgsSex#"+drgsData.getPatientSex())){
				drgCnCodeList.addAll(redisOperationService.getMapSetStringValue(DataCache.DRGS_GROUP_MATCH_MAP, tempAdrg+"#drgsSex#"+drgsData.getPatientSex()));
			}
			if(redisOperationService.mapContainsStringKey(DataCache.DRGS_GROUP_MATCH_MAP, tempAdrg+"#drgsAge#"+drgsData.getAgeType())){
				drgCnCodeList.addAll(redisOperationService.getMapSetStringValue(DataCache.DRGS_GROUP_MATCH_MAP, tempAdrg+"#drgsAge#"+drgsData.getAgeType()));
			}
			if(redisOperationService.mapContainsStringKey(DataCache.DRGS_GROUP_MATCH_MAP, tempAdrg+"#drgsWeight#"+drgsData.getWeightType())){
				drgCnCodeList.addAll(redisOperationService.getMapSetStringValue(DataCache.DRGS_GROUP_MATCH_MAP, tempAdrg+"#drgsWeight#"+drgsData.getWeightType()));
			}
			if(redisOperationService.mapContainsStringKey(DataCache.DRGS_GROUP_MATCH_MAP, tempAdrg+"#dfxzycsFlag#"+drgsData.getIntermediateResult().getDfxzycsFlag())){
				drgCnCodeList.addAll(redisOperationService.getMapSetStringValue(DataCache.DRGS_GROUP_MATCH_MAP, tempAdrg+"#dfxzycsFlag#"+drgsData.getIntermediateResult().getDfxzycsFlag()));
			}
			if(redisOperationService.mapContainsStringKey(DataCache.DRGS_GROUP_MATCH_MAP, tempAdrg+"#drgsOutcome#0")){
				drgCnCodeList.addAll(redisOperationService.getMapSetStringValue(DataCache.DRGS_GROUP_MATCH_MAP, tempAdrg+"#drgsOutcome#0"));
			}
			if(redisOperationService.mapContainsStringKey(DataCache.DRGS_GROUP_MATCH_MAP, tempAdrg+"#drgsTrtime#3")){
				drgCnCodeList.addAll(redisOperationService.getMapSetStringValue(DataCache.DRGS_GROUP_MATCH_MAP, tempAdrg+"#drgsTrtime#3"));
			}
			
			HashMap<String,Integer> drgCnCodeMap = new HashMap<String,Integer>();
			HashSet<COMMTbDrgsgroup> maxDrgCnCodeMatchSet = new HashSet<COMMTbDrgsgroup>();
			int maxMatchCount = 0;
			//遍历drgs列表,记录各个drgs匹配数量,并存入maxDrgCnCodeMatchSet
			for(String drgCnCode : drgCnCodeList){
				COMMTbDrgsgroup tempCOMMTbDrgsgroup = (COMMTbDrgsgroup) redisOperationService.getMapValue(DataCache.DRGS_GROUP_MAP, drgCnCode);
						
				//为了防止属性覆盖，用map记录matchCount最多的对象,对多个adrg找出的相同对象取最大数的对象
				if(map.containsKey(tempCOMMTbDrgsgroup.getDrgCnCode())){
					tempCOMMTbDrgsgroup = map.get(tempCOMMTbDrgsgroup.getDrgCnCode());
				}else{
					map.put(tempCOMMTbDrgsgroup.getDrgCnCode(),tempCOMMTbDrgsgroup);
				}
				
				//重新设置当前匹配的对象的多个用于匹配的值
				if(factors.getAdrg().equals(tempCOMMTbDrgsgroup.getOprgroupCode())){
					verificateAndResetResultParameters(tempCOMMTbDrgsgroup,factors.getFactorList());
				}
				if(drgCnCodeMap.containsKey(drgCnCode)){//如果统计数量的map中包含
					int tempCount = drgCnCodeMap.get(drgCnCode) + 1;
					drgCnCodeMap.put(drgCnCode, tempCount);//修改当前drgCnCode对应的数量
					if(tempCount > maxMatchCount){//如果当前数量大于记录的最大数
						maxDrgCnCodeMatchSet = new HashSet<COMMTbDrgsgroup>();
						maxMatchCount = tempCount;
						if(tempCOMMTbDrgsgroup.getMatchCount() < maxMatchCount){
							tempCOMMTbDrgsgroup.setMatchCount(maxMatchCount);
						}
						maxDrgCnCodeMatchSet.add(tempCOMMTbDrgsgroup);
					}else if(tempCount == maxMatchCount){//如果当前数量等于记录的最大数
						if(tempCOMMTbDrgsgroup.getMatchCount() < maxMatchCount){
							tempCOMMTbDrgsgroup.setMatchCount(maxMatchCount);
						}
						maxDrgCnCodeMatchSet.add(tempCOMMTbDrgsgroup);
					}
				}else{//如果map中不包含
					drgCnCodeMap.put(drgCnCode, 1);
					if(maxMatchCount < 1){
						maxMatchCount = 1;
						if(tempCOMMTbDrgsgroup.getMatchCount() < maxMatchCount){
							tempCOMMTbDrgsgroup.setMatchCount(maxMatchCount);
						}
						maxDrgCnCodeMatchSet.add(tempCOMMTbDrgsgroup);
					}else if(maxMatchCount == 1){
						if(tempCOMMTbDrgsgroup.getMatchCount() < maxMatchCount){
							tempCOMMTbDrgsgroup.setMatchCount(maxMatchCount);
						}
						maxDrgCnCodeMatchSet.add(tempCOMMTbDrgsgroup);
					}
				}
//				LogUtil.getInstance(this.getClass()).info(tempCOMMTbDrgsgroup.toString());
			}
			results.addAll(new ArrayList<COMMTbDrgsgroup>(maxDrgCnCodeMatchSet));
		}
		if(results.isEmpty()){
			return;
		}
		sortResultByMatchCountDesc(results);
		removeLessThanMatchCount(results);
		sortDrgsResultLastest(results);
		ResultManager.newManager(drgsData).adapterAndSetDrgsMidResult(results);
	}

	/**
	 * 按匹配数量降序排序
	 * @param results
	 */
	private void sortResultByMatchCountDesc(List<COMMTbDrgsgroup> results) {
		Collections.sort(results, new Comparator<COMMTbDrgsgroup>() {
			@Override
            public int compare(COMMTbDrgsgroup o1, COMMTbDrgsgroup o2) {
            	//o1在前为升序，o2在前为降序
                return ComparisonChain.start()
                        .compare(o2.getMatchCount(), o1.getMatchCount())
                        .result();
            }
        });
	}
	
	private void verificateAndResetResultParameters(COMMTbDrgsgroup cOMMTbDrgsgroup, List<String> factorList) {
		cOMMTbDrgsgroup.setCcFlag(drgsData.getIntermediateResult().getCcFlag());
		cOMMTbDrgsgroup.setDrgsAge(drgsData.getAgeType().toString());
		cOMMTbDrgsgroup.setDrgsSex(drgsData.getPatientSex());
		cOMMTbDrgsgroup.setDrgsWeight(drgsData.getWeightType().toString());
//		cOMMTbDrgsgroup.setDfxzycsFlag(drgsData.getIntermediateResult().getDfxzycsFlag());
		cOMMTbDrgsgroup.setOtherFactorId1(factorList.get(0));
		cOMMTbDrgsgroup.setOtherFactorId2(factorList.get(1));
		cOMMTbDrgsgroup.setOtherFactorId3(factorList.get(2));
	}

	private void removeLessThanMatchCount(List<COMMTbDrgsgroup> results) {
		int resultMaxMatchCount = results.get(0).getMatchCount();
		//遍历drgs匹配结果列表将匹配数量小于匹配结果列表最大匹配数量的匹配结果去掉
		for(int i = 0 ; i < results.size() ; i++){
			COMMTbDrgsgroup tempCOMMTbDrgsgroup = results.get(i);
			if(tempCOMMTbDrgsgroup.getMatchCount() < resultMaxMatchCount){
				results.remove(tempCOMMTbDrgsgroup);
				i--;
			}
			
			//判断此adrg是否是来自于mainOprFirst的默认adrg
			int defaultAdrg = 0; 
			if(!StringUtil.isEmpty(drgsData.getIntermediateResult().getDefaultAdrgs())&&tempCOMMTbDrgsgroup.getOprgroupCode().equals(drgsData.getIntermediateResult().getDefaultAdrgs())){
				defaultAdrg = 1;
			}
			tempCOMMTbDrgsgroup.setDefaultAdrg(defaultAdrg);
		}
	}

	/**
	 * 对drgs匹配列表排序.drgType降序,其他因素升序,过滤drgs结果表不包含者优先,drgs升序
	 * @param results
	 */
	private void sortDrgsResultLastest(List<COMMTbDrgsgroup> results) {
		Collections.sort(results, new Comparator<COMMTbDrgsgroup>() {
			@Override
            public int compare(COMMTbDrgsgroup o1, COMMTbDrgsgroup o2) {
            	String dfxzycsFlayForo1 = "0";
            	String dfxzycsFlayForo2 = "0";
            	if(!StringUtil.isEmpty(drgsData.getIntermediateResult().getDfxzycsFlag())){
            		if(!StringUtil.isEmpty(o1.getDfxzycsFlag())&&o1.getDfxzycsFlag().equals(drgsData.getIntermediateResult().getDfxzycsFlag())){
	            		dfxzycsFlayForo1 = "1";
	            	}
	            	if(!StringUtil.isEmpty(o2.getDfxzycsFlag())&&o2.getDfxzycsFlag().equals(drgsData.getIntermediateResult().getDfxzycsFlag())){
	            		dfxzycsFlayForo2 = "1";
	            	}
            	}
            	
            	String disdefaultGroup1 = drgsData.getIntermediateResult().getIcd10() + "#" + o1.getOprgroupCode();
            	String disdefaultGroup2 = drgsData.getIntermediateResult().getIcd10() + "#" + o2.getOprgroupCode();
            	int defaultGroup1 = redisOperationService.setContainsStringValue(DataCache.DIS_DEFAULT_GROUP_SET, disdefaultGroup1)?0:1;
            	int defaultGroup2 = redisOperationService.setContainsStringValue(DataCache.DIS_DEFAULT_GROUP_SET, disdefaultGroup2)?0:1;
            	
            	String otherFactor1Foro1 = "0";//为空=1 非空=0，升序排列
            	String otherFactor1Foro2 = "0";
            	if(StringUtil.isNull(o1.getOtherFactorId1()) && StringUtil.isNull(o2.getOtherFactorId1())){
            		otherFactor1Foro1 = "1";
            		otherFactor1Foro2 = "1";
            	}
            	if(!StringUtil.isNull(o1.getOtherFactorId1()) && StringUtil.isNull(o2.getOtherFactorId1())){
            		otherFactor1Foro1 = "1";
            		otherFactor1Foro2 = "0";
            	}
            	if(StringUtil.isNull(o1.getOtherFactorId1()) && !StringUtil.isNull(o2.getOtherFactorId1())){
            		otherFactor1Foro1 = "0";
            		otherFactor1Foro2 = "1";
            	}
            	if(!StringUtil.isNull(o1.getOtherFactorId1()) && !StringUtil.isNull(o2.getOtherFactorId1())){
            		otherFactor1Foro1 = o1.getOtherFactorId1();
            		otherFactor1Foro2 = o2.getOtherFactorId1();
            	}
            	//o1在前为升序，o2在前为降序
                return ComparisonChain.start()
                		.compare(dfxzycsFlayForo2, dfxzycsFlayForo1)
                        .compare(o2.getDrgType(), o1.getDrgType())
                        .compare(otherFactor1Foro2,otherFactor1Foro1)
                        .compare(o2.getDefaultAdrg(),o1.getDefaultAdrg())
                        .compare(defaultGroup1,defaultGroup2)
                        .compare(o1.getDrgCnCode(), o2.getDrgCnCode())
                        .result();
            }
        });
	}
}
