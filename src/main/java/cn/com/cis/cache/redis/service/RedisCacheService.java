package cn.com.cis.cache.redis.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import cn.com.cis.cache.DataCache;
import cn.com.cis.common.utils.StringUtil;
import cn.com.cis.module.drgsgroup.dao.COMMTbDrgsgroupMapper;
import cn.com.cis.module.drgsgroup.dao.DiseaseOprExceptionMapper;
import cn.com.cis.module.drgsgroup.dao.SecondaryDiseaseMapper;
import cn.com.cis.module.drgsgroup.dao.TbDfxzycsMapper;
import cn.com.cis.module.drgsgroup.dao.TbDisdefaultGroupMapper;
import cn.com.cis.module.drgsgroup.dao.TbDisgroupMapper;
import cn.com.cis.module.drgsgroup.dao.TbHbzCcMapper;
import cn.com.cis.module.drgsgroup.dao.TbHbzChargeMccMapper;
import cn.com.cis.module.drgsgroup.dao.TbHbzExceptionMapper;
import cn.com.cis.module.drgsgroup.dao.TbHbzMccMapper;
import cn.com.cis.module.drgsgroup.dao.TbMainoprFirstMapper;
import cn.com.cis.module.drgsgroup.dao.TbMdcOprMapper;
import cn.com.cis.module.drgsgroup.dao.TbOprChargeMapper;
import cn.com.cis.module.drgsgroup.dao.TbOprgroupMapper;
import cn.com.cis.module.drgsgroup.dao.TbOtherFactorMapper;
import cn.com.cis.module.drgsgroup.entity.COMMTbDrgsgroup;
import cn.com.cis.module.drgsgroup.entity.DiseaseOprException;
import cn.com.cis.module.drgsgroup.entity.SecondaryDisease;
import cn.com.cis.module.drgsgroup.entity.TbDfxzycs;
import cn.com.cis.module.drgsgroup.entity.TbDisdefaultGroup;
import cn.com.cis.module.drgsgroup.entity.TbDisgroup;
import cn.com.cis.module.drgsgroup.entity.TbHbzCc;
import cn.com.cis.module.drgsgroup.entity.TbHbzChargeMcc;
import cn.com.cis.module.drgsgroup.entity.TbHbzException;
import cn.com.cis.module.drgsgroup.entity.TbHbzMcc;
import cn.com.cis.module.drgsgroup.entity.TbMainoprFirst;
import cn.com.cis.module.drgsgroup.entity.TbMdcOpr;
import cn.com.cis.module.drgsgroup.entity.TbOprCharge;
import cn.com.cis.module.drgsgroup.entity.TbOprgroup;
import cn.com.cis.module.drgsgroup.entity.TbOtherFactor;
import cn.com.cis.utils.GetMethodAndValue;

@Service
public class RedisCacheService {
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private TbDisgroupMapper tbDisgroupMapper;
	@Autowired
	private TbHbzChargeMccMapper tbHbzChargeMccMapper;
	@Autowired
	private TbHbzMccMapper tbHbzMccMapper;
	@Autowired
	private TbHbzCcMapper tbHbzCcMapper;
	@Autowired
	private TbHbzExceptionMapper tbHbzExceptionMapper;
	@Autowired
	private TbOprChargeMapper tbOprChargeMapper;
	@Autowired
	private TbMainoprFirstMapper tbMainoprFirstMapper;
	@Autowired
	private TbOprgroupMapper tbOprgroupMapper;
	@Autowired
	private TbMdcOprMapper tbMdcOprMapper;
	@Autowired
	private TbOtherFactorMapper tbOtherFactorMapper;
	@Autowired
	private COMMTbDrgsgroupMapper cOMMTbDrgsgroupMapper;
	@Autowired
	private TbDisdefaultGroupMapper tbDisdefaultGroupMapper;
	@Autowired
	private TbDfxzycsMapper tbDfxzycsMapper;
	@Autowired
	private SecondaryDiseaseMapper secondaryDiseaseMapper;
	@Autowired
	private DiseaseOprExceptionMapper diseaseOprExceptionMapper;
	
	public void initAllData() throws Exception{
		RedisCacheService redisCacheServiceProxy = (RedisCacheService)AopContext.currentProxy();
		//请缓存
		redisCacheServiceProxy.clearAllCache();
		//1.获取诊断adrg映射表,多个adrg用"#"连接,与查询到的顺序相反
		redisCacheServiceProxy.initDisgroupMap();
		//2.获取呼吸机合并症表
		redisCacheServiceProxy.initHbzChargeMccSet();
		//3.获取严重合并症表
		redisCacheServiceProxy.initHbzMccSet();
		//4.获取普通合并症表
		redisCacheServiceProxy.initHbzCcSet();
		//5.获取合并症排除表  此表拥有两个字段，把两个字段(并病症类型+"#"+从诊断code)拼起来存入
		redisCacheServiceProxy.initHbzExceptionSet();
		//6.获取手术过滤表
		redisCacheServiceProxy.initOprFilter();
		//7.获取主手术过滤表
		redisCacheServiceProxy.initMainoprFirstSet();
		//8.通过手术编码，获取所有对应的adrg,多个adrg用"#"连接,与查询到的顺序相反
		redisCacheServiceProxy.initOprgroupMap();
		//9.(诊断和手术的)adrg映射mdcCode
		redisCacheServiceProxy.initMdcMap();
		//10.其他因素表
		redisCacheServiceProxy.initOtherFactorMap();
		//11.adrg匹配出drgs表
		redisCacheServiceProxy.initDrgsgroupMap();
		//12.adrg排序表
		redisCacheServiceProxy.initDisdefaultGroupSet();
		//13.默认adrg选取
		redisCacheServiceProxy.initDefaultAdrgMap();
		//14.多发性重要创伤表
		redisCacheServiceProxy.initDfxcsMap();
		//15.次要诊断替换主诊断表
		redisCacheServiceProxy.initSecondaryDisease();
		//16.主手术排除表
		redisCacheServiceProxy.initMainOprExceptionSet();
	}
	
	@SuppressWarnings("unchecked")
	public String clearAllCache(){
		return redisTemplate.execute(new RedisCallback() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushDb();
                return "ok";
            }
        });
	}
	
	public void initDisgroupMap(){
		List<TbDisgroup> disgroupList = tbDisgroupMapper.selectAllTbDisgroups();
		//放入诊断对应的adrg编码
		for(TbDisgroup tempTbDisgroup : disgroupList){
			String icdCode = tempTbDisgroup.getIcdCode().toUpperCase();
			String value = tempTbDisgroup.getOprgroupCode();
			if(redisTemplate.opsForHash().hasKey(DataCache.DISGROUPMAP_KEY, icdCode)){
				value += "#" + redisTemplate.opsForHash().get(DataCache.DISGROUPMAP_KEY, icdCode);
			}
			redisTemplate.opsForHash().put(DataCache.DISGROUPMAP_KEY, icdCode, value);
		}
		disgroupList.clear();
	}
	
	public void initHbzChargeMccSet(){
		List<TbHbzChargeMcc> hbzChargeMccList = tbHbzChargeMccMapper.selectAllTbHbzChargeMccs();
		for(TbHbzChargeMcc tempTbHbzChargeMcc : hbzChargeMccList){
			redisTemplate.opsForSet().add(DataCache.HBZ_CHARGE_MCC_SET, tempTbHbzChargeMcc.getClientCode());
		}
		hbzChargeMccList.clear();
	}
	
	public void initHbzMccSet(){
		List<TbHbzMcc> hbzMccList = tbHbzMccMapper.selectTbHbzMcc();
		for(TbHbzMcc tempTbHbzMcc : hbzMccList){
			redisTemplate.opsForSet().add(DataCache.HBZ_MCC_SET,tempTbHbzMcc.getIcd10Code().toUpperCase());
		}
		hbzMccList.clear();
	}
	
	public void initHbzCcSet(){
		List<TbHbzCc> hbzCcList = tbHbzCcMapper.selectTbHbzCc();
		for(TbHbzCc tempTbHbzCc : hbzCcList){
			redisTemplate.opsForSet().add(DataCache.HBZ_CC_SET, tempTbHbzCc.getIcd10Code().toUpperCase());
		}
		hbzCcList.clear();
	}
	
	public void initHbzExceptionSet(){
		HashSet<String> diseaseSet = new HashSet<String>();
		diseaseSet.add("LC0.000");
		diseaseSet.add("LC0.FJ0");
		diseaseSet.add("BZ0.FJ0");
		List<TbHbzException> hbzExceptionList = tbHbzExceptionMapper.selectTbHbzException();
		for(TbHbzException tempTbHbzException : hbzExceptionList){
			String type = tempTbHbzException.getExceptType();
			String icd10 = tempTbHbzException.getDiseaseIcd10Id();
			if(StringUtil.isEmpty(type) && diseaseSet.contains(icd10)){
				redisTemplate.opsForSet().remove(DataCache.HBZ_EXCEPTION_SUBDISEASE_SET, tempTbHbzException.getExceptIcd10Id());
			}else if(!StringUtil.isEmpty(type)){
				redisTemplate.opsForSet().remove(DataCache.HBZ_EXCEPTION_SET, tempTbHbzException.getDiseaseIcd10Id()+"#"+tempTbHbzException.getExceptType()
						+"#"+tempTbHbzException.getExceptIcd10Id().toUpperCase());
			}
		}
		hbzExceptionList.clear();
	}
	
	public void destoryHbzExceptionSet(){
		HashSet<String> diseaseSet = new HashSet<String>();
		diseaseSet.add("LC0.000");
		diseaseSet.add("LC0.FJ0");
		diseaseSet.add("BZ0.FJ0");
		List<TbHbzException> hbzExceptionList = tbHbzExceptionMapper.selectTbHbzException();
		for(TbHbzException tempTbHbzException : hbzExceptionList){
			String type = tempTbHbzException.getExceptType();
			String icd10 = tempTbHbzException.getDiseaseIcd10Id();
			if(StringUtil.isEmpty(type) && diseaseSet.contains(icd10)){
				redisTemplate.opsForSet().add(DataCache.HBZ_EXCEPTION_SUBDISEASE_SET, tempTbHbzException.getExceptIcd10Id());
			}else if(!StringUtil.isEmpty(type)){
				redisTemplate.opsForSet().add(DataCache.HBZ_EXCEPTION_SET, tempTbHbzException.getDiseaseIcd10Id()+"#"+tempTbHbzException.getExceptType()
						+"#"+tempTbHbzException.getExceptIcd10Id().toUpperCase());
			}
		}
		hbzExceptionList.clear();
	}
	
	@SuppressWarnings("unchecked")
	public void initOprFilter() throws IOException{
		List<TbOprCharge> oprChargeList = tbOprChargeMapper.selectTbOprCharge();
		
		/**
		 * 遍历手术表
		 * 1 将itemId#icd10Code相同的手术存入oprChargeMap,key是itemId#icd10Code,value是手术列表
		 * 2 统计各个seqId的数量,存入seqIdCountMap,key是seqId,value是数量
		 */
		for(TbOprCharge tempTbOprCharge : oprChargeList){
			String tempKey = null;
			if(StringUtil.isEmpty(tempTbOprCharge.getIcd10Code())){
				tempKey = tempTbOprCharge.getItemId() + "#" + tempTbOprCharge.getIcd10Code();
			}else{
				tempKey = tempTbOprCharge.getItemId() + "#" + tempTbOprCharge.getIcd10Code().toUpperCase();
			}
			List<TbOprCharge> valueList = new ArrayList<TbOprCharge>();
			if(redisTemplate.opsForHash().hasKey(DataCache.OPR_CHARGE_MAP, tempKey)){
				valueList = (List<TbOprCharge>) redisTemplate.opsForHash().get(DataCache.OPR_CHARGE_MAP, tempKey);
			}
			valueList.add(tempTbOprCharge);
			redisTemplate.opsForHash().put(DataCache.OPR_CHARGE_MAP, tempKey, valueList);
			//保存按seqId分组统计条数的Map
			int count = 0;
			String tempSeqId = tempTbOprCharge.getSeqId();
			if(redisTemplate.opsForHash().hasKey(DataCache.SEQ_COUNT_MAP, tempSeqId)){
				count = Integer.parseInt(redisTemplate.opsForHash().get(DataCache.SEQ_COUNT_MAP, tempSeqId).toString());
			}
			redisTemplate.opsForHash().put(DataCache.SEQ_COUNT_MAP, tempSeqId, count+1);
		}
		
		//初始化父子手术关系缓存 paternityOprMap------start-----------
		/**
		 * 遍历手术列表,按照seqId数量分组.将分组结果存入tempPaternityOprMap,key是seqId数量,value是Map<SeqId,手术对象>
		 */
		HashMap<Integer,HashMap<String,TbOprCharge>> tempPaternityOprMap = new HashMap<Integer,HashMap<String,TbOprCharge>>();
		for(TbOprCharge tbOprCharge : oprChargeList){//从手术列表遍历
			Object countTemp = redisTemplate.opsForHash().get(DataCache.SEQ_COUNT_MAP, tbOprCharge.getSeqId());
			int seqCount = countTemp == null ? null : (int)countTemp;
			HashMap<String,TbOprCharge> map= new HashMap<String,TbOprCharge>();
			HashSet<String> itemIdSet = new HashSet<String>();//相同seqId包含的itemId的集合
			if(tempPaternityOprMap.containsKey(seqCount)){
				map = tempPaternityOprMap.get(seqCount);
				if(map.containsKey(tbOprCharge.getSeqId())){
					itemIdSet = map.get(tbOprCharge.getSeqId()).getItemIdSet();
				}
			}
			itemIdSet.add(tbOprCharge.getItemId());
			tbOprCharge.setItemIdSet(itemIdSet);
			map.put(tbOprCharge.getSeqId(), tbOprCharge);
			tempPaternityOprMap.put(seqCount, map);
		}
		
		/**
		 * 1 遍历tempPaternityOprMap,将比当前手术对应的服务项数量少的或者相等的手术存入tempTotalMap,key是seqId,value是手术对象。
		 * 2 遍历tempTotalMap,将当前手术对应的服务项的子集对应的手术存入paternityOprMap,key是父手术对应的seqId,value是子手术对应的seqId集合
		 */
		for(Map.Entry<Integer, HashMap<String,TbOprCharge>> map : tempPaternityOprMap.entrySet()){
			int count = map.getKey();
			for(String tempSeqId :  tempPaternityOprMap.get(count).keySet()){
				String icd10 = "";
				if(!StringUtil.isEmpty(tempPaternityOprMap.get(count).get(tempSeqId).getIcd10Code())){
					icd10 = tempPaternityOprMap.get(count).get(tempSeqId).getIcd10Code().toUpperCase();
				}
				HashMap<String,TbOprCharge> tempTotalMap = new HashMap<String,TbOprCharge>();
				int selectCount = count;//遍历数量级
				if(StringUtil.isEmpty(icd10)){//若icd10为空，则此条不能作为count相同的seq的父手术，应从比他数量小的手术开始遍历
					selectCount = selectCount - 1;
				}
				for(int i = selectCount; i > 0; i--){//从相同和数量小的手术中取出所有手术对象
					tempTotalMap.putAll(tempPaternityOprMap.get(i));
				}
				TbOprCharge tbOprCharge = tempPaternityOprMap.get(count).get(tempSeqId);//取出当前手术
				for(String tarSeqId : tempTotalMap.keySet()){//遍历数量小于等于当前手术的手术集合
					if(tempSeqId.equals(tarSeqId)){
						continue;
					}else{
						boolean containsFlag = tbOprCharge.getItemIdSet().containsAll(tempTotalMap.get(tarSeqId).getItemIdSet());//比较当前手术的itemId是否完全包含遍历的手术的itemId
						if(containsFlag){//若完全包含
							List<String> list = new ArrayList<String>();
							if(tbOprCharge.getItemIdSet().size() == tempTotalMap.get(tarSeqId).getItemIdSet().size()){//数量等于遍历的手术
								if(StringUtil.isEmpty(tempTotalMap.get(tarSeqId).getIcd10Code())){//当被包含的手术icd为空时，则此手术为当前手术的子手术
									if(redisTemplate.opsForHash().hasKey(DataCache.PATERNITY_OPR_MAP, tempSeqId)){
										list = (List<String>) redisTemplate.opsForHash().get(DataCache.PATERNITY_OPR_MAP, tempSeqId);
									}
									list.add(tarSeqId);
								    redisTemplate.opsForHash().put(DataCache.PATERNITY_OPR_MAP, tempSeqId, list);
								}
							}else{//数量大于遍历的手术
								if(redisTemplate.opsForHash().hasKey(DataCache.PATERNITY_OPR_MAP, tempSeqId)){
									list = (List<String>) redisTemplate.opsForHash().get(DataCache.PATERNITY_OPR_MAP, tempSeqId);
								}
								list.add(tarSeqId);
								redisTemplate.opsForHash().put(DataCache.PATERNITY_OPR_MAP, tempSeqId, list);
							}
						}
					}
				}
			}
		}
		//初始化父子手术关系缓存 paternityOprMap------end-----------
		
		tempPaternityOprMap.clear();
		oprChargeList.clear();
	}
	
	public void initMainoprFirstSet(){
		List<TbMainoprFirst> mainoprFirstList = tbMainoprFirstMapper.selectAllTbMainoprFirsts();
		for(TbMainoprFirst tempTbMainoprFirst : mainoprFirstList){
			redisTemplate.opsForSet().add(DataCache.MAIN_OPR_FIRST_SET, (tempTbMainoprFirst.getIcd9Code()
					+"#"+tempTbMainoprFirst.getIcdCode()).toUpperCase());
		}
		mainoprFirstList.clear();
	}
	
	public void initOprgroupMap(){
		List<TbOprgroup> oprgroupList =  tbOprgroupMapper.selectTbOprgroup();
		for(TbOprgroup tempTbOprgroup : oprgroupList){
			String icd9Code = tempTbOprgroup.getIcd9Code().toUpperCase();
			String value = tempTbOprgroup.getOprgroupCode();
			
			if(redisTemplate.opsForHash().hasKey(DataCache.OPR_GROUP_MAP, icd9Code)){
				value += "#" + redisTemplate.opsForHash().get(DataCache.OPR_GROUP_MAP, icd9Code);
			}
			redisTemplate.opsForHash().put(DataCache.OPR_GROUP_MAP,icd9Code, value);
		}
		oprgroupList.clear();
	}
	
	public void initMdcMap(){
		List<TbMdcOpr> mdcList = tbMdcOprMapper.selectAllTbMdcOprs();
		for(TbMdcOpr tempMdcOpr : mdcList){
			String adrg = tempMdcOpr.getOprgroupCode().toUpperCase();
			String value = tempMdcOpr.getIcdMdcType();
			if(redisTemplate.opsForHash().hasKey(DataCache.MDC_MAP, adrg)){
				value += "#" + redisTemplate.opsForHash().get(DataCache.MDC_MAP, adrg);
			}
			redisTemplate.opsForHash().put(DataCache.MDC_MAP, adrg, value);
		}
		mdcList.clear();
	}
	
	public void initOtherFactorMap(){
		List<TbOtherFactor> otherFactorList = tbOtherFactorMapper.selectAllTbOtherFactors();
		for(TbOtherFactor tempOtherFactor : otherFactorList){
			String icdCode = tempOtherFactor.getFactorItemId();
			String adrgCode = tempOtherFactor.getOprgroupCode();
			String value = tempOtherFactor.getOtherFactorId();
			String key = (icdCode + "#" + adrgCode).toUpperCase();
			if(redisTemplate.opsForHash().hasKey(DataCache.OTHER_FACTOR_MAP, key)){
				value += "#" +redisTemplate.opsForHash().get(DataCache.OTHER_FACTOR_MAP, key);
			}
			redisTemplate.opsForHash().put(DataCache.OTHER_FACTOR_MAP, key, value);
		}
		otherFactorList.clear();
	}
	
	public void initDrgsgroupMap(){
		HashSet<String> usedFieldSet = new HashSet<String>();
		usedFieldSet.add("ccFlag");
		usedFieldSet.add("otherFactorId1");
		usedFieldSet.add("otherFactorId2");
		usedFieldSet.add("otherFactorId3");
		usedFieldSet.add("drgsSex");
		usedFieldSet.add("drgsAge");
		usedFieldSet.add("drgsWeight");
		usedFieldSet.add("dfxzycsFlag");
		usedFieldSet.add("drgsOutcome");
		usedFieldSet.add("drgsTrtime");
		List<COMMTbDrgsgroup> drgsGroupList = cOMMTbDrgsgroupMapper.selectCOMMTbDrgsgroup();
		for(COMMTbDrgsgroup tempDrgsGroup : drgsGroupList){
			String[] objectFields= GetMethodAndValue.getFiledName(tempDrgsGroup);
			String drgCnCode = tempDrgsGroup.getDrgCnCode();
			List<String> factorList = new ArrayList<String>();
			for(String fieldName : objectFields){
				if(!usedFieldSet.contains(fieldName)){
					continue;
				}
				String tempvalue = (String)GetMethodAndValue.getFieldValueByName(fieldName, tempDrgsGroup);
				if(fieldName.indexOf("otherFactorId")==-1){
					if(StringUtil.isEmpty(tempvalue)){
						continue;
					}
					String[] values = tempvalue.replace("(", "").replace(")", "").replace("（", "").replace("）", "").replace("，", ",").split(",");
					for(String value : values){
						String tempKey = fieldName+"#"+value;
						putDrgsGroupMatchMap(drgCnCode, tempKey, tempDrgsGroup.getOprgroupCode());
					}
				}else{//三个factor
					if(!StringUtil.isEmpty(tempvalue)){
						factorList.add(tempvalue);
					}
				}
			}
			
			//向map中添加factor匹配项
			String factor = "";
			String factorForTwo = "";
			String factorForOne = "";
			if(factorList.size() == 0){
				//3
				factor = "otherFactor#null#null#null";
				putDrgsGroupMatchMap(drgCnCode, factor , tempDrgsGroup.getOprgroupCode());
				//2
				factorForTwo = "otherFactorForTwo#null#null";
				putDrgsGroupMatchMap(drgCnCode, factorForTwo , tempDrgsGroup.getOprgroupCode());
				//1
				factorForOne = "otherFactorForOne#null";
				putDrgsGroupMatchMap(drgCnCode, factorForOne , tempDrgsGroup.getOprgroupCode());
			}else if (factorList.size() == 1){
				//3
				factor = "otherFactor#"+factorList.get(0)+ "#null#null";
				putDrgsGroupMatchMap(drgCnCode, factor , tempDrgsGroup.getOprgroupCode());
				//2
				factorForTwo = "otherFactorForTwo#null#null";
				putDrgsGroupMatchMap(drgCnCode, factorForTwo , tempDrgsGroup.getOprgroupCode());
				factorForTwo = "otherFactorForTwo#"+factorList.get(0) + "#null";
				putDrgsGroupMatchMap(drgCnCode, factorForTwo , tempDrgsGroup.getOprgroupCode());
				//1
				factorForOne = "otherFactorForOne#"+factorList.get(0);
				putDrgsGroupMatchMap(drgCnCode, factorForOne , tempDrgsGroup.getOprgroupCode());
				factorForOne = "otherFactorForOne#null";
				putDrgsGroupMatchMap(drgCnCode, factorForOne, tempDrgsGroup.getOprgroupCode());
			}else if (factorList.size() == 2){
				//3
				factor = "otherFactor#"+factorList.get(0)+"#"+factorList.get(1)+"#null";
				putDrgsGroupMatchMap(drgCnCode, factor, tempDrgsGroup.getOprgroupCode());
				factor = "otherFactor#"+factorList.get(1)+"#"+factorList.get(0)+"#null";
				putDrgsGroupMatchMap(drgCnCode, factor, tempDrgsGroup.getOprgroupCode());
				//2
				factorForTwo = "otherFactorForTwo#"+factorList.get(0)+"#"+factorList.get(1);
				putDrgsGroupMatchMap(drgCnCode, factorForTwo, tempDrgsGroup.getOprgroupCode());
				factorForTwo = "otherFactorForTwo#"+factorList.get(1)+"#"+factorList.get(0);
				putDrgsGroupMatchMap(drgCnCode, factorForTwo, tempDrgsGroup.getOprgroupCode());
				factorForTwo = "otherFactorForTwo#"+factorList.get(0)+"#null";
				putDrgsGroupMatchMap(drgCnCode, factorForTwo, tempDrgsGroup.getOprgroupCode());
				factorForTwo = "otherFactorForTwo#"+factorList.get(1)+"#null";
				putDrgsGroupMatchMap(drgCnCode, factorForTwo, tempDrgsGroup.getOprgroupCode());
				//1
				factorForOne = "otherFactorForOne#"+factorList.get(0);
				putDrgsGroupMatchMap(drgCnCode, factorForOne, tempDrgsGroup.getOprgroupCode());
				factorForOne = "otherFactorForOne#"+factorList.get(1);
				putDrgsGroupMatchMap(drgCnCode, factorForOne, tempDrgsGroup.getOprgroupCode());
				factorForOne = "otherFactorForOne#null";
				putDrgsGroupMatchMap(drgCnCode, factorForOne, tempDrgsGroup.getOprgroupCode());
			}else{//factorList.size() == 3
				//3
				factor = "otherFactor#"+factorList.get(0)+"#"+factorList.get(1)+"#"+factorList.get(2);
				putDrgsGroupMatchMap(drgCnCode, factor, tempDrgsGroup.getOprgroupCode());
				factor = "otherFactor#"+factorList.get(0)+"#"+factorList.get(2)+"#"+factorList.get(1);
				putDrgsGroupMatchMap(drgCnCode, factor, tempDrgsGroup.getOprgroupCode());
				factor = "otherFactor#"+factorList.get(1)+"#"+factorList.get(0)+"#"+factorList.get(2);
				putDrgsGroupMatchMap(drgCnCode, factor, tempDrgsGroup.getOprgroupCode());
				factor = "otherFactor#"+factorList.get(1)+"#"+factorList.get(2)+"#"+factorList.get(0);
				putDrgsGroupMatchMap(drgCnCode, factor, tempDrgsGroup.getOprgroupCode());
				factor = "otherFactor#"+factorList.get(2)+"#"+factorList.get(0)+"#"+factorList.get(1);
				putDrgsGroupMatchMap(drgCnCode, factor, tempDrgsGroup.getOprgroupCode());
				factor = "otherFactor#"+factorList.get(2)+"#"+factorList.get(1)+"#"+factorList.get(0);
				putDrgsGroupMatchMap(drgCnCode, factor, tempDrgsGroup.getOprgroupCode());
				//2
				factorForTwo = "otherFactorForTwo#"+factorList.get(0)+"#"+factorList.get(1);
				putDrgsGroupMatchMap(drgCnCode, factorForTwo, tempDrgsGroup.getOprgroupCode());
				factorForTwo = "otherFactorForTwo#"+factorList.get(0)+"#"+factorList.get(2);
				putDrgsGroupMatchMap(drgCnCode, factorForTwo, tempDrgsGroup.getOprgroupCode());
				factorForTwo = "otherFactorForTwo#"+factorList.get(1)+"#"+factorList.get(0);
				putDrgsGroupMatchMap(drgCnCode, factorForTwo, tempDrgsGroup.getOprgroupCode());
				factorForTwo = "otherFactorForTwo#"+factorList.get(1)+"#"+factorList.get(2);
				putDrgsGroupMatchMap(drgCnCode, factorForTwo, tempDrgsGroup.getOprgroupCode());
				factorForTwo = "otherFactorForTwo#"+factorList.get(2)+"#"+factorList.get(0);
				putDrgsGroupMatchMap(drgCnCode, factorForTwo, tempDrgsGroup.getOprgroupCode());
				factorForTwo = "otherFactorForTwo#"+factorList.get(2)+"#"+factorList.get(1);
				putDrgsGroupMatchMap(drgCnCode, factorForTwo, tempDrgsGroup.getOprgroupCode());
				//1
				factorForOne = "otherFactorForOne#"+factorList.get(0);
				putDrgsGroupMatchMap(drgCnCode, factorForOne, tempDrgsGroup.getOprgroupCode());
				factorForOne = "otherFactorForOne#"+factorList.get(1);
				putDrgsGroupMatchMap(drgCnCode, factorForOne, tempDrgsGroup.getOprgroupCode());
				factorForOne = "otherFactorForOne#"+factorList.get(2);
				putDrgsGroupMatchMap(drgCnCode, factorForOne, tempDrgsGroup.getOprgroupCode());
			}
			redisTemplate.opsForHash().put(DataCache.DRGS_GROUP_MAP, tempDrgsGroup.getDrgCnCode().toUpperCase(), tempDrgsGroup);
		}
		drgsGroupList.clear();
	}

	@SuppressWarnings("unchecked")
	private void putDrgsGroupMatchMap(String drgCnCode,String factor,String adrg) {
		HashSet<String> tempSet = new HashSet<String>();
		String key = (adrg.toUpperCase() + "#" + factor).toUpperCase();
		if(redisTemplate.opsForHash().hasKey(DataCache.DRGS_GROUP_MATCH_MAP, key)){
			tempSet = (HashSet<String>) redisTemplate.opsForHash().get(DataCache.DRGS_GROUP_MATCH_MAP, key);
		}
		tempSet.add(drgCnCode);
		redisTemplate.opsForHash().put(DataCache.DRGS_GROUP_MATCH_MAP, key, tempSet);
	}
	
	public void initDisdefaultGroupSet(){
		List<TbDisdefaultGroup> disdefaultGroupList = tbDisdefaultGroupMapper.selectTbDisdefaultGroup();
		for(TbDisdefaultGroup tempDisdefaultGroup : disdefaultGroupList){
			String value = (tempDisdefaultGroup.getIcd10().toUpperCase() + "#" + tempDisdefaultGroup.getOprgroupCode()).toUpperCase();
			redisTemplate.opsForSet().add(DataCache.DIS_DEFAULT_GROUP_SET, value);
		}
		disdefaultGroupList.clear();
	}
	
	public void initDefaultAdrgMap(){
		List<TbMainoprFirst> mainoprFirstList = tbMainoprFirstMapper.selectAllDefaultMainoprFirsts();
		String icd9 = "";
		String icd10 = "";
		for(TbMainoprFirst tempTbMainoprFirst : mainoprFirstList){
			icd9 = tempTbMainoprFirst.getIcd9Code();
			icd10 = tempTbMainoprFirst.getIcdCode();
			redisTemplate.opsForHash().put(DataCache.DEFAULT_ADRG_MAP, (icd9+"#"+icd10).toUpperCase(), tempTbMainoprFirst.getOprgroupCode());
		}
		mainoprFirstList.clear();
	}
	
	@SuppressWarnings("unchecked")
	public void initDfxcsMap(){
		List<TbDfxzycs> dfxzycsList = tbDfxzycsMapper.selectAllTbDfxzycs();
		for(TbDfxzycs tempTbDfxzycs : dfxzycsList){
			HashSet<TbDfxzycs> dfxzycsSet = new HashSet<TbDfxzycs>();
			String icd10 = tempTbDfxzycs.getIcd10().toUpperCase();
			if(redisTemplate.opsForHash().hasKey(DataCache.DFXZYCS_MAP, icd10)){
				dfxzycsSet = (HashSet<TbDfxzycs>) redisTemplate.opsForHash().get(DataCache.DFXZYCS_MAP, icd10);
			}
			dfxzycsSet.add(tempTbDfxzycs);
			redisTemplate.opsForHash().put(DataCache.DFXZYCS_MAP, icd10, dfxzycsSet);
		}
		dfxzycsList.clear();
	}
	
	public void initSecondaryDisease() {
		List<SecondaryDisease> secondaryDiseases = secondaryDiseaseMapper.getAllSecondaryDiseases();
		for(SecondaryDisease secondaryDisease : secondaryDiseases){
			redisTemplate.opsForHash().put(DataCache.SECONDARY_DISEASE_MAP, secondaryDisease.getIcdCode().toUpperCase(), secondaryDisease);
		}
	}
	
	public void initMainOprExceptionSet(){
		List<DiseaseOprException> diseaseOprExceptionList = diseaseOprExceptionMapper.selectDiseaseOprException();
		for(DiseaseOprException tempDiseaseOprException : diseaseOprExceptionList){
			String icd9 = tempDiseaseOprException.getIcd9Code().toUpperCase();
			String icd10 = tempDiseaseOprException.getIcd10Code().toUpperCase();
			String value = (icd9+"#"+icd10).toUpperCase();
			redisTemplate.opsForSet().add(DataCache.MAIN_OPR_EXCEPTION_SET, value);
		}
		diseaseOprExceptionList.clear();
	}

}
