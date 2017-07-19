package cn.com.cis.cache;


public class DataCache {
	
	//诊断-adrg映射表
	public static final String DISGROUPMAP_KEY ="disgroupMap";
	//呼吸机映射表
	public static final String HBZ_CHARGE_MCC_SET = "hbzChargeMccSet";
	//严重合并症映射表
	public static final String HBZ_MCC_SET = "hbzMccSet";
	//普通合并症映射表
	public static final String HBZ_CC_SET = "hbzCcSet";
	//合并症排除表
	public static final String HBZ_EXCEPTION_SET = "hbzExceptionSet";
	public static final String HBZ_EXCEPTION_SUBDISEASE_SET = "hbzExceptionSubDiseaseSet";
	//过滤手术表
	public static final String OPR_CHARGE_MAP = "oprChargeMap";
	public static final String SEQ_COUNT_MAP = "seqIdCountMap";
	public static final String PATERNITY_OPR_MAP = "paternityOprMap";
	//过滤主手术表
	public static final String MAIN_OPR_FIRST_SET = "mainoprFirstSet";
	//手术对应adrg表
	public static final String OPR_GROUP_MAP = "oprgroupMap";
	//adrg对应mdc表
	public static final String MDC_MAP = "mdcMap";
	//其他因素表
	public static final String OTHER_FACTOR_MAP = "otherFactorMap";
	//adrg匹配表
	public static final String DRGS_GROUP_MATCH_MAP = "drgsgroupMatchMap";
	public static final String DRGS_GROUP_MAP = "drgsgroupMap";
	//过滤drgs结果表
	public static final String DIS_DEFAULT_GROUP_SET = "disdefaultGroupSet";
	//默认adrg
	public static final String DEFAULT_ADRG_MAP = "defaultAdrgMap";
	//多发性重要创伤表
	public static final String DFXZYCS_MAP = "dfxzycsMap";
	//次要诊断替换主诊断表
	public static final String SECONDARY_DISEASE_MAP = "secondaryDiseaseMap";
	//病例主手术排除表
	public static final String MAIN_OPR_EXCEPTION_SET = "mainOprExceptionSet";
}
