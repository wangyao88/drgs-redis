package cn.com.cis.module.drgsgroup.entity;

import java.io.Serializable;

import lombok.Data;

@Data
//TB_DISGROUP	诊断adrg映射表 ------>通过诊断编码（normalCode/icd10）获取诊断adrg
public class TbDisgroup implements Serializable{
	
	private static final long serialVersionUID = 5137847178960903505L;
	
	private String icdCode;//icd10 === 中公网诊断代码normalCode
	
	private String oprgroupCode;//诊断的adrg
	
}