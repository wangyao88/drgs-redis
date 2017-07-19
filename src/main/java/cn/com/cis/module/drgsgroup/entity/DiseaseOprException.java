package cn.com.cis.module.drgsgroup.entity;

import lombok.Data;

/**
 * 主手术排除表 tb_disease_opr_exception
 */
@Data
public class DiseaseOprException {
	
	private String icd9Code;//手术编码
	
	private String icd10Code;//诊断编码（映射后的） 
}
