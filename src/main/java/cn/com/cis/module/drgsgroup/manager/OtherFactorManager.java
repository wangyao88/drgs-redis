package cn.com.cis.module.drgsgroup.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import cn.com.cis.cache.DataCache;
import cn.com.cis.cache.redis.service.RedisOperationService;
import cn.com.cis.enums.OtherFactorJudgeType;
import cn.com.cis.module.drgsgroup.entity.AdrgFactor;
import cn.com.cis.module.drgsgroup.entity.Disease;
import cn.com.cis.module.drgsgroup.entity.DrgsData;
import cn.com.cis.module.drgsgroup.entity.Operation;
import cn.com.cis.utils.ServiceAndMapperUtil;

import com.google.common.collect.ComparisonChain;


@Slf4j
public class OtherFactorManager {

	private DrgsData drgsData;
	private RedisOperationService redisOperationService;

	private OtherFactorManager(DrgsData drgsData) {
		this.drgsData = drgsData;
		this.redisOperationService = ServiceAndMapperUtil.getService().getRedisOperationService();
	}

	public static OtherFactorManager newManager(DrgsData drgsData) {
		return new OtherFactorManager(drgsData);
	}

	/**
	 * 遍历主单的adrg,将主诊断#当前adrg,附属诊断#当前adrg,主手术#当前adrg,非主手术#当前adrg存入otherFactorSet
	 */
	public void findOtherFactors() {
		List<Disease> subDiseases = DiseaseManager.newManager(drgsData).getSubDisease();
		List<Operation> mainOperations = drgsData.getMainOperations();
		for(String adrg : drgsData.getIntermediateResult().getAdrgs()){
//			Set<String> otherFactorSet = initOtherFactorSet(subDiseases,mainOperations, adrg);
//			Set<String> matchedOtherFactorSet = matchOtherFactorSetFromCache(otherFactorSet);
			Set<String> matchedOtherFactorSet = matchOtherFactorSetFromCache(subDiseases,mainOperations, adrg);
			List<String> otherFactorList = initOtherFactorListFromSet(matchedOtherFactorSet);
			sortListByAsc(otherFactorList);
			resetOtherFactorListSize(otherFactorList);
			setOtherFactorOfIntermediateResult(adrg, otherFactorList);
		}
	}

	private Set<String> matchOtherFactorSetFromCache(List<Disease> subDiseases,List<Operation> mainOperations, String adrg) {
		//主诊断其他因素
		String otherFactorMapKey = drgsData.getIntermediateResult().getIcd10()+"#"+adrg;
		List<String> mainDiseaseOtherFactorList = new ArrayList<String>();
		if(redisOperationService.mapContainsStringKey(DataCache.OTHER_FACTOR_MAP, otherFactorMapKey)){
		    mainDiseaseOtherFactorList = Arrays.asList(redisOperationService.getMapStringValue(DataCache.OTHER_FACTOR_MAP, otherFactorMapKey).split("#"));
		}
		HashSet<String> mainDiseaseOtherFactorSet =  new HashSet<String>(mainDiseaseOtherFactorList);
		HashMap<String,Integer> mainDiseaseOtherFactorMap = mutiplyPartFactorDeal(mainDiseaseOtherFactorSet,OtherFactorJudgeType.DISEASE,adrg);
		
		//附属诊断其他因素
		HashSet<String> subDiseaseOtherFactorSet =  new HashSet<String>();
		for(Disease disease : subDiseases){
			String key = disease.getDiseaseId()+"#"+adrg;
			if(redisOperationService.mapContainsStringKey(DataCache.OTHER_FACTOR_MAP, key)){
				subDiseaseOtherFactorSet.addAll(Arrays.asList(redisOperationService.getMapStringValue(DataCache.OTHER_FACTOR_MAP, key).split("#")));
			}
		}
		HashMap<String,Integer> subDiseaseOtherFactorMap = mutiplyPartFactorDeal(subDiseaseOtherFactorSet,OtherFactorJudgeType.SUBDISEASE,adrg);
		
		HashMap<String,Integer> diseaseOtherFactorMap = new HashMap<String,Integer>();
		diseaseOtherFactorMap.putAll(mainDiseaseOtherFactorMap);
		for(String key : subDiseaseOtherFactorMap.keySet()){
			int count = subDiseaseOtherFactorMap.get(key);
			if(diseaseOtherFactorMap.containsKey(key)){
				count += diseaseOtherFactorMap.get(key);
			}
			diseaseOtherFactorMap.put(key, count);
		}
		
		
		Iterator<Map.Entry<String,Integer>> diseaseIterator = diseaseOtherFactorMap.entrySet().iterator();  
        while(diseaseIterator.hasNext()){  
            Map.Entry<String,Integer> entry=diseaseIterator.next();  
            String key = entry.getKey();
            int value = entry.getValue();
            Object[] obj = getMatchUnderlineData(key);
			if(obj[1] != null && Integer.valueOf((String)obj[1]) > value){
				diseaseIterator.remove(); 
			}
        }  
		
		
		//主手术其他因素
		HashSet<String> mainOprOtherFactorSet =  new HashSet<String>();
		if(!mainOperations.isEmpty()){
			String key = mainOperations.get(0).getIcd9()+"#"+adrg;
			if(redisOperationService.mapContainsStringKey(DataCache.OTHER_FACTOR_MAP, key)){
				mainOprOtherFactorSet.addAll(Arrays.asList(redisOperationService.getMapStringValue(DataCache.OTHER_FACTOR_MAP, key).split("#")));
			}
		}
		HashMap<String,Integer> mainOprOtherFactorMap = mutiplyPartFactorDeal(mainOprOtherFactorSet,OtherFactorJudgeType.MAINOPR,adrg);
		
		
		//非主手术其他因素
		HashSet<String> unMainOprOtherFactorSet =  new HashSet<String>();
		for(Operation operation : drgsData.getSubOperations()){
			String key = operation.getIcd9()+"#"+adrg;
			if(redisOperationService.mapContainsStringKey(DataCache.OTHER_FACTOR_MAP, key)){
				unMainOprOtherFactorSet.addAll(Arrays.asList(redisOperationService.getMapStringValue(DataCache.OTHER_FACTOR_MAP, key).split("#")));
			}
		}
		HashMap<String,Integer> unMainOprOtherFactorMap = mutiplyPartFactorDeal(unMainOprOtherFactorSet,OtherFactorJudgeType.UNMAINOPR,adrg);
		
		
		HashMap<String,Integer> oprOtherFactorMap = new HashMap<String,Integer>();
		oprOtherFactorMap.putAll(mainOprOtherFactorMap);
		for(String key : unMainOprOtherFactorMap.keySet()){
			int count = unMainOprOtherFactorMap.get(key);
			if(oprOtherFactorMap.containsKey(key)){
				count += oprOtherFactorMap.get(key);
			}
			oprOtherFactorMap.put(key, count);
		}
		
		Iterator<Map.Entry<String,Integer>> oprIterator = oprOtherFactorMap.entrySet().iterator();  
        while(oprIterator.hasNext()){  
            Map.Entry<String,Integer> entry=oprIterator.next();  
            String key = entry.getKey();
            int value = entry.getValue();
            if(isMatchThirdUnderlinePart(key)){
            	Object[] obj = getMatchUnderlineData(key);
     			if(obj[1] != null && Integer.valueOf((String)obj[1]) > value){
     				oprIterator.remove(); 
     			}
            }
        }
        
        
        HashSet<String> itemOtherFactorSet = new HashSet<String>();
        HashSet<String> otherFacotrStdCodes = drgsData.getOtherFactorStdCodes();
		for(String otherFactorStdCode:otherFacotrStdCodes){
			if(redisOperationService.mapContainsStringKey(DataCache.OTHER_FACTOR_MAP, otherFactorStdCode+"#"+adrg)){
				String tempOtherFactorId = redisOperationService.getMapStringValue(DataCache.OTHER_FACTOR_MAP, otherFactorStdCode+"#"+adrg);
				if(isMatchFirstUnderlineItem(tempOtherFactorId)){
					itemOtherFactorSet.add(tempOtherFactorId);
				}
			}
		}
		
		Set<String> otherFactorSet = new HashSet<String>();
		otherFactorSet.addAll(diseaseOtherFactorMap.keySet());
		otherFactorSet.addAll(oprOtherFactorMap.keySet());
		otherFactorSet.addAll(itemOtherFactorSet);
		return otherFactorSet;
	}
	
	
	private HashMap<String,Integer> mutiplyPartFactorDeal(HashSet<String> set, OtherFactorJudgeType type,String adrg){
		HashMap<String,Integer> resultMap = new HashMap<String,Integer>();
		List<String> tempOtherFactorList = new ArrayList<String>(set);
		for(int i=0;i<tempOtherFactorList.size();i++){
			String otherFactorId = tempOtherFactorList.get(i);
			if(isMatchThirdUnderlinePart(otherFactorId)){
				Object[] obj = getMatchUnderlineData(otherFactorId);
				if(obj == null){
					log.error("匹配部位因素错误");
					continue;
				}
				String part = (String)obj[0];
				switch(type){
					case DISEASE: 
					case SUBDISEASE: 
						getPartOtherFactor(part,resultMap,otherFactorId,"ZD");
						break;
					case MAINOPR: 
					case UNMAINOPR: 
						getPartOtherFactor(part,resultMap,otherFactorId,"SS");
						break;
				}
			}else{
				Object[] lastUnderlineDiseaseFactor = getMatchLastUnderlineDiseaseFactor(otherFactorId);
				switch(type){
					case DISEASE: 
						if(lastUnderlineDiseaseFactor.equals("QTZD")){
							break;
						}
						resultMap.put(otherFactorId, 0);
						break;
					case SUBDISEASE: 
						if(lastUnderlineDiseaseFactor.equals("ZZD")){
							break;
						}
						resultMap.put(otherFactorId, 0);
						break;
					case MAINOPR: 
					case UNMAINOPR: 
						if(lastUnderlineDiseaseFactor.equals("QTZD")||lastUnderlineDiseaseFactor.equals("ZZD")){
							break;
						}
						resultMap.put(otherFactorId, 0);
						break;
				}
			}
		}
		
		return resultMap;
	}
	
	private void getPartOtherFactor(String part,HashMap<String, Integer> resultMap, String otherFactorId,String partType) {
		int count = 0;
		if(part.equals(partType)){
			if(resultMap.containsKey(otherFactorId)){
				count = resultMap.get(otherFactorId);
			}
			resultMap.put(otherFactorId, count+1);
		}
	}

	private Object[] getMatchLastUnderlineDiseaseFactor(String content){
		Object[] obj = new Object[2];
		obj[0] = null;
		String regEx = ".*_(ZD|ZZD)";
		Pattern pattern = Pattern.compile(regEx);
 	    Matcher isDisease = pattern.matcher(content);
 	    if(isDisease.find()){
 	    	obj[0] = isDisease.group(0);
 	    	obj[1] = isDisease.group(1);
 	    }
 	    return obj;
	}
	
	private boolean isMatchThirdUnderlinePart(String content){
		String regEx = ".*_.*_[0-9]*_BW";
		boolean isMatch = Pattern.matches(regEx, content);
		return isMatch;
	}
	
	private Object[] getMatchUnderlineData(String content){
		Object[] obj = new Object[2];
		String regEx = ".*_(.*)_(.*)_BW";
		Pattern pattern = Pattern.compile(regEx);
 	    Matcher isNum = pattern.matcher(content);
 	    if(isNum.find()){
 	    	obj[0] = isNum.group(1);
 		    obj[1] = isNum.group(2);
 	    }
 	    return obj;
	}
	
	private boolean isMatchFirstUnderlineItem(String content){
		String regEx = ".*_XM";
		boolean isMatch = Pattern.matches(regEx, content);
		return isMatch;
	}

	private List<String> initOtherFactorListFromSet(Set<String> otherFactorSet) {
		List<String> otherFactorList = new ArrayList<String>(otherFactorSet);
		return otherFactorList;
	}

	private void sortListByAsc(List<String> otherFactorList) {
		Collections.sort(otherFactorList, new Comparator<String>() {
		    @Override
		    public int compare(String o1, String o2) {
		        return ComparisonChain.start()
		                .compare(o1, o2)
		                .result();
		    }
		});
	}

	private void resetOtherFactorListSize(List<String> otherFactorList) {
		if(otherFactorList.size() > 3){
			for(int n = 3; n<otherFactorList.size();n++){
				otherFactorList.remove(n);
				n--;
			}
		}else{
			int count = 3 - otherFactorList.size();
			//其他因素列表size小于3,则用null补齐三位
			for(int i = 0; i < count; i++){
				otherFactorList.add(null);
			}
		}
	}
	
	private void setOtherFactorOfIntermediateResult(String adrg,List<String> otherFactorList) {
		AdrgFactor adrgFactor = new AdrgFactor();
		adrgFactor.setAdrg(adrg);
		adrgFactor.setFactorList(otherFactorList);
		drgsData.getIntermediateResult().getOtherFactors().add(adrgFactor);
	}
}
