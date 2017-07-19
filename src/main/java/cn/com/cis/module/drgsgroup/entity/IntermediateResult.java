package cn.com.cis.module.drgsgroup.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;
import cn.com.cis.enums.DrgType;

@Data
public class IntermediateResult {
	
	private String id;
	
	private String hisId;
	
	private Long groupNo;
	
	//手术对应的adrg 多个用逗号分隔
	private String mainOprAdrgs;
	
	//诊断对应的adrg 多个用逗号分隔
	private String diseaseAdrgs;
	
	//默认的adrg 多个用逗号分隔
	private String defaultAdrgs;
	
	//最终分组的adrg
	private String resultAdrg;
	
	//三条路径得出分组匹配用的adrgs
	private Set<String> adrgs = new HashSet<String>();
	
	//主诊断是否有adrg
	private boolean havingDiseaseAdrg = true;
	
	//主诊断编码
	//分组逻辑用的诊断编码
	private String icd10;
	
	//原主诊断
	private String originalIcd10;
	
	//附属诊断编码 多个用逗号分隔
	private String primaryDiagnosisCode;
	
	//主手术编码
	private String icd9;
	
	//是否有主手术 1-有 0-无
	private String isHasMainOpr;
	
	//主手术金额
	private float mainOprCosts;
	
	//主手术项目日期
	private Date mainOprDate;
	
	//主手术ICD10
	private String mainOprIcd10;
	
	//主诊断对应的mdc 多个用逗号分隔
	private String diseaseMdc;
	
	private String otherFactor1;
	
	private String otherFactor2;
	
	private String otherFactor3;
	
	private Integer ageType;
	
	private Integer weightType;
	
	private String sex;
	
	//合并症
	private String ccFlag;
	
	private int matchCount = 0;
	
	private Date uploadDate;
	
	private Date drgGroupDate;
	
	private String unMainOprStr;
	
	private DrgType drgType;
	
	private String drgCode;
	
	//多发性重要创伤标识
	private String dfxzycsFlag;
	
	//每个选出的adrg对应的3个其他因素
	private List<AdrgFactor> otherFactors = new ArrayList<AdrgFactor>();
	
	private DrgsGroupResult drgsGroupResult = new DrgsGroupResult();
	
	private List<Disease> subDisease = new ArrayList<Disease>();
}
