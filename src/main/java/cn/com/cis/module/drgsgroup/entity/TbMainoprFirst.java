package cn.com.cis.module.drgsgroup.entity;

import java.io.Serializable;

import lombok.Data;

@Data
//TB_MAINOPR_FIRST	主手术优先选取表---->从手术列表中，优先选取（通过itemId找出的icd9Code+icdCode）的手术，作为预备主手术

public class TbMainoprFirst implements Serializable{
	
	private static final long serialVersionUID = 2384670683641723177L;
	
	private String icdCode;//标准诊断编码
	
	private String icd9Code;//手术编码（从TB_OPR_CHARGE通过std_code(映射后的itemId)中获取手术编码）
	
	private String oprgroupCode;//默认adrg
	
}
